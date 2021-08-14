/**
 * A Runnable that prints info every 5th second
 * @author Gustav Sjölin
 *
 */
public class T2 implements Runnable{

	/**
	 * Prints "T1: Tråd T1" every second
	 */
	public void run() {
		// TODO Auto-generated method stub
		boolean go = true;
		while(go) {
			System.out.println("T2: Tråd T2");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				go = false;
				
			}
		}
		
	}

	
	
	
}
