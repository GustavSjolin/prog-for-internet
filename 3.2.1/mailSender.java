import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;


/**
 * Sends mail
 * @author Gustav Sjölin
 *
 */
public class mailSender {
	
	public void mailSener() {
		
	}
	
	/**
	 * Used to send mail
	 * <p>
	 * Sending an email with the declared inputs.
	 * @param server - Only need to specify the domain (ex gmail, hotmail etc.).
	 * @param user - The emailadress to send from (ex example@gmail.com).
	 * @param password - Password for the email.
	 * @param sendTo - Receiver of the email.
	 * @param subject - Title of the mail.
	 * @param text - The message for the receiver.
	 */
	public void sendMail(String server, String user, String password, String sendTo, String subject, String text) {
		
	    Properties props = new Properties();
	    if(server.equalsIgnoreCase("gmail")) {
	    	props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host	    	
	    }else {
	    	System.out.println("No valid server found");
	    	return;
	    }
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); //SMTP Port
	    Session session = Session.getInstance(props,
	      new javax.mail.Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(user, password);
	            }
	      });
	    
	    try {
	        MimeMessage msg = new MimeMessage(session);
	        msg.setFrom(user);
	        msg.setRecipients(Message.RecipientType.TO,
	                          sendTo);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        msg.setText(text);
	        Transport.send(msg, user, password);
	        System.out.println("Email sent");
	    } catch (MessagingException mex) {
	        System.out.println("send failed, exception: " + mex);
	    }
	}
}
