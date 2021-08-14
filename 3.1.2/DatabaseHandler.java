import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import Secret;

/**
 * Opens a connection to a Database
 * @author Gustav Sjölin
 *
 */
public class DatabaseHandler {
	private final String url = "jdbc:mysql://localhost:3306/GuestBookDB";
	Secret s = new Secret();
	private final String username = s.username;
	private final String password = s.password;
	private Connection dbConnection;
	private Statement st;
	private TempDB tempdb;
	public static String lastID;
	
	/**
	 * Initilizing the connection to the database.
	 * 
	 * @param tempdb
	 */
	public DatabaseHandler(TempDB tempdb) {
		this.tempdb = tempdb;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			 dbConnection = DriverManager.getConnection(url,username,password);
			 st = dbConnection.createStatement();
			 downloadAllPosts();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Downloads the post from the Database to TempDB.
	 * @see TempDB
	 */
	public void downloadAllPosts() {
		
		try {
			ResultSet r = st.executeQuery("SELECT * FROM GuestBookDB.posts;");
			while(r.next()) {
//			
				tempdb.saveValues(r.getString(1), r.getString("name"), r.getString("epost"), r.getString("website"), r.getString("comment"));
				lastID = r.getString(1);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	/**
	 * Saves the new posts in the Database
	 * <p>
	 * Should be called when the GUI is closed to save all the new posts. 
	 * Closes the connection to the database.
	 */
	public void saveToDatabase() {
		try {
			StringBuilder newRecord = tempdb.getnewRecord();
			String name = "";
			String epost = "";
			String website = "";
			String comment = "";
			String insertString = "";
			
			if(!newRecord.toString().equals("")) {
				String[] newPosts = newRecord.toString().split("\n");
				
				for(String row: newPosts) {
					String[] data = row.split(",");
					name = data[0];
					epost = data[1];
					website = data[2];
					comment = data[3];
					insertString = "INSERT INTO GuestBookDB.posts (name,epost,website,comment) VALUES('"+name+"','"+epost+"','"+website+"','"+comment+"')";
					st.execute(insertString);
				}
			}
			
			dbConnection.close();
			System.out.println("DATABASE SAVED");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}	
	
	

