package server;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Monitor for the game.
 * <p>
 * Keeps track of all the calls and all the logic.
 * 
 * 
 * @author Gustav Sjölin
 *
 */
public class GameHandler {
	private ArrayList<ServerClient> playerList;
	private String correctAnswer;
	private boolean isCorrect;
	private boolean timeUp;
	private boolean ready;
	private long endTime;
	private DatabaseHandler db;

	
	/**
	 * Constructor
	 * <p>
	 * Creates a new Player-list.
	 * @param DatabaseHandler db
	 */
	public GameHandler(DatabaseHandler db) {
		this.playerList = new ArrayList<ServerClient>();
		this.isCorrect = false;
		this.timeUp = false;
		this.ready = false;
		this.db = db;
		
	}

	/**
	 * A player is added to the player-list.
	 * @param ServerClient player - player to add to the list
	 */
	public synchronized void addPlayer(ServerClient player) {
		playerList.add(player);
		notifyAll();
	}

	/**
	 * Remove the player from the player-list
	 * @param ServerClient player - player to be removed from the list
	 */
	public synchronized void disconnect(ServerClient player) {
		if (playerList.contains(player)) {
			playerList.remove(player);
			notifyAll();
		}
	}

	/**
	 * Internally used to send message to everyone on the player-list.
	 * @param msg - Message to be sent to everyone.
	 */
	private synchronized void writeAll(GameMessage msg) {
		if (!playerList.isEmpty()) {
			for (ServerClient p : playerList) {
				p.write(msg);
			}
		}
	}
	/**
	 * Internally used to send message to a specific ServerClient on the player-list.
	 * <p>
	 * The message is sent to the ServerClient with the same id as the msg.id
	 * @param msg - Message to be sent.
	 */
	private synchronized void write(GameMessage msg) {
		for(ServerClient p: playerList) {
			if(p.getId() == msg.id) {
				p.write(msg);
			}
		}
	}
	

