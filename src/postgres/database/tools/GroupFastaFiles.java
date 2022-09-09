package postgres.database.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Helper method used for grouping multiple Fasta files into one file for easier database handling.
 * All files that you want to group must be in the same directory.
 * After grouping files, in the same directory you will find new 'sequence_grouped.fasta' file
 * 
 * @author Daniel_Ranogajec
 *
 */
public class GroupFastaFiles {

	public static void main(String[] args) {
		Scanner sc = new java.util.Scanner(System.in);

		System.out.println("Enter directory location: ");
		String location = sc.next().trim();
		sc.close();

		File dir = new File(location);
		if (!dir.isDirectory())
			throw new RuntimeException("Not a directory!");

		File[] files = dir.listFiles();
		if (files == null)
			throw new NullPointerException("I/O Error occured!");


		List<String> list = new ArrayList<>();

		Comparator<File> comp1 = (f1, f2) -> f1.getName().compareTo(f2.getName());
		Comparator<File> comp2 = (f1, f2) -> { 
			if(f1.getName().length() > f2.getName().length())
				return 1;
			else if (f1.getName().length() == f2.getName().length())
				return 0;
			return -1;
		};
		Arrays.stream(files).sorted(comp2.thenComparing(comp1)).forEach(f -> {
			try {
				list.addAll(Files.readAllLines(f.toPath()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		try {
			File newFile = new File(location + "/sequence_grouped.fasta");
			if (newFile.createNewFile()) {
				FileWriter fw = new FileWriter(newFile);
				for (String line : list) 
					if (!line.isBlank())
						fw.write(line + System.lineSeparator());

				fw.close();
				System.out.println("Sequence files were grouped to sequence_grouped.fasta!");

			} else {
				System.out.println("Grouped file already exists");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static List<String> groupFiles(File[] files) {
		if (files == null)
			throw new NullPointerException("Given files can't be null!");

		if (files.length == 1) 
			throw new IllegalArgumentException("You have to give more than 1 file!");

		List<String> list = new ArrayList<>();

		Comparator<File> comp1 = (f1, f2) -> f1.getName().compareTo(f2.getName());
		Comparator<File> comp2 = (f1, f2) -> { 
			if(f1.getName().length() > f2.getName().length())
				return 1;
			else if (f1.getName().length() == f2.getName().length())
				return 0;
			return -1;
		};
		Arrays.stream(files).sorted(comp2.thenComparing(comp1)).forEach(f -> {
			try {
				list.addAll(Files.readAllLines(f.toPath()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		return list;
		
	}
}
