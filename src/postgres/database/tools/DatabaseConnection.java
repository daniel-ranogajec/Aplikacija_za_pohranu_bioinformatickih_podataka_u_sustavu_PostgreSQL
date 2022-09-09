package postgres.database.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

/**
 * Helper class used for getting database info.
 * When connection to database, call Connect function which checks for data in src/resources/database_info
 * and if there is no data there, asks user to provide with data.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class DatabaseConnection {
	
	/**
	 * Method used to check for database info.
	 * If there is no data it will ask user to insert data.
	 * @return List<String> data with database name, username and password
	 * @throws IOException database_info file was not found
	 */
	public static List<String> Connect() throws IOException {
		File f = new File("src/resources/database_info");
		
		List<String> list = Files.readAllLines(f.toPath());
		
		if (list.size() > 2) 
			return list;
		
		if (!list.isEmpty()) 
			throw new RuntimeException();
		
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Insert database name: ");
		list.add(sc.nextLine());
		
		System.out.print("Insert user name: ");
		list.add(sc.nextLine());

		System.out.print("Insert password: ");
		list.add(sc.nextLine());

		sc.close();
		
		FileWriter fw = new FileWriter(f);
		for (String line : list) 
			fw.write(line + System.lineSeparator());
		
		fw.close();
		
		return list;		
	}
	
	
	public static List<String> ConnectToDb() throws IOException {
		File f = new File("src/resources/database_info");
		
		List<String> list = Files.readAllLines(f.toPath());
		
		if (list.size() > 2) 
			return list;
		
		if (!list.isEmpty()) 
			throw new IOException();
		
		return null;
	}
	
	/**
	 * Method that is called when program could not connect to database.
	 * Method empties database_info so user can enter new data next time.
	 * @throws IOException database_info file was not found
	 */
	public static void WrongData() throws IOException {
		File f = new File("src/resources/database_info");
		
		FileWriter fw = new FileWriter(f);
			fw.write("");
			
		fw.close();
	}
	
}
