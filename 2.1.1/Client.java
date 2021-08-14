
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.util.Scanner;

/**
 * The client side of a Chatprogram
 * @author Gustav Sjölin
 *
 */
public class Client extends Thread{
	private Socket socket;
	public Client(Socket socket) {
		this.socket = socket;
	}

	/**
	 * The run method
	 * 
	 * <p> 
	 * Waits until there is a something in the input stream. Then prints it in the terminal.
	 */
	public void run() {
		BufferedReader in;
		boolean run = true;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(run) {
				try {
					System.out.println(in.readLine());				
					Thread.sleep(1000);				
				} catch (InterruptedException e) {
					run = false;
					
				}
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * The main method 
	 * <p>
	 * Starts up the Socket connection. Default is "127.0.0.1" and port 2000. In args both the adress and the port can be changed.
	 * 
	 * Contains a loop that waits for input from the terminal. Then sends that input to the outstream.
	 * @param values - String values[] - first element will change the adress. Second will change the port
	 */
	public static void main(String[] values) {
		Socket socket;
		String address = "127.0.0.1";
		Thread client;
		Scanner scan = new Scanner(System.in);
		PrintWriter out;
		int port = 2000;
		if (values.length == 1) {
			address = values[0];
		} else if (values.length == 2) {
			address = values[0];
			port = Integer.parseInt(values[1]);
		}

		try {
			socket = new Socket(InetAddress.getByName(address), port);
			client = new Client(socket);
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
			client.start();
			
			String s = "";
			while (!s.equals("quit")) {
				s = scan.nextLine();
				out.println(s);
				out.flush();
			}
			out.close();
			client.interrupt();
			socket.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
