/**
 * A mainclass only to run a mainmethod
 * <p>
 * Runs two threads that prints every 5th second.
 * @author Gustav Sjölin
 *
 */
public class Main {

	/**
	 * Starts and stops two threads
	 * @see T1 and T2
	 * @param args not used
	 */
	public static void main(String[] args){
		T1 t1 = new T1();
		t1.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		T2 t2 = new T2();
		Thread t = new Thread(t2);
		t.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t1.interrupt();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.interrupt();
	}
}
