/**
 * A thread that prints info every 5th second
 * @author Gustav Sjölin
 *
 */
public class T1 extends Thread{

	/**
	 * Prints "T1: Tråd T1" every second
	 */
	public void run() {
		boolean go = true;
		while(go) {
			System.out.println("T1: Tråd T1");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				go = false;
				
			}
		}
	}
	
	
}
