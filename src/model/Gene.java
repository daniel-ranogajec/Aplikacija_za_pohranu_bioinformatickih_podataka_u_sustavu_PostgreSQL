package model;

/**
 * Model for genes.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class Gene {

	private String symbol;
	private String gene_description;
	private String organism;
	private String genomic_context;
	private String annotation;
	private int ID;
	private String[] other_aliases;
	private String[] other_designations;
	

	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getGene_description() {
		return gene_description;
	}
	
	public void setGene_description(String gene_description) {
		this.gene_description = gene_description;
	}
	
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	
	public String getGenomic_context() {
		return genomic_context;
	}
	
	public void setGenomic_context(String genomic_context) {
		this.genomic_context = genomic_context;
	}
	
	public String getAnnotation() {
		return annotation;
	}
	
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int id) {
		ID = id;
	}
	
	public String[] getOther_aliases() {
		return other_aliases;
	}
	
	public void setOther_aliases(String[] other_aliases) {
		this.other_aliases = other_aliases;
	}
	
	public void setOther_aliases(String other_aliases) {
		if (other_aliases != null)
			this.other_aliases = other_aliases.split(", ");
	}
	
	public String[] getOther_designations() {
		return other_designations;
	}
	
	public void setOther_designations(String[] other_designations) {
		this.other_designations = other_designations;
	}
	
	public void setOther_designations(String other_designations) {
		if (other_designations != null)
			this.other_designations = other_designations.split(", ");
	}

	@Override
	public String toString() {
		return symbol;
	}
	
	
	
}
