package postgres.database.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import postgres.database.tools.DatabaseConnection;
import postgres.database.tools.FileReader;

/**
 * Example for searching Escherichia coli reference genomes
 * 
 * @author Daniel_Ranogajec
 *
 */
public class Demo {
	
	public static void main(String[] args) {
		
		List<String> userData = null;
		try {
			userData = DatabaseConnection.Connect();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String s = "Escherichia coli";
		
		if (userData == null)
			return;
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){
			
			PreparedStatement pstmt = connection.prepareStatement("select * from names, reference_genomes where name_txt = ?");
			pstmt.setString(1, s);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Map<String, List<String>> map = FileReader.readFasta(rs.getString("file_location"));
				
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					System.out.println(entry.getKey());
					//entry.getValue().forEach(l -> System.out.println(l));
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
}
