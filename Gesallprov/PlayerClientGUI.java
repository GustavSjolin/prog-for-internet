package server;

import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;



/** The client-side program
 * <p>
 * Opens up a Socket connection to the Server-side. A GUI is created for interactions with the Server and the other connected clients.
 * A login is required, which will be created if not exists.
 *
 * @author Gustav Sjölin
 *
 */
@SuppressWarnings("serial")
public class PlayerClientGUI extends JFrame{
	private JPanel inputPanel;
	private JTextArea outputTextArea;
	private JTextField inputTextField;
	private JButton sendButton;
	private JScrollPane scroll;
	private String name = "startname";
	private final String startText = "Welcome to Quizpeed! \nYou have 30 seconds to answer each question.\nHints will be given every 10 secons."
			+ " \nThe faster you answer the more points you will get.\nGood luck!\nType \"!points\" to show your score!\n";
	
	private Socket socket;
	private String address = "127.0.0.1";
	private int port = 2000;
	
	private boolean isLoggedIn = false;
	private boolean gotRespons = false;
//	private BufferedReader in;
//	private PrintWriter out;
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private final int TEXT_LENGTH = 60;
	
	
	/**
	 * The constructor 
	 * <p>
	 * Starts up a Socket connection. Then Starts a new Thread for listening to the input steam.
	 * 
	 * @param values a String vector - first element is which adress to connect to the second is which port. If none is passed the default will be "127.0.0.1"
	 * and port 2000. If only one is passed it will change the adress.
	 * 
	 */
	public PlayerClientGUI(String[] values) {
		
		if (values.length == 1) {
			address = values[0];
		} else if (values.length == 2) {
			address = values[0];
			port = Integer.parseInt(values[1]);
		}
		try {
			socket = new Socket(InetAddress.getByName(address), port);
//			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new ObjectOutputStream(socket.getOutputStream());
			in =  new ObjectInputStream(socket.getInputStream());
		
			new Thread(()->{
				boolean run = true;
				while(run) {
					try {
						GameMessage msg  = (GameMessage) in.readObject();
						
						switch (msg.type) {
						case WRITE_MESSAGE_TO_ALL:
							if(isLoggedIn) {
								addText(msg);								
							}
							break;
						case TEST_LOGIN:
							if(!isLoggedIn) {
								giveRespons(msg);								
							}
						default:
							break;
						}
						
						
						Thread.sleep(1);
					} catch (IOException e) {
						run = false;
					}catch (InterruptedException e){
						run = false;
					} catch (ClassNotFoundException e) {
						run = false;
						e.printStackTrace();
					}
				}
			}).start();
			
			try {
				login();
			} catch (ClassNotFoundException | InterruptedException e1) {
				e1.printStackTrace();
			}
			initGUI();
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void exit() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("In and/or Out is bad");
			e.printStackTrace();
		}
		System.out.println("CLIENT EXIT");
		dispose();
		System.exit(0);
	}
	
	/**
	 * To add text to the textArea 
	 *
	 * @param GameMessage m - The message to display on the screen.
	 */
	private void addText(GameMessage m) {
		String preText = outputTextArea.getText();
		String outputString = m.from + ": ";
		String part = m.message;
		while(part.length() > TEXT_LENGTH) {
			outputString += part.substring(0, TEXT_LENGTH) + "\n";
			part = part.substring(TEXT_LENGTH);
		}
		outputString += part + "\n";
		
		outputTextArea.setText(preText + outputString);
		SwingUtilities.invokeLater(()->{
			JScrollBar vertical = scroll.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
			
		});
	}
	
	private void giveRespons(GameMessage msg) {
		isLoggedIn = msg.login;
		gotRespons = true;
		
	}
	
	
	/**
	 * Push a message to the output stream
	 * 
	 * 
	 * @param GameMessage msg - The message to be sent to the Server and the other clients.
	 */
	private void write(GameMessage msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Called to handle the login
	 * <p>
	 * A GUI is created to handle the login. Will check with the database if the account exists.
	 * If not it will create a new login with the inputed password.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void login() throws ClassNotFoundException, IOException, InterruptedException {
		
		JPanel panel = new JPanel(new BorderLayout(5, 5));
	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel("Username", SwingConstants.RIGHT));
	    label.add(new JLabel("Password", SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    JTextField username = new JTextField(20);
	    controls.add(username);
	    JPasswordField password = new JPasswordField(20);
	    controls.add(password);
	    panel.add(controls, BorderLayout.CENTER);
	    
	   
	   
	    while(!isLoggedIn) {
	    	int option = JOptionPane.showConfirmDialog(null, panel, "login", JOptionPane.OK_CANCEL_OPTION);
	    	if(option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION && false){
	    		
	    		exit();
	    	}else if ( option == JOptionPane.OK_OPTION) {
	    		name = username.getText();
	    		String pass = new String(password.getPassword());
	    		GameMessage msg = new GameMessage();
	    		msg.clientUsername = name;
	    		msg.password = pass;
	    		msg.type = MessageType.TEST_LOGIN;
	    		gotRespons = false;
	    		write(msg);
	    		
	    		while(!gotRespons) {
	    			Thread.sleep(100);
	    		}	
	    	}
	    }		
	}
	
	/**
	 * Initilizing the GUI
	 * 
	 * <p>
	 * The GUI to send and display messages.
	 */
	private void initGUI() {
		
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			setLayout(new BorderLayout());
			inputPanel = new JPanel(new BorderLayout());
			outputTextArea = new JTextArea(20,5);
			outputTextArea.setText(startText);
			outputTextArea.setEditable(false);
			inputTextField = new JTextField();
			inputTextField.setColumns(30);
			sendButton = new JButton("Send answer");
			scroll = new JScrollPane(outputTextArea);
			inputPanel.add(inputTextField,BorderLayout.WEST);
			inputPanel.add(sendButton,BorderLayout.EAST);
			add(scroll, BorderLayout.NORTH);
			add(inputPanel,BorderLayout.SOUTH);
			scroll.setAutoscrolls(true);
			
			revalidate();
			repaint();
			getRootPane().setDefaultButton(sendButton);
			pack();
			setVisible(true);
			
			sendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String answer = inputTextField.getText();
					GameMessage m = new GameMessage();
					
					if(answer.equals("!points")) {
						m.type = MessageType.SHOW_POINTS;
						
					}else {
						m.type = MessageType.CHECK_ANSWER;
					}
					
					m.message = answer;
					m.from = name;
					m.clientUsername = name;
					
					
					
					write(m);
					inputTextField.setText("");
				}
			});
			
			addWindowListener(new WindowAdapter() {
				 @Override
				    public void windowClosing(WindowEvent event) {
				        exit();
				    }
			});
	}
	
	
	/**
	 * The main method. Creates a new PlayerClientGUI that connects to the server.
	 * @param args - Input the adress and the port. Default will be "127.0.0.1" and 2000.
	 */
	public static void main(String[] args){
		
		@SuppressWarnings(value = { "unused" })
		PlayerClientGUI gui = new PlayerClientGUI(args);
		
		
		
	}

}
