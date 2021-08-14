import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import java.util.regex.Pattern;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
/**
 * A GUI to display the information of the Guestbook
 * @author Gustav Sjölin
 *
 */
public class GUI extends JFrame{

	JPanel namePanel;
	JLabel nameLabel;
	JTextField nameField;
	
	JPanel epostPanel;
	JLabel epostLabel;
	JTextField epostField;
	
	JPanel websitePanel;
	JLabel websiteLabel;
	JTextField websiteField;
	
	JPanel commentPanel;
	JLabel commentLabel;
	JTextField commentField;
	
	JPanel addPanel;
	JLabel addLabel;
	JButton addButton;
	
	JTextArea logArea;
	JScrollPane scrollArea;
	
	JPanel upper;
	JPanel down;
	
	private final int cols = 25;
	private TempDB tempdb;
	private DatabaseHandler db;
	
	
	/**
	 * Initilizing the GUI 
	 * <p>
	 * Opens up a GuestBook GUI.
	 * 
	 * @param tempdb
	 */
	public GUI(TempDB tempdb) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(650,500);
//		setPreferredSize(new Dimension(400,100));
		setLayout(new GridLayout(2,1));
		this.tempdb = tempdb;
		 db = new DatabaseHandler(tempdb);
		
		
		upper = new JPanel(new GridLayout(5,1));
		down = new JPanel(new GridLayout());
		
		namePanel = new JPanel(new BorderLayout());
		nameField = new JTextField();
		nameLabel = new JLabel("Name:");
		namePanel.add(nameLabel,BorderLayout.WEST);
		namePanel.add(nameField,BorderLayout.EAST);
		nameField.setColumns(cols);
	
		
		epostPanel = new JPanel(new BorderLayout());
//		epostPanel.setAlignmentX(LEFT_ALIGNMENT);
		epostField = new JTextField();
		epostLabel = new JLabel("E-mail: ");
		epostField.setColumns(cols);
		epostPanel.add(epostLabel,BorderLayout.WEST);
		epostPanel.add(epostField,BorderLayout.EAST);
		
		websitePanel = new JPanel(new BorderLayout());
		websiteField = new JTextField();
		websiteLabel = new JLabel("Homepage: ");
		websiteField.setColumns(cols);
		websitePanel.add(websiteLabel,BorderLayout.WEST);
		websitePanel.add(websiteField,BorderLayout.EAST);
		
		commentPanel = new JPanel(new BorderLayout());
		commentField = new JTextField();
		commentLabel = new JLabel("Comment: ");
		commentField.setColumns(cols);
		commentPanel.add(commentLabel,BorderLayout.WEST);
		commentPanel.add(commentField,BorderLayout.EAST);
		
		addPanel = new JPanel(new BorderLayout());
		addButton = new JButton("Add");
		addLabel = new JLabel("Add post: ");
		addPanel.add(addLabel,BorderLayout.WEST);
		addPanel.add(addButton,BorderLayout.EAST);
		
		
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setSize(new Dimension(300,300));
		scrollArea = new JScrollPane(logArea);
		scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		down.add(scrollArea);
		
		
		upper.add(namePanel);
		upper.add(epostPanel);
		upper.add(websitePanel);
		upper.add(commentPanel);
		upper.add(addPanel);
		
		logArea.setText(tempdb.toString());
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String epost = epostField.getText();
				String website = websiteField.getText();
				String comment = commentField.getText();
				Pattern p = Pattern.compile("<.*>");
				
				if (p.matcher(name).matches()) {
					name = "CENSUR";
				}
				if(p.matcher(epost).matches()) {
					epost = "CENSUR";
				}
				if(p.matcher(website).matches()) {
					website = "CENSUR";
				}
				if(p.matcher(comment).matches()) {
					comment = "CENSUR";
				}
				
				tempdb.addPost(name, epost, website, comment);
				updateLog();
				nameField.setText("");
				epostField.setText("");
				websiteField.setText("");
				commentField.setText("");
			}

			
			
		});
		
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent event) {
		        saveAndClose();
		        
		    }
		});
		
		
        
        
		add(upper);
		add(down);
		
		
//		pack();
		setVisible(true);
		
	}
	
	
	/**
	 * Saving the new postes before closing the window.
	 */
	private void saveAndClose() {
		System.out.println("CLOSING");
		db.saveToDatabase();
		System.exit(0);
		
	}
	
	/**
	 * Updating the textarea with new posts.
	 */
	private void updateLog() {
		logArea.setText(tempdb.toString());
		
	}
	
	
	
	
	/**
	 * Main method.
	 *
	 * Creates a new temporary database and opens the GUI. 
	 * @param args
	 */
	public static void main(String[] args) {
		TempDB tdb = new TempDB();
		GUI g = new GUI(tdb);
		
	}
}
