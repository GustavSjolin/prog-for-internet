import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Temporary database for storing 
 * @author Gustav Sjölin
 *
 */
public class TempDB {

	private StringBuilder sb;
	private StringBuilder newRecord;
	
	/**
	 * Creating two Stringbuilders for datakeeping.
	 */
	public TempDB() {
		sb = new StringBuilder();
		newRecord = new StringBuilder();
	}
	/**
	 * Saves the data from the database to this database.
	 * <p>
	 * Called from the DatabaseHandler when the data from the database is fetched.
	 * @param id
	 * @param name
	 * @param epost
	 * @param website
	 * @param comment
	 */
	public void saveValues(String id, String name, String epost, String website, String comment) {
		sb.append(id + ',');
		sb.append(name + ',');
		sb.append(epost + ',');
		sb.append(website + ',');
		sb.append(comment + ',');
		sb.append('\n');
	}
	/**
	 * Saves the data from the GUI to this database.
	 * <p>
	 * Should be called from the GUI when a new Post is created.
	 * @param name
	 * @param epost
	 * @param website
	 * @param comment
	 */
	public void addPost(String name, String epost, String website, String comment) {
		newRecord.append(name + ',');
		newRecord.append(epost + ',');
		newRecord.append(website + ',');
		newRecord.append(comment + ',');
		newRecord.append('\n');
		
	}
	
	/**
	 * Converting the inputs to an output-form.
	 * <p>
	 * Taking the inputs and creating a Post that looks better for the user.
	 * @param id
	 * @param name
	 * @param epost
	 * @param website
	 * @param comment
	 * @return String of a post in output-form.
	 */
	public String standardForm(String id,String name, String epost, String website, String comment) {
		StringBuilder tempString = new StringBuilder();
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
//		LocalDateTime now = LocalDateTime.now();  
		tempString.append("#####################\n");
		tempString.append("Post Number: " + id + "\n");
//		tempString.append("Time: " + dtf.format(now) + "\n");
		tempString.append("Name: " + name + "\n" );
		tempString.append("E-mail: " + epost + "\n" );
		tempString.append("Homepage: " + website + "\n" );
		tempString.append("Comment: " + comment + "\n" );
		tempString.append("#####################\n");
		tempString.append("\n");
		return tempString.toString();
		
	}
	
	/**
	 * Returns a string with all content.
	 * <p>
	 * All the posts are returned as a string.
	 * 
	 * @return String with all the posts.
	 */
	public String toString() {
		StringBuilder allPost = new StringBuilder();
		String[] posts = sb.toString().split("\n");
		
		for(String row: posts) {
			String[] data = row.split(",");
			allPost.append(standardForm(data[0],data[1],data[2],data[3],data[4]));
		}
		
		if(!newRecord.toString().equals("")) {
			String[] newPosts = newRecord.toString().split("\n");
			int id = Integer.parseInt(DatabaseHandler.lastID);
			id++;
			for(String row: newPosts) {
				String[] data = row.split(",");
				allPost.append(standardForm(Integer.toString(id),data[0],data[1],data[2],data[3]));
				id++;
			}
			
			
		}
		
		
		
		
		return allPost.toString();
	}
	
	/**
	 * Returning a StringBuilder of the new posts.
	 * 
	 * @return StringBuilder of the new posts.
	 */
	public StringBuilder getnewRecord() {
		return newRecord;
	}
	
	
}
