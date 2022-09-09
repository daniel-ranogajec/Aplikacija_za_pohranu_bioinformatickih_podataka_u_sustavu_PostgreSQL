package postgres.database.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class used for reading files
 * 
 * @author Daniel_Ranogajec
 *
 */
public class FileReader {

	/**
	 * Static method that reads given file from src/resources
	 * @param name of file that is in src/resources/ folder
	 * @return List<String> lines from the file, <code>null</code> if couldn't read the file
	 */
	public static List<String> readFile(String name) {
		File file = new File("src/resources/" + name);
		try {
			List<String> s = Files.readAllLines(file.toPath());
			return s;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Static method used for reading Fasta files
	 * @param name name of file that is in src/resources/reference_genomes
	 * @return A map that contains data about genome as key and content of genome as list of strings
	 */
	public static Map<String, List<String>> readFasta(String name) {
		List<String> lines = readFile("reference_genomes/" + name);
		
		Map<String, List<String>> map = new HashMap<>();
		String current = null;
		List<String> list = new ArrayList<>();
		
		for (String s : lines) {
			if (s.startsWith(">")) {
				if (current != null)  {
					map.put(current, new ArrayList<String>(list));
					list.clear();
				}
				current = s;

			} else {
				list.add(s);
			}
		}
		
		if (current != null && !list.isEmpty())
			map.put(current, list);
		
		return map;
	}
}
