import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
/**
 * The Server side that controls all the clients
 * @author Gustav Sjölin
 *
 */
public class Server {
	private LinkedList<ClientHandler> clientList;
	
	
	/**
	 * Creates an empty list for all clients
	 */
	public Server() {
		clientList = new LinkedList<ClientHandler>();
		
	}
	/**
	 * Returns the amount of cliets connected to the server
	 * @return int amount of clients
	 */
	public synchronized int getNumberOfClients() {
		return clientList.size();
	}
	/**
	 * Adds a client to the list
	 * @param ClientHandler cl - to be added to the list
	 */
	public void addClients(ClientHandler client) {
		clientList.add(client);
	}
	
	/**
	 * Removes the client from the list
	 * @param ClientHandler cl - to be removed from the list
	 */
	public synchronized void killThread(ClientHandler cl) {
		clientList.remove(cl);
		System.out.println("CLIENT DISCONNECTED");
		System.out.println("CURRENTLY " + getNumberOfClients() + " CLIENTS CONNECTED");
	}
	
	/**
	 * Sends a message to all the clients
	 * @param String msg - message to be sent
	 */
	public synchronized void sendMessageToAll(String msg) {
		for(ClientHandler c: clientList) {
			c.write(msg);
		}
	}
	
	
	/**
	 * Main method
	 * <p>
	 * Starts up the server and looking for connecting clients while it is running. The connected client is put in an own thread and
	 * added to the list. The defult port is 2000. Can be changed with the args parameter.
	 * @param String[] args - first element will change the port for the server
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				int port = 2000;
				if (args.length == 1) {
					port = Integer.parseInt(args[0]);
				}
				try {
					ServerSocket serverSocket = new ServerSocket(port);
					Server server = new Server();
					System.out.println("SERVER UP AND RUNNIGN ON PORT " + port);
					

					while (!serverSocket.isClosed()) {
						Socket clientSocket = serverSocket.accept();
						ClientHandler cl = new ClientHandler(clientSocket,server);
						server.addClients(cl);
						System.out.println("NEW CLIENT CONNECTED WITH IP: " + clientSocket.getInetAddress().getHostName());
						System.out.println("CURRENTLY " + server.getNumberOfClients() + " CLIENTS CONNECTED");
						Thread t = new Thread(cl);
						t.start();

					}

					
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
