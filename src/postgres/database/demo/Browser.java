package postgres.database.demo;

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
 * User can type download or -d as last argument to save those sequences in /user/sequences.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class Browser {

	/**
	 * Main program
	 * @param args first argument should be name of an organism. 
	 * You can add more arguments if you want to get specific genome or plasmid. 
	 * Write download or -d as last argument to download requested sequences. 
	 * Files will be saved in /user/downloads/.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please run this program with name of organism in argument!");
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

			PreparedStatement pstmt = connection.prepareStatement("select * from names, reference_genomes where names.tax_id = reference_genomes.tax_id and name_txt = ?;");
			pstmt.setString(1, args[0]);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Map<String, List<String>> map = FileReader.readFasta(rs.getString("file_location"));
				int tax_id = rs.getInt("tax_id");

				output(tax_id, map, connection, args);

			} else {
				PreparedStatement pstmt2 = connection.prepareStatement("select * from names, reference_genomes where names.tax_id = reference_genomes.tax_id and name_txt like ? and name_class = 'scientific name';");
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
					System.out.println("Couldn't find any results for given argument.");
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

	private static void output(int tax_id, Map<String, List<String>> map, Connection connection, String[] args) throws SQLException {
		if (args.length == 1) {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				System.out.println(entry.getKey());
			}
		} else {
			if (args[args.length-1].equalsIgnoreCase("dowload") || args[args.length-1].equalsIgnoreCase("-d")) {
				download(tax_id, map, connection, args);
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

	private static List<String> getHeaders(int tax_id, Connection connection, String[] args) throws SQLException {
		List<String> headers = new ArrayList<>();
		PreparedStatement pstmt = connection.prepareStatement("select * from headers where tax_id = ? and header like ?;");
		for (int i = 1; i < args.length; i++) {
			pstmt.setInt(1, tax_id);
			pstmt.setString(2, "%" + args[i] + "%");
			ResultSet rs2 = pstmt.executeQuery();
			while (rs2.next()) {
				headers.add(rs2.getString("header"));
			}
		}	
		return headers;
	}

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
}
