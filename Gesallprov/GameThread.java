package server;
/**
 * A Thread to send questions to the monitor.
 * @author Gustav Sjölin
 *
 */
public class GameThread extends Thread {

	private GameHandler mon;
	
	/**
	 * Constructor
	 * 
	 * <p>
	 * Links the internal GameHandler with the provided GameHandler
	 * @param mon Links with internal GameHandler
	 */
	public GameThread(GameHandler mon) {
		this.mon = mon;
		
	}
	/*
	 * Run method for sending quesions to the GameHandler
	 */
	public void run() {
		while (true) {
			try {
				mon.sendQuestion();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
