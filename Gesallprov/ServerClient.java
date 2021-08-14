package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * The connection between the client and the server. 
 * 
 * <p>
 * The ServerClient will listed on the input stream and send the correct message based on the MessageType.
 * 
 * @see MessageType
 * @author Gustav Sjölin
 *
 */
public class ServerClient implements Runnable {
	private Socket socket;
	private GameHandler mon;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean disconnected = false;
	private int id;
	private String name = "";
	


	/**
	 * Constructor
	 * <p>
	 * Opens up an out- and input stream for the given Socket. 
	 * @param socket is given Socket from the server. Which socket the client has connected on.
	 * @param mon is the GameHandler (monitor). 
	 * @param id is the uniqe value of this ServerClient.
	 */
	public ServerClient(Socket socket, GameHandler mon, int id) {
		this.socket = socket;
		this.mon = mon;
		this.id = id;
		try {
//			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//			out = new PrintWriter(clientSocket.getOutputStream(), true);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println(
					"Could not open output- or inputstream. Used tried to use socket: " + socket.getInetAddress().getHostName());
			e.printStackTrace();
		}
	}

	
	/**
	 * Writes the desired message to the output stream.
	 * @param GameMessage msg - Desired message to send.
	 */
	public void write(GameMessage msg) {
		if(msg.type == MessageType.TEST_LOGIN && msg.login) {
			name = msg.clientUsername;
		}
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			disconnect();
		}
	}
	
	
	/**
	 * Returns the id of this ServerClient
	 * @return int id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the playes username connected with this serverclient
	 * @return String username
	 */
	public String getName() {
		return name;
	}

	/**
	 * Disconnects this ServerClient
	 */
	private void disconnect() {
		if (!disconnected) {
			mon.disconnect(this);
			disconnected = true;
		}
	}
	
	
	/**
	 * The run-method
	 * 
	 * <p>
	 * Listens to the input stream and sends the message to the desired location base on the MessageType.
	 * @see MessageType
	 */
	@Override
	public void run() {
		GameMessage msg;
		boolean run = true;
		try {
			while (((msg = (GameMessage) in.readObject()) != null) && run) {
				switch (msg.type) {
				case TEST_LOGIN:
					msg.id = id;
					mon.testLogin(msg);
					break;
				case CHECK_ANSWER:
					mon.checkAnswer(msg);
					break;
				case SHOW_POINTS:
					mon.showPoints(msg);
					break;
				default:
						break;
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Thread got interrupted");
			run = false;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound Failed");
			run = false;
			e.printStackTrace();
		} catch (IOException e) {
			
			run = false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load or insert to the Database");
			e.printStackTrace();
		}
		disconnect();
		try {
			out.close();
			in.close();
			socket.close();
			System.out.println("CLIENT DISCONNECTED");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
