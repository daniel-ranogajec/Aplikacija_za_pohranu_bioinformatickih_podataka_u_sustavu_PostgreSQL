package zavrsni;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import model.Gene;

/**
 * Model for the list of genes that implements ListModel
 * 
 * @author Daniel_Ranogajec
 *
 */
public class GenesListModel implements ListModel<Gene>{
	
	private List<Gene> elements;
	private List<ListDataListener> listeners;
	
	/**
	 * Constructor method.
	 * @param elements list of genes
	 */
	public GenesListModel(List<Gene> elements) {
		this.elements = elements;
		this.listeners = new ArrayList<>();
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	public Gene getElementAt(int index) {
		return elements.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	

}
