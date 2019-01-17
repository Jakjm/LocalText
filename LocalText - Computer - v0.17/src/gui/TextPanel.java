package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import localText.Contact;
import main.Main;
/**
 * Panel where the user can send or recieve texts on the gui.
 * @author jordan
 * @version January 10th 2018
 */
public class TextPanel extends JPanel implements ActionListener,KeyListener{
	private static final long serialVersionUID = 1L;
	private BasicField numberField;
	private BasicField textField;
	private JLabel contactLabel;
	private BasicArea messageArea;
	private JButton sendButton;
	private JButton backButton;
	private JButton addEmojiButton;
	public Contact currentContact = null;
	private JFrame emojiFrame;
	private EmojiSearchPane emojiPanel;
	private static final Dimension EMOJI_FRAME_SIZE = new Dimension(300,200);
	private static final String NO_NAME = "Unnamed Contact";
	public TextPanel() {
		super();
		
		this.setLayout(new BorderLayout());
		
		//Panel for the top bar of the texting panel.
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		
		//Adding label for the name of the contact, the field for the number and the back button to go back to the main menu.
		contactLabel = new JLabel(NO_NAME);
		topPanel.add(contactLabel);
		numberField = new BasicField("Number: ");
		topPanel.add(numberField);
		backButton = new JButton("Back to menu");
		backButton.addActionListener(this);
		topPanel.add(backButton);
		this.add(topPanel,BorderLayout.NORTH);
		
		messageArea = new BasicArea("");
		this.add(messageArea,BorderLayout.CENTER);
		messageArea.setEditable(false);
		
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.X_AXIS));
		
		textField = new BasicField("Message: ",5);
		//textField.setFont(new Font(Font.SERIF,Font.PLAIN,12));
		textField.addKeyListener(this); //Add key listener so that the enter key works as a send button.
		bottomPanel.add(textField);
		
		addEmojiButton = new JButton("❤️");
		addEmojiButton.addActionListener(this);
		bottomPanel.add(addEmojiButton);
		
		sendButton = new JButton("Send"); //Send button in case the user doesn't hit enter.
		sendButton.setMnemonic(KeyEvent.VK_ENTER);
		sendButton.addActionListener(this);
		bottomPanel.add(sendButton);
		
		//Setting up a frame for emojis.
		emojiFrame = new JFrame();
		emojiFrame.setSize(EMOJI_FRAME_SIZE);
		emojiPanel = new EmojiSearchPane(new EmojiClickListener());
		emojiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		emojiFrame.setContentPane(emojiPanel);
		emojiFrame.setVisible(false);
		this.add(bottomPanel,BorderLayout.SOUTH);
	}
	/**
	 * Updates the panel in the case the user seeks to text a contact.
	 * @param c
	 */
	public void updateFor(Contact c){
		this.currentContact = c;
		contactLabel.setText(" " + c.name);
		numberField.setEnabled(false);
		messageArea.setValue(c.messages);
		numberField.setValue(c.number);
		textField.clear();
		
		//Revalidates in case the panel is currently open.
		this.repaint();
		this.revalidate();
	}
	/**
	 * Updates the area in the event of a message being received.
	 */
	public void updateArea() {
		if(this.currentContact != null) {
			messageArea.setValue(this.currentContact.messages);
			revalidate();
			repaint();
			
		}
	}
	/**
	 * Sends a message using the information within the text panel.
	 */
	private void sendMessage() {
		//Checks to ensure the message being sent is valid first.
		if(!validateSend()) {
			JOptionPane.showMessageDialog(null,"Please ensure that the message isn't blank or that the phone number is valid.");
			return;
		}
		String number = Main.formatNumber(numberField.getValue());
		String message = textField.getValue();
		textField.clear();
		if(Main.client.active())Main.sendMessage(number, message, currentContact);
		else {
			JOptionPane.showMessageDialog(null, "It seems the server is no longer active. Try relaunching?");
			return;
		}
		messageArea.appendText("You:" + message +"\n");
		
		//Updates the panel if a contact exists for the number we sent a message to.
		if(currentContact == null) {
			Contact getContact = Main.getContactFromNumber(number);
			if(getContact != null) {
				this.updateFor(getContact);
			}
		}
	}
	/**
	 * Ensures that the values entered are legitimate for sending to the server.
	 * @return whether the app is ready to send a message or not.
	 */
	private boolean validateSend() {
		if(textField.getValue().trim().equals(""))return false;
		String number = Main.formatNumber(numberField.getValue().trim());
		if(number.equals("") || number.length() < 10)return false;
		return true;
	}
	/**
	 * Action Listener for the buttons on the text panel.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton) {
			sendMessage();
		}
		else if(e.getSource() == backButton){
			backToMainMenu();
			emojiFrame.setVisible(false);
		}
		else if(e.getSource() == addEmojiButton) {
			showEmojiFrame();
		}
	}
	/**
	 * Positions the emoji pop-up frame with respect to the add emoji button.
	 * Also makes the emoji pop-up frame visible.
	 */
	public void showEmojiFrame() {
		Point buttonLocation = addEmojiButton.getLocationOnScreen();
		int x = buttonLocation.x - (emojiFrame.getSize().width / 2);
		int y = buttonLocation.y - emojiFrame.getSize().height;
		emojiFrame.setLocation(x, y);
		emojiFrame.setVisible(true);
	}
	/**
	 * Resets the panel for heading back into the main menu.
	 */
	public void backToMainMenu() {
		this.currentContact = null;
		contactLabel.setText(NO_NAME);
		numberField.setEnabled(true);
		numberField.clear();
		messageArea.clear();
		textField.clear();
		Main.cardLayout.show(Main.backPanel,Main.MODE_SELECT);
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
	}
	/**
	 * Checking for key releases to make messages send.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMessage();
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {		
	}
	
	
	/**
	 * Action listener to provide to the emoji frame.
	 * @author jordan
	 * Gets the emoji from the button and adds it to the textField.
	 */
	private class EmojiClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String emoji = ((JButton)e.getSource()).getText().trim();
			textField.insertTextAtCursor(emoji); //Inserts the given text at this point.
			emojiFrame.setVisible(false);
			emojiPanel.clearPanel();
		}
		
	}
}
