package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 * Establishes a conncetion to the database
 * @author Gustav Sjölin
 *
 */
public class DatabaseHandler {
	private final String url = "jdbc:mysql://localhost:3306/questionDB";
	Secret s = new Secret();
	private final String username = s.username;
	private final String password = s.password;
	private Connection dbConnection;
	private ArrayList<Question> quest;
	private ArrayList<User> users;
	private int poss;
	

	
	/**
	 * Constructor
	 * <p>
	 * Creates the connection with the database.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public DatabaseHandler() {
		
			try {
				Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
				dbConnection = DriverManager.getConnection(url, username, password);
				updateQuestions();
				
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
	
	
	/**
	 * Updates the questions
	 * <p>
	 * Imports all the questions from the database and places them in an ArrayList.
	 */
	private void updateQuestions() {
		try {
			quest = new ArrayList<Question>();
			Statement st = dbConnection.createStatement();
			ResultSet r = st.executeQuery("SELECT * FROM questionDB.questions;");
			while (r.next()) {
				Question q = new Question();
				q.answer = r.getString("answer");
				q.question = r.getString("question");
				quest.add(q);
			}
			poss = quest.size() - 1;
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Returns the next question in the list.
	 * 
	 * @return The next Question in the list.
	 */
	public Question getNextQuestion() {
		Question q;
		if (quest.size() == 0) {
			updateQuestions();
		}

		if (quest.size() > 0) {
			q = quest.get(poss);
			quest.remove(poss);
			poss--;
		} else {
			q = new Question();
			q.question = "Vem?";
			q.answer = "vad";
		}

		return q;

	}
	
	/**
	 * Returns the amount of points a player has
	 * <p>
	 * Returns the amount of points the specific player has (the msg.clientUsername)
	 * 
	 * @param GameMessage msg 
	 * <ul>
	 * <li> msg.clientUsername = player to get points from
	 * @return int - amount of points the specific player has
	 * @throws SQLException
	 */
	public int getPoints(GameMessage msg) throws SQLException {
		Statement st = dbConnection.createStatement();
		ResultSet r = st.executeQuery("SELECT points FROM questionDB.users WHERE user = '"+ msg.clientUsername +"';");
		System.out.println(msg.clientUsername);
		int points = -1;
		if(r != null) {
			while(r.next()) {

				points = Integer.parseInt(r.getString(1));
			}
			
		}
		st.close();
		return points;
	}
	
	
	/**
	 * Updates the points in the database
	 * <p>
	 * updates the database with the amount of points a player has. First gets the points from the database. Then 
	 * adds the points base on the msg.points (the amount of points to add), finally updates the points with the new value.
	 * @param GameMessage msg -
	 * <ul>
	 * <li> msg.userName = what player to add points to
	 * <li> msg.points = amount of points to add to the player
	 * @throws SQLException
	 */
	public void updatePoints(GameMessage msg) throws SQLException {
		int p = getPoints(msg);
		p += msg.points;
		Statement st = dbConnection.createStatement();
		boolean r = st.execute("UPDATE `questionDB`.`users` SET `points` = '" + p + "' WHERE (`user` = '" + msg.clientUsername+ "');");
		if(r) {
			System.out.println("New player Created");
		}
		st.close();
		
		
	}
	
	
	
	
	/**
	 * Trying to login the specific player
	 * <p>
	 * Trying to login the spcific player based on the userName and the password. Loops through all the users in the database. 
	 * If the player does not exist the player is created with that username and password.
	 * @param String userName - The username 
	 * @param String password - The password
	 * @return int points - amoung of points the player has
	 * <ul>
	 * <li> int points - if the player exists
	 * <li> -2 = the username and password does not match
	 * <li> -1 = creates a new username with the given password
	 * 
	 */
	public int testLogin(String userName,String password) {
		try {
			users = new ArrayList<User>();
			Statement st = dbConnection.createStatement();
			ResultSet r = st.executeQuery("SELECT * FROM questionDB.users;");
			while (r.next()) {
				User user = new User();
				user.userName = r.getString("user");
				user.password = r.getString("password");
				user.points = r.getInt("points");
				users.add(user);
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(User u: users) {
			if(u.userName.equals(userName) && u.password.equals(password)) {
				System.out.println("DB returns " + u.points);
				return u.points;
			}else if(u.userName.equals(userName) && !u.password.equals(password)) {
				System.out.println("DB returns " + (-2));
				return -2;
			}
		}
		try {
			Statement st = dbConnection.createStatement();
			boolean worked = st.execute("INSERT INTO `questionDB`.`users` (`user`, `password`) VALUES ('"+ userName +"', '"+ password +"');");
			System.out.println("Inserted is " + worked );
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("DB returns " + (-1));
		return -1;
	}
}
