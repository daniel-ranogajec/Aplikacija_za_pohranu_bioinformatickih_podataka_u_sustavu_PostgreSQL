package zavrsni;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.WindowConstants;
import javax.swing.table.TableRowSorter;

import model.Gene;

/**
 * JFrame that shows a list of genes with its information.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class GenesWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private List<Gene> genes;
	private JTable table;
	private TableModel tableModel;

	
	/**
	 * Constructor method
	 * 
	 * @param genes list of genes
	 */
	public GenesWindow(List<Gene> genes) {
		
		if (genes == null || genes.isEmpty())
			return;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.genes = genes;
		initGUI();
		setSize(600, 300);
		setLocationRelativeTo(null);
		setTitle(genes.get(0).getOrganism());
		setVisible(true);
	}

	/**
	 * Method for initializing the GUI.
	 */
	private void initGUI() {

		Container cp = this.getContentPane();
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.CYAN);
		
		GenesListModel model = new GenesListModel(genes);
		
		JList<Gene> lista = new JList<>(model);
		
		tableModel = new TableModel(initData(genes.get(0)));
		table = new JTable(tableModel);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKey = new ArrayList<>();
		sortKey.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKey);
		
		JPanel panel2 = new JPanel();
		panel2.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		panel2.setLayout(new BorderLayout());
		panel2.setBackground(Color.CYAN);
		
		panel2.add(new JScrollPane(lista));
		
		lista.setSelectedIndex(0);
		lista.addListSelectionListener(e -> dataSelected(lista.getSelectedValue()));
		JPanel panel3 = new JPanel();
		panel3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel3.setLayout(new BorderLayout());
		panel3.setBackground(Color.CYAN);
		
		panel3.add(table);
		
		panel.add(panel2, BorderLayout.CENTER);
		panel.add(panel3, BorderLayout.AFTER_LAST_LINE);
		cp.add(panel);
	}

	/**
	 * Method that is called when selected value from list of genes is changed.
	 * 
	 * @param selectedValue
	 */
	protected void dataSelected(Gene selectedValue) {
		
		tableModel.updateData(initData(selectedValue));
		tableModel.fireTableDataChanged();
		
	}

	/**
	 * Method for initializing the data.
	 * @param gen the gene you are showing the info of
	 * @return map of gene info
	 */
	private Map<String, String> initData(Gene gen) {
		
		Map<String, String> mapa = new HashMap<>();
		mapa.put("Symbol", gen.getSymbol());
		mapa.put("Annotation", gen.getAnnotation());
		mapa.put("Gene ID", String.valueOf(gen.getID()));
		mapa.put("Gene Description", gen.getGene_description());
		mapa.put("Genomic Context", gen.getGenomic_context());
		if (gen.getOther_aliases() != null)
			mapa.put("Other Aliases", Arrays.stream(gen.getOther_aliases()).collect(Collectors.joining(", ")));
		else
			mapa.put("Other Aliases", null);

		if (gen.getOther_designations() != null)
			mapa.put("Other Designations", Arrays.stream(gen.getOther_designations()).collect(Collectors.joining(", ")));
		else
			mapa.put("Other Designations", null);
		return mapa;
	}


}
