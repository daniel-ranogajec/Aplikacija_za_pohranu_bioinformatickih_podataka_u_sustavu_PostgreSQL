package postgres.database.seed;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import postgres.database.tools.DatabaseConnection;
import postgres.database.tools.FileReader;


/**
 * Class for seeding reference genomes
 * Used for creating filling table ReferenceGenomes.
 * Should not be called before Seed.
 * Can be called multiple times.
 * When adding new resource in referenge_genomes folder, run this main to update database.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class SeedReferenceGenomes {

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		seed();
	}
	
	public static void seed() {
		File dir = new File("src/resources/reference_genomes");
		if (!dir.isDirectory()) 
			throw new RuntimeException("Not a directory!");

		File[] files = dir.listFiles();
		if (files == null)
			throw new NullPointerException("I/O Error occured!");

		

		List<String> userData = null;
		try {
			userData = DatabaseConnection.Connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		if (userData == null)
			return;
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){

			System.out.println("Connected to PostgreSQL database!");

			Map<Integer, File> mapRG = new HashMap<>();
			
			PreparedStatement pstmt = connection.prepareStatement("select * from names where name_txt = ?");

			for (File file : files) {
				String s = file.getName();
				if (!s.endsWith(".fasta"))
					throw new RuntimeException("Not an .fasta file!");
				s = s.substring(0, s.length()-6).replace('_', ' ');
				
				pstmt.setString(1, s);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					
					mapRG.put(rs.getInt("tax_id"),file);
					
				}
			}
						
			fillReferenceGenomes(connection, mapRG);

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
     * Private method used for filling Reference_genomes table
     * @param connection
     * @param map of reference_genome files and its tax_id's
     * @throws SQLException
     */
	private static void fillReferenceGenomes(Connection connection, Map<Integer, File> map) throws SQLException {
		String SQLinsert = "INSERT INTO reference_genomes VALUES(?,?) returning genome_id;";
		Map<String, Integer> mapHeaders = new HashMap<>();
		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
						
			for (Map.Entry<Integer, File> entry : map.entrySet()) {
				
				pstmt.setInt(1, entry.getKey());
				pstmt.setString(2, entry.getValue().getName());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					Map<String, List<String>> fasta = FileReader.readFasta(entry.getValue().getName());
					Set<String> set = fasta.keySet();
					for (String string : set) {
						mapHeaders.put(string, rs.getInt(1));
					}
				}
	        }
	        System.out.println("Table reference_genomes filled!");
	        
			fillHeaders(connection, mapHeaders);

        } catch (SQLException e) {
        	throw new SQLException("Error inserting into reference_genomes table.");
        }		
	}
	
	/**
     * Private method used for filling Headers table
     * @param connection
     * @param map of Header strings and its tax_id's
     * @throws SQLException
     */
    private static void fillHeaders(Connection connection, Map<String, Integer> map) throws SQLException {
    	String SQLinsert = "INSERT INTO headers VALUES(?,?);";

		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
						
			int counter = 0;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
	        	
				pstmt.setString(1, entry.getKey());
				pstmt.setInt(2, entry.getValue());
				
				pstmt.addBatch();
				
				if (++counter % 100 == 0 || counter == map.size())
					pstmt.executeBatch();
	        }
	        System.out.println("Table headers filled!");

        } catch (SQLException e) {
        	throw new SQLException("Error inserting into headers table.");
        }	
	}
	
}
