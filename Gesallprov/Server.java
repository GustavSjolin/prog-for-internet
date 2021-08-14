package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Server that is looking for new Socket connections.
 *<p> 
 *When a new connections is made a Thread of a new ServerClient is created and started.
 * @see ServerClient
 * @author Gustav Sjölin
 *
 */
public class Server extends Thread {
	private GameHandler mon;

	
	/**
	 * Constructor liks together the  GameHandler
	 * 
	 * @see GameHandler
	 * @param mon
	 */
	public Server(GameHandler mon) {
		this.mon = mon;
	}

	
	/**
	 * The run-method that listens for new Socket connections
	 */
	public void run() {
		int port = 2000;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("SERVER IS UP AND RUNNING ON PORT " + port);
			int id = 1;
			while (!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				ServerClient player = new ServerClient(clientSocket, mon, id);

				System.out.println("NEW CLIENT CONNECTED WITH IP: " + clientSocket.getInetAddress().getHostName());

				Thread t = new Thread(player);
				mon.addPlayer(player);
				t.start();
				id++;
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * The main method for the Server side
	 *
	 * @param args string is not used for anything
	 */
	public static void main(String[] args) {
		try {
			DatabaseHandler dbHandle = new DatabaseHandler();
			GameHandler mon = new GameHandler(dbHandle);
			Server server = new Server(mon);
			server.start();
			Thread game = new GameThread(mon);
			Thread.sleep(3000);
			game.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
