import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * A Runnable that listens on the input stream.
 * 
 * @author Gustav Sjölin
 *
 */
public class ClientHandler implements Runnable {
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private Server server;
	
	/**
	 * Links the internal and external Socket and server.
	 * @param Socket clientSocket - the connected socket
	 * @param Server server - the running server
	 */
	public ClientHandler(Socket clientSocket, Server server) {
		this.clientSocket = clientSocket;
		this.server = server;

	}
	
	/**
	 * Sends a message to the outputstream.
	 * @param String msg - message to be sent
	 */
	public void write(String msg) {
		out.println(msg);
		out.flush();
	}
	
	/**
	 * Run method
	 * <p>
	 * Waits for the input stream to have a string to read. Prints information about the Socket and the message from the
	 * input stream.
	 */
	public void run() {
		String msg;
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);

			while ((msg = in.readLine()) != null) {
				System.out.println("CLIENT: " +clientSocket.getInetAddress().getHostName() +" BROADCAST:" + msg);
				server.sendMessageToAll(msg);
			}
			
			
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.killThread(this);
		
	}

}