	/**
	 * Checks if the incomming message has the correct answer.
	 * <p>
	 * The message is then sent to all the ServerClients on the list.
	 * @param msg
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public synchronized void checkAnswer(GameMessage msg) throws InterruptedException, SQLException {
		while (!ready) {
			wait();
		}
		if (msg.message.equalsIgnoreCase(correctAnswer) && !isCorrect && !timeUp) {
			msg.points = (endTime - System.currentTimeMillis()) /1000;
			msg.message = "Good job " + msg.from + "! The correct answer was: " + msg.message + "! Next questions arrives in 5 seconds.";
			msg.from = "Server";
			isCorrect = true;
			timeUp = false;
			db.updatePoints(msg);
			notifyAll();
		}
		msg.type = MessageType.WRITE_MESSAGE_TO_ALL;
		
		writeAll(msg);

	}
	
	
	/**
	 * Gets and sends the points of a specific client.
	 * @param GameMessage msg - Gets the point for the msg.clientUsername 
	 */
	public synchronized void showPoints(GameMessage msg) {
		GameMessage newMessage = new GameMessage();
		try {
			int p = db.getPoints(msg);
			newMessage.message = msg.clientUsername + " has " + p + " points ";
			newMessage.from = "Server";
			newMessage.type = MessageType.WRITE_MESSAGE_TO_ALL;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeAll(newMessage);
		
		
		
	}
	/**
	 * Tests if the player is in the database.
	 * <p>
	 * Take the input msg and checks the DB if there is a player with the correct username and password.
	 *  Then sends a message with the information. If the playername does not exist a new player with the name is created.
	 * @param GameMessage msg - The message with clientName and password for the desired player. 
	 */
	public synchronized void testLogin(GameMessage msg) {
		boolean isActive = false;
		for(ServerClient p: playerList) {
			if(p.getName().equals(msg.clientUsername)) {
				isActive = true;
				
			}
		}
		GameMessage m = msg;
		m.from = "Server";
		int a = 0;
		if(isActive) {
			//Player already logged in
			m.login = false;
			
		}else {
			a = db.testLogin(msg.clientUsername, msg.password);
			if(a == -1) {
				//Created new player
				m.points = 0;
				m.login = true;
			}else if(a == -2) {
				//Username and password did not match
				m.login = false;
			}else {
				m.points = a;
				m.login = true;
			}
			m.id = msg.id;
		}
		write(m);
	}
	
	
	/**
	 * Sends the next Question to the clients.
	 * <p>
	 * 
	 * Sends question to the clients. Also sends hints if the Question is not answered correctly.
	 * @throws InterruptedException
	 */
	public synchronized void sendQuestion() throws InterruptedException {
		Question q = db.getNextQuestion();
		if (q.answer == null || q.question == null) {
			System.out.println("Question or answer was null");
			return;
		}

		while (playerList.size() == 0) {
			wait();
		}

		correctAnswer = q.answer;

		ready = true;
		GameMessage msg = new GameMessage();
		msg.message = q.question;

		msg.from = "Server";
		msg.type = MessageType.WRITE_MESSAGE_TO_ALL;
		writeAll(msg);

		long timeNow = System.currentTimeMillis();
		endTime = timeNow + 30000;
		long hint1 = timeNow + 10000;
		long hint2 = timeNow + 20000;
		boolean h1 = false;
		boolean h2 = false;

		while ((!isCorrect && !timeUp)) {

			if (System.currentTimeMillis() < hint1 && !isCorrect) {
				wait(hint1 - System.currentTimeMillis());
			} else if (System.currentTimeMillis() > hint1 && System.currentTimeMillis() < hint2 && !isCorrect && !h1) {
				GameMessage hint1msg = new GameMessage();
				char[] ans = q.answer.toCharArray();
				String hint = "";
				for (int i = 0; i < ans.length; i++) {
					if (Character.isWhitespace(ans[i])) {
						hint += " ";
					} else {
						hint += "_";
					}
				}
				hint1msg.message = "Hint 1 - " + hint;
				hint1msg.from = "Server";
				hint1msg.type = MessageType.WRITE_MESSAGE_TO_ALL;
				
				if (!playerList.isEmpty()) {
					writeAll(hint1msg);
				}
				h1 = true;
			}

			if (System.currentTimeMillis() < hint2 && System.currentTimeMillis() > hint1 && h1 && !isCorrect) {
				wait(hint2 - System.currentTimeMillis());
			} else if (System.currentTimeMillis() > hint2 && System.currentTimeMillis() < endTime && !isCorrect && !h2) {
				GameMessage hint2msg = new GameMessage();
				char[] ans = q.answer.toCharArray();
				String hint = "";

				for (int i = 0; i < ans.length; i++) {
					if (Character.isWhitespace(ans[i])) {
						hint += " ";
					} else if (i % 3 == 0) {
						hint += ans[i];
					} else {
						hint += "_";
					}
				}
				hint2msg.message = "Hint 2 - " + hint;
				hint2msg.from = "Server";
				hint2msg.type = MessageType.WRITE_MESSAGE_TO_ALL;
				if (!playerList.isEmpty()) {
					writeAll(hint2msg);
				}
				h2 = true;
			}

			if (System.currentTimeMillis() < endTime && System.currentTimeMillis() > hint2 && h2 && !isCorrect) {
				wait(endTime - System.currentTimeMillis());
			} else if (System.currentTimeMillis() > endTime && !isCorrect) {
				timeUp = true;
				notifyAll();
				GameMessage endMess = new GameMessage();
				endMess.message = "Time is up! Correct answer was: " + q.answer;
				endMess.from = "Server";
				endMess.type = MessageType.WRITE_MESSAGE_TO_ALL;
				if (!playerList.isEmpty()) {
					writeAll(endMess);
				}
			}
		}
		timeNow = System.currentTimeMillis();
		long nextQuestionInFiveSeconds = timeNow + 5000;
		
		while(nextQuestionInFiveSeconds > System.currentTimeMillis()) {
			wait(nextQuestionInFiveSeconds - System.currentTimeMillis());
		}
		
		
		isCorrect = false;
		timeUp = false;

		notifyAll();

	}

}
