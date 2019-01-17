package gui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import localText.Contact;
import localText.Notification;
import main.Main;

/**
 * Panel for selecting which mode to open.
 * @author jordan
 *
 */
public class ModeSelect extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton contactsMode;
	private JButton sendText;
	public CardLayout cardLayout;
	private JPanel notificationsPanel;
	private JButton clearAll;
	public ModeSelect(CardLayout cardLayout) {
		super();
		setLayout(new GridLayout(1,2));
		this.cardLayout = cardLayout;
		
		//Initializing and adding the left ide panel.
		JPanel leftSidePanel = new JPanel();
		leftSidePanel.setLayout(new GridLayout(2,1));
		
		contactsMode = new JButton("Select Contact");
		contactsMode.addActionListener(this);
		leftSidePanel.add(contactsMode);
		
		
		sendText = new JButton("Send Text");
		sendText.addActionListener(this);
		leftSidePanel.add(sendText);
		this.add(leftSidePanel);
		
		//Panel for our notifications from sending/receiving a message.
		notificationsPanel = new JPanel();
		notificationsPanel.setLayout(new BoxLayout(notificationsPanel,BoxLayout.Y_AXIS));
		JScrollPane notifSP = new JScrollPane(notificationsPanel);
		notifSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		notifSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(notifSP);
		
		//Clear notifications button
		clearAll = new JButton("<html><b>Clear Notifications!</b></html>");
		clearAll.addActionListener(this);
		notificationsPanel.add(clearAll);
	}
	/**
	 * Updates the user on recent notifications.
	 * @param action - weather the notification is about a received or sent text, for example.
	 * @param contactName - 
	 * @param message
	 */
	public void addNotification(String action,Contact contact,String message){
		Notification newNotification = new Notification(action,contact,message);
		JButton notificationButton = newNotification.getButton();
		notificationButton.addActionListener(this);
		notificationsPanel.add(notificationButton);
	}
	/**
	 * Action listener for this panel.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == contactsMode) {
			cardLayout.show(Main.backPanel,Main.CONTACT_PANEL);
		}
		else if(e.getSource() == sendText) {
			cardLayout.show(Main.backPanel,Main.TEXT_PANEL);
		}
		else if(e.getSource() == clearAll) {
			notificationsPanel.removeAll();
			notificationsPanel.add(clearAll);
			notificationsPanel.repaint();
			notificationsPanel.revalidate();
		}
		//First of two action listeners for notification buttons.
		//The other one comes from the notification class.
		else {
			notificationsPanel.remove((JButton)e.getSource());
		}
	}
	
}

