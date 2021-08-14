import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Generates a GUI to be able to send mail
 * @author Gustav Sjölin
 *
 */
@SuppressWarnings("serial")
public class GUI extends JFrame{
	private final int colSize = 30;
	private final int borderThickness = 2;
	private JPanel upper;
	private JPanel down;
	private JPanel serverPanel;
	private JPanel usernamePanel;
	private JPanel passwordPanel;
	
	
	private JTextField serverTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	
	private JLabel serverLabel;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	
	
	private JPanel toPanel;
	private JPanel subjectPanel;
	private JPanel messagePanel;
	
	private JLabel toLabel;
	private JLabel subjectLabel;
	private JLabel messageLabel;
	
	private JTextField toTextField;
	private JTextField subjectTextField;
	private JTextArea messageTextArea;
	
	private JButton sendButton;
	private mailSender mailsender;
	
	/**
	 * Creating an GUI for sending mail.
	 * 
	 * 
	 */
	public GUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(3,1));
		this.mailsender = new mailSender();
		
		
		upper = new JPanel(new GridLayout(3,1));
		upper.setBorder(BorderFactory.createLineBorder(Color.black,borderThickness));
		
		
		serverPanel = new JPanel(new BorderLayout());
		usernamePanel = new JPanel(new BorderLayout());
		passwordPanel = new JPanel(new BorderLayout());
		
		serverTextField = new JTextField();
		serverTextField.setColumns(colSize);
		usernameTextField = new JTextField();
		usernameTextField.setColumns(colSize);
		passwordTextField = new JPasswordField(10);
		passwordTextField.setColumns(colSize);
		
		serverLabel = new JLabel("Server: ");
		usernameLabel = new JLabel("Email: ");
		passwordLabel = new JLabel("Password: ");
		
		serverPanel.add(serverLabel,BorderLayout.WEST);
		serverPanel.add(serverTextField,BorderLayout.EAST);
		
		usernamePanel.add(usernameLabel,BorderLayout.WEST);
		usernamePanel.add(usernameTextField,BorderLayout.EAST);
		
		passwordPanel.add(passwordLabel,BorderLayout.WEST);
		passwordPanel.add(passwordTextField,BorderLayout.EAST);
		
		
		upper.add(serverPanel);
		upper.add(usernamePanel);
		upper.add(passwordPanel);
		
		
		add(upper);
		
		down = new JPanel(new GridLayout(3,1));
//		
		toPanel = new JPanel(new BorderLayout());
		subjectPanel = new JPanel(new BorderLayout());
		messagePanel = new JPanel(new BorderLayout());
		
		toLabel = new JLabel("To: ");
		subjectLabel = new JLabel("Subject: ");
		messageLabel = new JLabel("Message: ");
		
		
		toTextField = new JTextField();
		toTextField.setColumns(colSize);
		subjectTextField = new JTextField();
		subjectTextField.setColumns(colSize);
		messageTextArea = new JTextArea(5,20);
		
		
		
		sendButton = new JButton("SEND MAIL");
		
		toPanel.add(toLabel,BorderLayout.WEST);
		toPanel.add(toTextField,BorderLayout.EAST);
		
		subjectPanel.add(subjectLabel,BorderLayout.WEST);
		subjectPanel.add(subjectTextField,BorderLayout.EAST);

		messagePanel.add(messageLabel,BorderLayout.NORTH);
		messagePanel.add(messageTextArea,BorderLayout.CENTER);
		messagePanel.add(sendButton,BorderLayout.SOUTH);
		
		down.add(toPanel);
		down.add(subjectPanel);
		down.add(messageLabel);
		
		
		add(down);
		add(messagePanel);
		
		
		//When the send button is pressed.
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String user = usernameTextField.getText();
				String password = new String(passwordTextField.getPassword());
				String server = serverTextField.getText();
				String sendTo = toTextField.getText();
				String subject = subjectTextField.getText();
				String message = messageTextArea.getText();
				mailsender.sendMail( server, user, password, sendTo,subject,message);
				
			}
		});
		
		
		pack();
		setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		
		@SuppressWarnings("unused")
		GUI gui = new GUI();
	}

}
