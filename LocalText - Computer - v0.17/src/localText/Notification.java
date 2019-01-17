package localText;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import main.Main;

/**
 * Class for a notification that was created by sending or receiving a text message from the client.
 * @author jordan
 * @version 0.14 2018
 */
public class Notification implements ActionListener{
	public String notificationText;
	public String action;
	public Contact contactInvolved;
	private JButton notificationButton;
	public final static String RECEIVED = "Received";
	public final static String SENT = "Sent";
	public Notification(String action,Contact contact,String message) {
		this.contactInvolved = contact;
		String labelText = "";
		if(action.equals(RECEIVED)) {
			labelText = "<html>" + RECEIVED + " text from " + contact.name + ":<br>" + message + "</html>";
		}
		else if(action.equals(SENT)) {
			labelText = "<html>" + SENT + " message to " + contact.name + ":<br>" + message + "</html>";
		}
		notificationButton = new JButton(labelText);
		notificationButton.addActionListener(this);
	}
	
	public JButton getButton() {
		return notificationButton;
	}
	/**
	 * Action listener for our notification button - pulls up the texting window for the 
	 * contact that was involved.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Main.textPanel.updateFor(contactInvolved);
		Main.cardLayout.show(Main.backPanel,Main.TEXT_PANEL);
		
	}
}
