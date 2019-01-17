package gui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import localText.Contact;
import main.Main;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Panel for selecting a contact to send a message to.
 * @author jordan
 * @version January 10th 2018
 */
public class ContactsPanel extends JPanel implements ActionListener,KeyListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane innerScroll;
	private JPanel innerPanel;
	public ArrayList<Contact> currentList; //The list of contacts currently being viewed.
	private ArrayList<JButton> buttonList;
	private JLabel contactsLabel;
	private TextPanel textPanel;
	private CardLayout cardLayout;
	private JButton backButton;
	private JTextField searchBar;
	public ContactsPanel(Dimension size,TextPanel textPanel,CardLayout cardLayout) {
		super();
		this.setLayout(new BorderLayout());
		this.currentList = Main.contactList;
		this.setSize(size);
		
		//Top of the panel.
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		contactsLabel = new JLabel("Select Contact");
		topPanel.add(contactsLabel);
		
		
		
		//Search bar
		JPanel searchPanel = new JPanel();
		
		searchPanel.setLayout(new GridLayout(1,2));
		JLabel searchLabel = new JLabel("Search:");
		searchPanel.add(searchLabel);
		
		searchBar = new JTextField();
		searchBar.addKeyListener(this);
		searchPanel.add(searchBar);
		topPanel.add(searchPanel);
		
		
		//Back buton
		backButton = new JButton("Back");
		backButton.addActionListener(this);
		topPanel.add(backButton);
		
		this.add(topPanel,BorderLayout.NORTH);
		
		
		//Setting up a scrollable panel for selecting a contact to send a mesage to.
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		innerScroll = new JScrollPane(innerPanel);
		innerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		innerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(innerScroll,BorderLayout.CENTER);
		this.setVisible(true);
		this.addButtons(currentList);
		System.out.println("Set up done");
		
		this.cardLayout = cardLayout;
		this.textPanel = textPanel;
		
	}
	/**
	 * Adding the buttons to our inner panel.
	 * @param contactList
	 */
	public void addButtons(ArrayList<Contact> contactList) {
		buttonList = new ArrayList<JButton>();
		innerPanel.removeAll();
		for(Contact c :  contactList){
			String buttonText = c.name + "(" +  c.number + ")";
			JButton contactButton = new JButton(buttonText);
			contactButton.addActionListener(this);
			contactButton.setSize(200,40);
			buttonList.add(contactButton);
			innerPanel.add(contactButton);
		}
	}
	/**
	 * Updates the panel for the given contact list.
	 * @param givenList
	 */
	public void updateForList(ArrayList<Contact> givenList) {
		this.currentList = givenList;
		this.addButtons(givenList);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == backButton) {
			this.cardLayout.show(Main.backPanel,Main.MODE_SELECT);
			reset();
		}
		switchToContact(e);
	}
	/**
	 * Resets the panel by making an empty search.
	 */
	public void reset() {
		this.searchBar.setText("");
		performSearch();//Performs an empty search just to get the search window back!
	}
	/**
	 * Gets a search list of contacts based on the given query.
	 * @param searchQuery
	 * @return
	 */
	public ArrayList<Contact> getSearchList(String searchQuery) {
		ArrayList<Contact> queryList = new ArrayList<Contact>();
		String upperQuery = searchQuery.toUpperCase();
		//First round checks if the query is very close to the front of the name.
		for(Contact c : Main.contactList) {
			String contactName = c.name.toUpperCase();
			int indexOf = contactName.indexOf(upperQuery);
			if(indexOf <= 2 && indexOf > -1){
				queryList.add(c);
			}
		}
		//Second round checks if the query is even contained within the name.
		for(Contact c : Main.contactList) {
			String contactName = c.name.toUpperCase();
			if(contactName.contains(upperQuery) && queryList.contains(c) == false) {
				queryList.add(c);
			}
		}
		
		return queryList;
	}
	public void switchToContact(ActionEvent e) {
		int indexNumber = 0;
	    for(JButton button : buttonList) {
	    	if(button == e.getSource()) {
	    		Contact c = currentList.get(indexNumber);
	    		textPanel.updateFor(c);
	    		this.cardLayout.show(Main.backPanel,Main.TEXT_PANEL);
	    		this.reset();
	    		return;
	    	}
	    	indexNumber++;
	    }
	}
	/**
	 * Performs a contact search for the user without even having to press a button!
	 */
	public void performSearch() {
		String searchQuery = searchBar.getText().trim();
		ArrayList<Contact> queryList = getSearchList(searchQuery);
		updateForList(queryList);
		this.revalidate();
		this.repaint();
	}
	public void keyPressed(KeyEvent e) {
	}
	/**
	 * Updates the panel based on the search input.
	 * @param e
	 */
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == searchBar) {
			performSearch();
		}
	}
	public void keyTyped(KeyEvent e) {
	}
	
}
