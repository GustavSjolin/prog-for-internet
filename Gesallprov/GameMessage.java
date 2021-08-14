package server;

import java.io.Serializable;
/**
 * Message to be sent between Input and Output streams
 * <p>
 * <ul>
 * <li> String message = text to be displayed/answer to question
 * <li> String from	= Who sent the message. A client or the server
 * <li> String clientUsername = The username for the client
 * <li> String password = The password corresponding with the username
 * <li> int id = The id of the ServerClient
 * <li> long points = Amount of points given to the user
 * <li> boolean login = is the player loged in or not
 * <li> MessageType = what type of message it is
 * @see MessageType
 * @author Gustav Sjölin
 *
 */
public class GameMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	public String message;
	public String from;
	public int id;
	public long points;
	public String clientUsername;
	public String password;
	public boolean login;
	public MessageType type;
	
	

}
