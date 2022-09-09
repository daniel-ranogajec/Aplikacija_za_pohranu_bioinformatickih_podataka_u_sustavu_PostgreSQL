package postgres.database.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DownloadFastaFile {

	public static void download(String name, List<String> lines) {
		String home = System.getProperty("user.home");
		File file = new File(home+"/Downloads/" + name + ".fasta"); 
		try {
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file);
				if (lines != null) {
					for (String line : lines) 
						if (!line.isBlank())
							fw.write(line + System.lineSeparator());
				}
				
				fw.close();
				System.out.println("File \"" + file.toString() + "\" saved!");
			} else {
				System.out.println("File \"" + file.toString() + "\" already exists!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void download(String name, List<String> lines, File location) {
		File file = new File(location + "/" + name + ".fasta"); 
		try {
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file);
				if (lines != null) {
					for (String line : lines) 
						if (!line.isBlank())
							fw.write(line + System.lineSeparator());
				}

				fw.close();
				System.out.println("File \"" + file.toString() + "\" saved!");
			} else {
				System.out.println("File \"" + file.toString() + "\" already exists!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
