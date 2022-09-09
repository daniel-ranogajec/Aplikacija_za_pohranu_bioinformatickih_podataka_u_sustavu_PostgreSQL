package zavrsni;

import java.util.Arrays;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * Model for organisms that extends AbstractTableModel.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class TableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String[] dataKey;
	private String[] dataValue;
	
	/**
	 * Constructor method.
	 * @param data pairing of key and value of each information of an organism
	 */
	public TableModel(Map<String, String> data) {
		if (data != null) {
			dataKey = Arrays.copyOf(data.keySet().toArray(), data.size(), String[].class);
			dataValue = Arrays.copyOf(data.values().toArray(), data.size(), String[].class);
		}
	}

	@Override
	public int getRowCount() {
		if (dataKey != null)
			return dataKey.length;
		else
			return 0;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return switch(columnIndex) {
		case 0 -> dataKey[rowIndex];
		case 1 -> dataValue[rowIndex];
		default -> throw new IllegalArgumentException();
		};
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public String getColumnName(int column) {
		return "";
	}

	/**
	 * Method used for updating data
	 * @param data
	 */
	public void updateData(Map<String, String> data) {
		if (data != null) {
			dataKey = Arrays.copyOf(data.keySet().toArray(), data.size(), String[].class);
			dataValue = Arrays.copyOf(data.values().toArray(), data.size(), String[].class);
		}
		
	}
	
}
