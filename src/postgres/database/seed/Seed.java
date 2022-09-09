package postgres.database.seed;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import postgres.database.tools.DatabaseConnection;
import postgres.database.tools.FileReader;
 
/**
 * Seed class which should be called only once.
 * Used for creating all the tables and filling them with data.
 * If there was an internal database error while filling the tables, delete that database and create a new one.
 * It could take up to 10 minutes to fill the database.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class Seed {
	
	/**
	 * Main method
	 */
    public static void main(String[] args) {
		seed();
    }
    
    public static void seed() {
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

        	createTables(connection);
        	
        	fillDivisions(connection);
        	fillGencode(connection);
            fillNodes(connection);
            fillNames(connection);
            
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
     * Private method used for creating all the tables
     * @param connection
     * @throws SQLException
     */
	private static void createTables(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		String sql = String.join("\n", FileReader.readFile("SQL_CreateTable"));
		statement.executeUpdate(sql);
		statement.close();
		System.out.println("Tables created!");
	}
	
    /**
     * Private method used for filling Divisions table
     * @param connection
     * @throws SQLException
     */
	private static void fillDivisions(Connection connection) throws SQLException {
		
		String SQLinsert = "INSERT INTO division VALUES(?,?,?,?);";

		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
			
			List<String> list = FileReader.readFile("taxdump/division.dmp");
			
			int counter = 0;
	        for (String s : list) {
	        	
				String[] elems = s.split("\\|");
				for (int i = 0; i < elems.length; i++) 
					elems[i] = elems[i].trim();
				
				pstmt.setInt(1, Integer.parseInt(elems[0]));
				pstmt.setString(2, elems[1]);
				if (elems[2].isEmpty()) 
					pstmt.setNull(3, java.sql.Types.VARCHAR);
				else
					pstmt.setString(3, elems[2]);
				pstmt.setString(4, elems[3]);
				
				pstmt.addBatch();
				
				if (++counter % 100 == 0 || counter == list.size())
					pstmt.executeBatch();
	        }
	        System.out.println("Table division filled!");

        } 	catch (SQLException e) {
        	throw new SQLException("Error inserting into division table.");
        }
	}

    /**
     * Private method used for filling Gencode table
     * @param connection
     * @throws SQLException
     */
	private static void fillGencode(Connection connection) throws SQLException {

		String SQLinsert = "INSERT INTO gencode VALUES(?,?,?,?,?);";

		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
	
			List<String> list = FileReader.readFile("taxdump/gencode.dmp");
			
			int counter = 0;
	        for (String s : list) {
	        	
				String[] elems = s.split("\\|");
				for (int i = 0; i < elems.length; i++) 
					elems[i] = elems[i].trim();
				
				pstmt.setInt(1, Integer.parseInt(elems[0]));
				if (elems[1].isEmpty())
					pstmt.setNull(2, java.sql.Types.VARCHAR);
				else
					pstmt.setString(2, elems[1]);
				pstmt.setString(3, elems[2]);
				if (elems[3].isEmpty()) 
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				else
					pstmt.setString(4, elems[3]);
				if (elems[4].isEmpty()) 
					pstmt.setNull(5, java.sql.Types.VARCHAR);
				else
					pstmt.setString(5, elems[4]);
				
				pstmt.addBatch();
				
				if (++counter % 100 == 0 || counter == list.size())
					pstmt.executeBatch();
				
	        }
	        System.out.println("Table gencode filled!");
	        
		} 	catch (SQLException e) {
        	throw new SQLException("Error inserting into gencode table.");
        }		
	}

    /**
     * Private method used for filling Names table
     * @param connection
     * @throws SQLException
     */
	private static void fillNames(Connection connection) throws SQLException {
		
		String SQLinsert = "INSERT INTO names VALUES(?,?,?,?);";

		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
	
			List<String> list = FileReader.readFile("taxdump/names.dmp");
			
			int counter = 0;
	        for (String s : list) {
	        	
				String[] elems = s.split("\\|");
				for (int i = 0; i < elems.length; i++) 
					elems[i] = elems[i].trim();
				
				pstmt.setInt(1, Integer.parseInt(elems[0]));
				pstmt.setString(2, elems[1]);
				pstmt.setString(3, elems[2]);
				if (elems[3].isEmpty()) 
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				else
					pstmt.setString(4, elems[3]);
				
				pstmt.addBatch();
				
				if (++counter % 100 == 0 || counter == list.size())
					pstmt.executeBatch();
				
	        }
	        System.out.println("Table names filled!");

		} 	catch (SQLException e) {
        	throw new SQLException("Error inserting into names table.");
        }		
	}

    /**
     * Private method used for filling Nodes table
     * @param connection
     * @throws SQLException
     */
	private static void fillNodes(Connection connection) throws SQLException {
		
		String SQLinsert = "INSERT INTO nodes VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";

		try (PreparedStatement pstmt = connection.prepareStatement(SQLinsert)) {
	
			List<String> list = FileReader.readFile("taxdump/nodes.dmp");
			
			int counter = 0;
	        for (String s : list) {
	        	
				String[] elems = s.split("\\|");
				for (int i = 0; i < elems.length; i++) 
					elems[i] = elems[i].trim();
				
				pstmt.setInt(1, Integer.parseInt(elems[0]));
				pstmt.setInt(2, Integer.parseInt(elems[1]));
				pstmt.setString(3, elems[2]);
				if (elems[3].isEmpty()) 
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				else
					pstmt.setString(4, elems[3]);
				pstmt.setInt(5, Integer.parseInt(elems[4]));
				pstmt.setInt(6, Integer.parseInt(elems[5]));
				pstmt.setInt(7, Integer.parseInt(elems[6]));
				pstmt.setInt(8, Integer.parseInt(elems[7]));
				pstmt.setInt(9, Integer.parseInt(elems[8]));
				pstmt.setInt(10, Integer.parseInt(elems[9]));
				pstmt.setInt(11, Integer.parseInt(elems[10]));
				pstmt.setInt(12, Integer.parseInt(elems[11]));
				if (elems[12].isEmpty()) 
					pstmt.setNull(13, java.sql.Types.VARCHAR);
				else
					pstmt.setString(13, elems[12]);
				
				pstmt.addBatch();
				
				if (++counter % 100 == 0 || counter == list.size())
					pstmt.executeBatch();
				
	        }
	        System.out.println("Table nodes filled!");
	        
		} 	catch (SQLException e) {
        	throw new SQLException("Error inserting into nodes table.");
        }
		
	}

}