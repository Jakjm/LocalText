package localText;
/**
 * Created by jordan on 09/01/18.
 * Class for a contact read from the contacts list.
 * @version January 10th 2018
 * @author Jordan Malek;
 */
public class Contact{
    public String name;
    public String number; //Phone number
    public String id;
    public String messages;
    public static final String RECEIVED = "Them:";
    public static final String SENT = "You:";
    public Contact(String name,String id,String number){
        this.id = id;
        this.name = name;
        this.number = number;
        this.messages = "";
    }
    /**
     * Creates a string version of this contact.
     * @return the string formed by this contact.
     */
    public String toString(){
        return "" + name + "|" + number + "|" + id;
    }
    /**
     * Adds a string message to the contact's message history.
     * @param message - the message that is to be added.
     * @param action - the action that was done to send the message (Received or sent)
     */
    public void addMessage(String message,String action) {
    	if(action.equals(RECEIVED)) {
    		messages += "Them:" + message + "\n";
    	}else if(action.equals(SENT)) {
    		messages += "You:" + message + "\n";
    	}
    }
}
