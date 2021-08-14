import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;
import java.util.*;

/**
 * Generates a frame for the user to draw on
 * @author Gustav Sjöon
 *
 */
public class Draw extends JFrame {
  private Paper p;

  /**
   * Adds a point to the paper
   * @param Point point - point to be added
   */
  public  void addPoint(Point point) {
	  p.addPoint(point);
  }
  
  /**
   * Main method
   * <p>
   * Opens a new Socket. When two are connected the points are sent to the other and the image is presented on both the screens.
   * @param String[] args - first index is this port. Second index is the port to be connected to.
   */
  public static void main(String[] args) {
    
    int myPort = Integer.parseInt(args[0]);
    String remoteHost = args[1];
    int remotePort = Integer.parseInt(args[2]);
    try {
		DatagramSocket dgs = new DatagramSocket(myPort,InetAddress.getByName(remoteHost));
		dgs.connect(InetAddress.getByName(remoteHost), remotePort);
		System.out.println(remoteHost + " ON PORT " + myPort + " CONNECTED TO " + remotePort);
		Draw draw = new Draw(dgs);
		Thread reader = new Thread() {
			public void run() {
				int len = 1024;
				byte[] bb = new byte[len];
				DatagramPacket pack;
				while(true) {
					try {
						pack = new DatagramPacket(bb,bb.length);
						dgs.receive(pack);
						byte[] b = pack.getData();
						String msg = new String(b);
						String[] xy = msg.split(",");
						Point point = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])); 
						draw.addPoint(point);
						Thread.sleep(1);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		};
		
		reader.start();
		
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  /**
   * Opens a GUI to be drawn on
   * @param Socket socket - Connects this internal Socket with the external.
   */
  public Draw(DatagramSocket socket) {
	this.p = new Paper(socket);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    getContentPane().add(p, BorderLayout.CENTER);

    setSize(640, 480);
    setVisible(true);
    
    
  }
}
/**
 * The paper to be drawn on
 * @author Gustav Sjölin
 *
 */
class Paper extends JPanel {
  private HashSet hs = new HashSet();
  private DatagramSocket dgs;

  /**
   * Constructor
   * @param DatagramSocket dgs - links the external and internal Datagramsocket
   */
  public Paper(DatagramSocket dgs) {
    setBackground(Color.white);
    addMouseListener(new L1());
    addMouseMotionListener(new L2());
    this.dgs = dgs;
  }
  
  /**
   * Draws the components on the GUI.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.black);
    Iterator i = hs.iterator();
    while(i.hasNext()) {
      Point p = (Point)i.next();
      g.fillOval(p.x, p.y, 2, 2);
    }
  }

  /**
   * Adds a point to the Hashset.
   * @param Point p - point to be added
   */
  public synchronized void addPoint(Point p) {
    hs.add(p);
    repaint();
  }
  
  /**
   * Sends the point to the other Frame to be drawn.
   * @param Point p - point to be sent.
   */
  public void sendPoint(Point p) {
	  String message = Integer.toString(p.x) + "," + Integer.toString(p.y) + ", 0";
	  byte[] msg = message.getBytes();
	  try {
		dgs.send(new DatagramPacket(msg,msg.length));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  class L1 extends MouseAdapter {
	  /**
	   * Adds and sends a point everytime it is called
	   */
    public void mousePressed(MouseEvent me) {
      addPoint(me.getPoint());
      sendPoint(me.getPoint());
    }
  }

  class L2 extends MouseMotionAdapter {
	  /**
	   * Adds and sends a point everytime it is called
	   */
    public void mouseDragged(MouseEvent me) {
      addPoint(me.getPoint());
      sendPoint(me.getPoint());
    }
  }
}