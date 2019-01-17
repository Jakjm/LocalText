package main;

import java.awt.CardLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.BasicFrame;
import gui.ContactsPanel;
import gui.ModeSelect;
import gui.OpenAppFrame;
import gui.TextPanel;
import localText.Notification;
import localText.Contact;
import netUtils.Client;
/**
 * Class for building the main functionality of the computer client of the localtext app.
 * @author jordan
 * @version February 20th 2018 v0.16A
 */
public class Main {
	public static ArrayList<Contact> contactList; // The list of contacts. Not necessarily from the phone.
	public static CardLayout cardLayout;
	public static ContactsPanel contactsPanel;
	public static ModeSelect menu;
	public static TextPanel textPanel;
	public static JPanel backPanel;
	public static OpenAppFrame openingFrame;
	//Gui Contstants
	public static final String CONTACT_PANEL = "CONTACT";
	public static final String TEXT_PANEL = "TEXT";
	public static final String MODE_SELECT = "MENU";
	public static Client client;
	public static Thread readThread;
	//Server Constants
	public static final int PORT_NO = 4025;
	//String for the filepath of the notification sound.
	public static final String NOTIFICATION_SOUND_PATH = "/sound.wav";
	//Commands to/from the server.
	public static final String SHUTDOWN = "SHUTDOWN";
	public static final String TEXT = "TEXT";
	//Pincode approval messages.
	public static final String FAIL = "FAIL";
	public static final String OK = "OK";
	public static void main(String[] args){
		openingFrame = new OpenAppFrame(new ConnectButtonListener());
	}
	/**
	 * Starts the program.
	 */
	public static void startProgram() {
		String ip = openingFrame.getIP();
		String pincode = openingFrame.getPincode();
		try {
			client = new Client(ip,PORT_NO);
			//If the program fails to generate a connection, we force the user to try again.
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Failed to connect with the selected ip.");
			e.printStackTrace();
			return;
		}
		//If the program fails to start with the given pascode, we force the user to try again.
		if(!sendPincode(pincode)) {
			JOptionPane.showMessageDialog(null,"Pincode was incorrect.");
			return;
		}
		openingFrame.appOpenFrame.setVisible(false);
		getContactList(client);
		constructGui();
		recieveMessages();
		addHook();
	}
	/**
	 * Sends the pincode to the server.
	 * @return Whether the pincode was accepted or not.
	 */
	public static boolean sendPincode(String pincode) {
		//Getting the pincode.
		client.write(pincode,true);
		String serverApproval = client.read();
		//If the pincode was not acceptable, return false.
		if(serverApproval == null || serverApproval.equals(FAIL)) {
			return false;
		}
		//Otherwise return true.
		else if(serverApproval.equals(OK)) {
			return true;
		}
		//If we've received a random message, return false.
		else {
			return false;
		}
	}
	/**
	 * Adds a shutdown hook to tell server to shutdown before the program closes.
	 */
	public static void addHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if(client.active())client.write(SHUTDOWN, true);
			}
		}));
	}
	/**
	 * Starts a background thread to receive messages from the phone.
	 * Has a check to ensure the client is active before writing and if not,
	 * exits the loop and the thread.
	 */
	public static void recieveMessages() {
		readThread = new Thread(new Runnable() {
			public void run() {
				while(true) {
					if(client.active())receiveServerMessage();
					else return;
				}
			}
		});
		readThread.start();
	}
	/**
	 * Reads what the action from the server is and executes that action.
	 */
	public static void receiveServerMessage() {
		String serverMessage = client.read();
		if(serverMessage == null || serverMessage.equals(SHUTDOWN)){
			client.shutCommunications();
		}
		else if(serverMessage.equals(TEXT)) {
			getMessage();
		}
	}
	/**
	 * Gets a message from the server.
	 */
	public static void getMessage() {
		String number = client.read();
		String message = client.read();
		//Plays a notification sound upon message arrival.
		
		Contact existingContact = getContactFromNumber(number);
		if(existingContact != null) {
			existingContact.addMessage(message,Contact.RECEIVED);
			menu.addNotification(Notification.RECEIVED, existingContact,message);
			menu.revalidate();
			menu.repaint();
			if(textPanel.currentContact != null && textPanel.currentContact == existingContact){
				textPanel.updateArea();
			}
		}
		else {
			//Creates a new contact for the unknown one.
			Contact newContact = new Contact("Unknown","",number);
			newContact.addMessage(message,Contact.RECEIVED);
			contactList.add(newContact);
			menu.addNotification(Notification.RECEIVED, newContact, message);
			menu.revalidate();
			menu.repaint();
		}
	}
	/**
	 * Sends a message for the phone to send via the socket.
	 * Also ensures to add the message to the contact's history and the notification bar at the menu.
	 * @param number
	 * @param message
	 * @param currentContact - the contact the message was sent to.
	 */
	public static void sendMessage(String number,String message,Contact currentContact) {
		client.write(TEXT,true);
		client.write(number, true);
		client.write(message, true);
		if(currentContact != null) {
			currentContact.addMessage(message,Contact.SENT);
			menu.addNotification(Notification.SENT,currentContact, message);
		}
		else if(getContactFromNumber(number) != null) {
			System.out.println(number);
			Contact thisContact = getContactFromNumber(number);
			thisContact.addMessage(message, Contact.SENT);
			menu.addNotification(Notification.SENT, thisContact, message);
		}
		else {
			/*
			 * If no contact was selected, we create a new one.
			 */
			Contact newContact = new Contact("Unknown","",number);
			newContact.addMessage(message,Contact.SENT);
			contactList.add(newContact);
			menu.addNotification(Notification.SENT,newContact, message);
		}
	}
	/**
	 * Formats a number to have only numerical content.
	 * Gets rid of the +1 for long distance as well.
	 * @param number
	 * @return the formatted phone number. OR "" if the number doesn't format properly.
	 */
	public static String formatNumber(String number) {
		String formattedNumber = number.trim();
		try {
			//Get rid of the long distance +1
			if(formattedNumber.substring(0, 2).equals("+1")) {
				formattedNumber = formattedNumber.substring(2);
			}
			else if(formattedNumber.substring(0,1).equals("1")) {
				formattedNumber = formattedNumber.substring(1);
			}
			for(int i = 0;i < formattedNumber.length();i++) {
				if(!Character.isDigit(formattedNumber.charAt(i))) {
					formattedNumber = formattedNumber.substring(0,i) + formattedNumber.substring(i+1);
					i--; //Move back an index since the current index has just been deleted.
				}
			}
		}
		catch(Exception e) {
			return "";
		}
		return formattedNumber;
	}
	/**
	 * Tries to get the contact matching the phone number.
	 */
	public static Contact getContactFromNumber(String number) {
		for(Contact currentContact : contactList) {
			if(formatNumber(currentContact.number).equals(formatNumber(number))) {
				return currentContact;
			}
		}
		return null;
	}
	/**
	 * Constructs the gui for the main program.
	 */
	public static void constructGui() {
		BasicFrame frame = new BasicFrame("LocalText",new Dimension(600,400));
		frame.setVisible(true);
		frame.setResizable(true);
	
		backPanel = new JPanel();
		cardLayout = new CardLayout();
		frame.setContentPane(backPanel);
		backPanel.setLayout(cardLayout);
		
		textPanel = new TextPanel();
		contactsPanel = new ContactsPanel(frame.getSize(),textPanel,cardLayout);
		
		menu = new ModeSelect(cardLayout);
		
		
		backPanel.add(textPanel,TEXT_PANEL);
		backPanel.add(contactsPanel,CONTACT_PANEL);
		backPanel.add(menu,MODE_SELECT);
		cardLayout.show(backPanel,MODE_SELECT);
	}
	
	/**
	 * Reads the contact list from the server.
	 * @param client
	 */
	public static void getContactList(Client client) {
		contactList = new ArrayList<Contact>();
		int numberOfContacts = Integer.parseInt(client.read());
		
		for(int i = 0;i < numberOfContacts;i++) {
			String readLine = client.read();
			//System.out.println(readLine);
			int indexOfLine = readLine.indexOf("|");
			//Getting the name from the readLine
			String name = readLine.substring(0,indexOfLine);
			readLine = readLine.substring(indexOfLine+1);
			indexOfLine = readLine.indexOf("|");
			//Getting the phone number
			String phoneNumber = readLine.substring(0,indexOfLine);
			phoneNumber = formatNumber(phoneNumber);
			readLine = readLine.substring(indexOfLine+1);
			//Getting the id from the remaining string.
			String id=readLine;
			Contact newContact = new Contact(name,id,phoneNumber);
			contactList.add(newContact);
		}
	}
	/**
	 * Class for the action listener we pass to the opening app frame.
	 * @author jordan
	 * @version v0.14 February 10th
	 */
	public static class ConnectButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			startProgram();
		}
		
	}

}
