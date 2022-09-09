package zavrsni;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import postgres.database.seed.Seed;
import postgres.database.seed.SeedReferenceGenomes;
import postgres.database.tools.DatabaseConnection;

/**
 * The application for storing bioinformatics data.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class BioinformaticsApp extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static List<String> userData = null;
	private JTextField username;
	private JTextField password;
	private JTextField dbName;
	private JButton login;
	private JLabel iconLabel;

	/**
	 * Constructor method.
	 */
	public BioinformaticsApp() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initGUI();

		setSize(400, 180);
		setLocationRelativeTo(null);
		setTitle("Login to database");
	}

	/**
	 * Method for initializing GUI.
	 */
	private void initGUI() {
		Container cp = this.getContentPane();

		cp.setLayout(new BorderLayout());

		JPanel p1 = new JPanel();
		p1.setBackground(Color.CYAN);
		p1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		p1.setLayout(new GridLayout(3,2));

		JPanel p2 = new JPanel();		
		p2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		p2.setBackground(Color.CYAN);


		p1.add(new JLabel("Username"));

		username = new JTextField();

		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(allHaveText())
					login.setEnabled(true);
				else
					login.setEnabled(false);

			}
		};
		username.addKeyListener(keyAdapter);

		p1.add(username);

		p1.add(new JLabel("Password"));

		password = new JTextField();
		password.addKeyListener(keyAdapter);
		p1.add(password);

		p1.add(new JLabel("Database name"));

		dbName = new JTextField();
		dbName.addKeyListener(keyAdapter);
		p1.add(dbName);

		login = new JButton("Log in");
		login.setEnabled(false);
		login.addActionListener(e -> {
			if (!username.getText().isEmpty() && !password.getText().isEmpty() && !login.getText().isEmpty()) {
				userData = new ArrayList<>();
				userData.add(dbName.getText());
				userData.add(username.getText());
				userData.add(password.getText());

				try {
					DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2));
				} catch (SQLException ex) {
					userData.clear();
					JOptionPane.showMessageDialog(BioinformaticsApp.this,
							"Incorrect data.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				if (!userData.isEmpty()) {
					File f = new File("src/resources/database_info");
					FileWriter fw;
					try {
						fw = new FileWriter(f);
						for (String line : userData) 
							fw.write(line + System.lineSeparator());

						fw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					Thread t = new Thread() {
						@Override
						public void run() {
							Seed.seed();
							SeedReferenceGenomes.seed();
							BioinformaticsApp.this.dispose();
							SwingUtilities.invokeLater(() -> {
								new MainWindow().setVisible(true);
							});
						}
					};

					try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2))){
						try {
							PreparedStatement pstmt = connection.prepareStatement("select count(*) from nodes;");
							ResultSet rs = pstmt.executeQuery();

							int k = 0;
							if (rs.next()) {
								k = rs.getInt(1);
							}

							if (k == 0) {
								if (JOptionPane.showOptionDialog(BioinformaticsApp.this, "Your database is empty. Please wait a couple of minutes to fill it!", "Fill the database", JOptionPane.OK_CANCEL_OPTION,  JOptionPane.WARNING_MESSAGE, null, null, null) != JOptionPane.OK_OPTION)
									return;

								login.setEnabled(false);
								iconLabel.setVisible(true);
								t.start();

							} else {
								this.dispose();
								SwingUtilities.invokeLater(() -> {
									new MainWindow().setVisible(true);
								});
							}

						} catch (SQLException ex) {
							if (JOptionPane.showOptionDialog(BioinformaticsApp.this, "Your database is empty. Please wait a couple of minutes to fill it!", "Fill the database", JOptionPane.OK_CANCEL_OPTION,  JOptionPane.WARNING_MESSAGE, null, null, null) != JOptionPane.OK_OPTION)
								return;

							login.setEnabled(false);
							iconLabel.setVisible(true);
							t.start();

						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					iconLabel.setVisible(true);


				}
			}
		});

		ImageIcon imageIcon = new ImageIcon("src/resources/ajax-loader.gif");
		iconLabel = new JLabel();
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setIcon(imageIcon);
		imageIcon.setImageObserver(iconLabel);
		iconLabel.setVisible(false);


		p2.setLayout(new BorderLayout());
		p2.add(login, BorderLayout.BEFORE_FIRST_LINE);
		p2.add(iconLabel, BorderLayout.CENTER);

		cp.add(p1, BorderLayout.CENTER);
		cp.add(p2, BorderLayout.PAGE_END);


	}


	/**
	 * Private method for checking if all JTextFields have text
	 * @return true if all have text
	 */
	private boolean allHaveText() {
		return !username.getText().isEmpty() && !password.getText().isEmpty() && !dbName.getText().isEmpty();

	}


	public static void main(String[] args) {

		boolean connected = false;
		try {
			userData = DatabaseConnection.ConnectToDb();
			DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + userData.get(0), userData.get(1), userData.get(2));
			connected = true;
		} catch (IOException | SQLException | NullPointerException e) {
			SwingUtilities.invokeLater(() -> {
				new BioinformaticsApp().setVisible(true);
			});
		} 

		if (connected)
			SwingUtilities.invokeLater(() -> {
				new MainWindow().setVisible(true);
			});
	}
}
