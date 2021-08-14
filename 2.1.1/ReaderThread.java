import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
/**
 * A thread that listens on the input stream.
 * 
 * 
 * @author Gustav Sjölin
 *
 */
public class ReaderThread extends Thread{
	private Socket socket;
	public ReaderThread(Socket socket) {
		this.socket = socket;
	}
	/**
	 * The runmethod
	 * <p>
	 * The method waits until there is something to get in the input stream. Then prints it in the terminal.
	 */
	public void run() {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				
				System.out.println(in.readLine());					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
