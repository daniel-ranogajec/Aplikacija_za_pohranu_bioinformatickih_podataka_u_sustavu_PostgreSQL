package postgres.database.search;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import postgres.database.tools.DatabaseConnection;
import postgres.database.tools.DownloadFastaFile;
import postgres.database.tools.FileReader;

/**
 * Program that recieves name of an organism in arguments. 
 * User can also type name of genome or plasmid in argument to specify which sequences he wants to get.
 * User can type download or -d as last argument to save those sequences in /user/downloads/.
 * User can type show or -s as last argument to show sequences in terminal.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class SearchEngine {

	/**
	 * Main program
	 * @param args first argument should be name of an organism. 
	 * You can add more arguments if you want to get specific genome or plasmid. 
	 * Write download or -d as last argument to download requested sequences. 
	 * Write show or -s as last argument to print requested sequences.
	 * Files will be saved in /user/downloads/.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please run this program with the name of an organism in arguments!");
			return;
		}

		List<String> userData = null;
		try {
			userData = DatabaseConnection.Connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		if (userData == null)
			return;
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){

			PreparedStatement pstmt = connection.prepareStatement("select * from names, reference_genomes where names.tax_id = reference_genomes.tax_id and lower(name_txt) = lower(?);");
			pstmt.setString(1, args[0]);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Map<String, List<String>> map = FileReader.readFasta(rs.getString("file_location"));
				int tax_id = rs.getInt("tax_id");

				output(tax_id, map, connection, args);

			} else {
				PreparedStatement pstmt2 = connection.prepareStatement("select * from names, reference_genomes where names.tax_id = reference_genomes.tax_id and lower(name_txt) like lower(?) and name_class = 'scientific name';");
				pstmt2.setString(1, "%" + args[0] + "%");
				ResultSet rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					System.out.println("Did you mean " + rs2.getString("name_txt") + "? [Y/N]");
					Scanner sc = new Scanner(System.in);
					if (sc.next().equalsIgnoreCase("Y")) {
						Map<String, List<String>> map = FileReader.readFasta(rs2.getString("file_location"));
						int tax_id = rs2.getInt("tax_id");

						output(tax_id, map, connection, args);
					}
					sc.close();
				} else {
					System.out.println("Couldn't find any results for given arguments.");
				}
			}


		} 	catch (SQLException e) {
			System.out.println("Connection failure.");
			e.printStackTrace();

			try {
				DatabaseConnection.WrongData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Private method that lists all headers for given arguments.
	 * If last argument is -d or download it calls download method.
	 * If last argument is -s or show it calls show method.
	 * @param tax_id
	 * @param map
	 * @param connection
	 * @param args
	 * @throws SQLException
	 */
	private static void output(int tax_id, Map<String, List<String>> map, Connection connection, String[] args) throws SQLException {
		if (args.length == 1) {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				System.out.println(entry.getKey());
			}
		} else {
			if (args[args.length-1].equalsIgnoreCase("download") || args[args.length-1].equalsIgnoreCase("-d")) {
				download(tax_id, map, connection, args);
			} else if (args[args.length-1].equalsIgnoreCase("show") || args[args.length-1].equalsIgnoreCase("-s")) {
				show(tax_id, map, connection, args);
			} else {
				List<String> headers = getHeaders(tax_id, connection, args);
				if (headers.size() == 0) {
					System.out.println("Couldn't find results for given arguments.");
					return;
				}
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					if (headers.contains(entry.getKey())) {
						System.out.println(entry.getKey());
					}
				}
			}
		}
	}

	/**
	 * Private method that returns all headers from database.
	 * @param tax_id
	 * @param connection
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private static List<String> getHeaders(int tax_id, Connection connection, String[] args) throws SQLException {
		List<String> headers = new ArrayList<>();
		PreparedStatement pstmt = connection.prepareStatement("select * from headers where tax_id = ? and lower(header) like lower(?);");
		for (int i = 1; i < args.length; i++) {
			pstmt.setInt(1, tax_id);
			pstmt.setString(2, "%" + args[i] + "%");
			ResultSet rs2 = pstmt.executeQuery();
			if (rs2.next()) {
				headers.add(rs2.getString("header"));
				while (rs2.next()) {
					headers.add(rs2.getString("header"));
				}
			} else {
				System.out.println("Couldn't find a sequence for argument: " + args[i]);
			}
		}	
		return headers;
	}

	/**
	 * Private method that downloads sequences and stores them in /user/dowloads/
	 * @param tax_id
	 * @param map
	 * @param connection
	 * @param args
	 * @throws SQLException
	 */
	private static void download(int tax_id, Map<String, List<String>> map, Connection connection, String[] args) throws SQLException {
		if (args.length > 2) {
			List<String> headers = getHeaders(tax_id, connection, Arrays.copyOfRange(args, 0, args.length-1));
			if (headers.size() == 0) {
				System.out.println("Couldn't find results for given arguments.");
				return;
			}
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				if (headers.contains(entry.getKey())) {
					DownloadFastaFile.download(entry.getKey().substring(1), entry.getValue());
				}
			}
		} else {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				DownloadFastaFile.download(entry.getKey().substring(1), entry.getValue());
			}
		}
	}
	
	/**
	 * Private method that prints header and sequence.
	 * @param tax_id
	 * @param map
	 * @param connection
	 * @param args
	 * @throws SQLException
	 */
	private static void show(int tax_id, Map<String, List<String>> map, Connection connection, String[] args) throws SQLException {
		if (args.length > 2) {
			List<String> headers = getHeaders(tax_id, connection, Arrays.copyOfRange(args, 0, args.length-1));
			if (headers.size() == 0) {
				System.out.println("Couldn't find results for given arguments.");
				return;
			}
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				if (headers.contains(entry.getKey())) {
					System.out.println(entry.getKey());
					entry.getValue().forEach(l -> System.out.println(l));
				}
			}
		} else {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				System.out.println(entry.getKey());
				entry.getValue().forEach(l -> System.out.println(l));
			}
		}
	}
}
