package postgres.database.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import model.Gene;

/**
 * Parser class for parsing genes read from a file
 * 
 * @author Daniel_Ranogajec
 *
 */
public class GeneParser {

	public static List<Gene> parseGenes(File file) {
		List<String> s;
		List<Gene> geni = new ArrayList<>();
		try {
			s = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			return null;
		}
		s.remove(s.size()-1);
		if (s.get(0).isEmpty())
			s.remove(0);
		geni.add(new Gene());
		int cnt = 0;
		for (String string : s) {
			if (string.isEmpty()) {
				geni.add(new Gene());
				cnt++;
			} else {
				parse(string, geni.get(cnt));
			}
		}
		geni.removeIf(g -> g.getSymbol() == null || g.getSymbol().equals("NEWENTRY"));
		
		return geni;
	}

	private static void parse(String string, Gene g) {
		if (g.getSymbol() == null) {
			g.setSymbol(string.split(" ")[1]);
		} else if (g.getGene_description() == null) {
			String[] spt = string.substring(0, string.length() - 1).split(" \\[");
			g.setGene_description(spt[0]);
			g.setOrganism(spt[1]);
		} else if(Character.isDigit(string.charAt(0))) {
			g.setSymbol(string.split(" ")[1]);
		} else if (string.startsWith("Other Aliases: ")) {
			g.setOther_aliases(string.substring(15).split(", "));
		} else if (string.startsWith("Other Designations: ")) {
			g.setOther_designations(string.substring(20).split(", "));
		} else if (string.startsWith("Genomic context: ")) {
			g.setGenomic_context(string.substring(17));
		} else if (string.startsWith("ID: ")) {
			g.setID(Integer.parseInt(string.substring(4)));
		} else if (string.startsWith("Annotation: ")) {
			g.setAnnotation(string.substring(13));
		}
	}

}
