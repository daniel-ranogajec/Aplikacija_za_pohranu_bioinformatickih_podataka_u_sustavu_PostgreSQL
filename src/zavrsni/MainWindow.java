package zavrsni;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.postgresql.util.PSQLException;

import postgres.database.tools.DatabaseConnection;
import postgres.database.tools.FileReader;

/**
 * JFrame that represents a search bar.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class MainWindow extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JTextArea search;
	private static JComboBox<String> comboBox;
	private static List<String> userData = null;
	private static String[] info;
	private static Map<String, String> data;
	private boolean hint;
	private Timer timer;

	/**
	 * Nested static class that implements Runnable.
	 * 
	 * @author Daniel_Ranogajec
	 *
	 */
	static class DbRunnable implements Runnable{

		public final static int SELECT = 1;
		public final static int INSERT = 2;
		
		private String element;
		private int mode;

		/**
		 * Constructor method.
		 * @param element
		 * @param mode
		 */
		DbRunnable(String element, int mode) {
			this.element = element;
			this.mode = mode;
		}

		/**
		 * Constructor method.
		 */
		DbRunnable() {
			this(null, 0);
		}

		@Override
		public void run() {
			if (mode == 0)
				dbSearch();
			else if (mode == 1)
				dbSelect();
			else 
				dbInsert();
		}

		/**
		 * Method used for inserting data into database.
		 */
		private void dbInsert() {
			if (userData == null)
				return;
			try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){
				try {
					PreparedStatement pstmt = connection.prepareStatement("INSERT INTO reference_genomes VALUES(?,?) returning genome_id;");
					pstmt.setInt(1, Integer.parseInt(data.get("tax_id")));
					pstmt.setString(2, element);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
						Map<String, Integer> map = new HashMap<>();
						Map<String, List<String>> fasta = FileReader.readFasta(element);
						Set<String> set = fasta.keySet();
						for (String string : set) {
							map.put(string, rs.getInt(1));
						}
						
						try (PreparedStatement pstmt2 = connection.prepareStatement("INSERT INTO headers VALUES(?,?);")) {
										
							int counter = 0;
							for (Map.Entry<String, Integer> entry : map.entrySet()) {
					        	
								pstmt2.setString(1, entry.getKey());
								pstmt2.setInt(2, entry.getValue());
								
								pstmt2.addBatch();
								
								if (++counter % 100 == 0 || counter == map.size())
									pstmt2.executeBatch();
					        }
				        } catch (SQLException e) {
				        	e.printStackTrace();
				        }	
					}

				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}

		/**
		 * Method used for selecting data from database.
		 */
		private void dbSelect() {
			if (userData == null)
				return;
			try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){

				try {
					PreparedStatement pstmt = connection.prepareStatement("select * from names, nodes left outer join reference_genomes \n"
							+ "on nodes.tax_id = reference_genomes.tax_id, gencode, division \n"
							+ "where nodes.genetic_code_id = gencode.genetic_code_id and nodes.division_id = division.division_id and nodes.tax_id = names.tax_id\n"
							+ "and lower(name_txt) = trim(lower(?));");
					pstmt.setString(1, element);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
						data = new HashMap<>();
						for (int i = 0; i < info.length; i++) {
							data.put(info[i], rs.getString(info[i]));
						}
					}
					if (data == null)
						return;
					if (data.get("file_location") == null) {
						pstmt = connection.prepareStatement("with recursive sub_tree as (\n"
								+ "select nodes.tax_id, names.name_txt, nodes.parent_tax_id, file_location\n"
								+ "from names, nodes left outer join reference_genomes \n"
								+ "on nodes.tax_id = reference_genomes.tax_id, gencode, division \n"
								+ "where nodes.genetic_code_id = gencode.genetic_code_id and nodes.division_id = division.division_id and nodes.tax_id = names.tax_id\n"
								+ "and name_txt = ?\n"
								+ "	\n"
								+ "union all\n"
								+ "	\n"
								+ "select nod.tax_id, nam.name_txt, nod.parent_tax_id, gen.file_location \n"
								+ "from sub_tree st, names nam, nodes nod left outer join reference_genomes gen\n"
								+ "on nod.tax_id = gen.tax_id, gencode gc, division div\n"
								+ "where nod.genetic_code_id = gc.genetic_code_id and nod.division_id = div.division_id and nod.tax_id = nam.tax_id\n"
								+ "and st.parent_tax_id = nod.tax_id and nam.name_class = 'scientific name'\n"
								+ ")\n"
								+ "select * from sub_tree\n"
								+ "limit 5");
						pstmt.setString(1, element);
						try {
							rs = pstmt.executeQuery();
							while(rs.next()) {
								if (rs.getString("file_location") != null) {
									data.put("file_location", rs.getString("file_location"));
									data.put("parent_name", rs.getString("name_txt"));
									break;
								}
							}
						} catch (PSQLException ex) {
							System.out.println(ex);
						}
					} 

				} catch (SQLException ex) {
					ex.printStackTrace();
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
		/**
		 * Method used for getting similar names from database.
		 */
		private void dbSearch() {
			if (userData == null)
				return;
			try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){

				try {
					PreparedStatement pstmt = connection.prepareStatement("select name_txt from names where name_txt ilike ?;");
					pstmt.setString(1, search.getText() + "%");
					ResultSet rs = pstmt.executeQuery();
					List<String> names = new ArrayList<>();
					
					while (rs.next()) 
						names.add(rs.getString("name_txt"));
					
					Collections.sort(names, Comparator.comparing(String::length).thenComparing((c1,c2) -> c1.compareTo(c2)));
					
					if (comboBox.getItemCount() != 0)
						comboBox.removeAllItems();
					for (String s : names) 
						comboBox.addItem(s);	
					
				} catch (SQLException ex) {
					ex.printStackTrace();
				}


			} catch (SQLException e) {
				System.out.println("Connection failure.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Constructor method.
	 */
	public MainWindow() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initGUI();
		setSize(650, 200);
		setLocationRelativeTo(null);
		setTitle("Bioinformatics app");

		try {
			userData = DatabaseConnection.ConnectToDb();
			DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2));
		} catch (IOException | SQLException | NullPointerException e) {
		}

		info = new String[] {"tax_id", "name_txt", "unique_name", "name_class", "parent_tax_id", "rank", "embl_code",
				"division_id", "inherited_div_flag", "genetic_code_id", "inherited_GC_flag", "mitochondrial_genetic_code_id",
				"inherited_MGC_flag", "genbank_hidden_flag", "hidden_subtree_root_flag", "division_cde", "division_name", 
				"abbrevation", "name", "cde", "starts", "file_location"};

	}

	/**
	 * Method used for initializing the GUI.
	 */
	private void initGUI() {
		
		Container cp = this.getContentPane();
			
		cp.setLayout(new WindowLayout());
		cp.setBackground(Color.CYAN);
		
		search = new JTextArea();
		search.setFont(search.getFont().deriveFont(20f)); 
		search.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), 
				BorderFactory.createLoweredBevelBorder()));
		checkHint();

		cp.add(search, WindowLayout.FIRST_ELEMENT);
		search.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				checkHint();
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (search.getText().length() > 2) {
					if (timer != null)
						timer.restart();
					else {
						timer = new Timer(500, l -> {
							if (search.getText().length() > 2) {
								Thread t = new Thread(new DbRunnable());
								t.start();
							}
						});
						timer.setRepeats(false);
					}
				}
				
			}
		});

		comboBox = new JComboBox<String>();
		
		cp.add(comboBox, WindowLayout.SECOND_ELEMENT);

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() > 1) {
					search.setText(((JComboBox<?>)e.getSource()).getSelectedItem().toString());	
					Thread t = new Thread(new DbRunnable());
					t.start();		

				}
			}
		});

		JButton next = new JButton("Search");
		next.setFont(next.getFont().deriveFont(15f));
		next.addActionListener(e -> {
			Thread t = new Thread(new DbRunnable(search.getText(), DbRunnable.SELECT));
			t.start();
			while (true) {
				try {
					t.join();
				} catch (InterruptedException ex) {
					continue;
				}
				break;
	 		}
			if (data != null) {
				new OrganismWindow(data.get("name_txt"), Integer.parseInt(data.get("tax_id")), data);
			} else {
				JOptionPane.showMessageDialog(MainWindow.this,
					    "No result!",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
		});
		cp.add(next, WindowLayout.THIRD_ELEMENT);
	}

	/**
	 * Method used for writing a hint in JTextArea.
	 */
	protected void checkHint() {
		if (search.getText().length() == 0)
			hint = true;
		if (hint) {
			search.setForeground(Color.LIGHT_GRAY);
			search.setText("Enter the name of an organism:");
			hint = false;
		} else {
			if (search.getText().equals("Enter the name of an organism:"))
				search.setText("");
			search.setForeground(Color.BLACK);
		}
	}


}
