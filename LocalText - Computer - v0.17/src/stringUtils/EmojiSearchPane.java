package stringUtils;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import gui.BasicField;

/**
 * Search pane for the emojis
 * @author jordan
 *
 */
public class EmojiSearchPane extends JPanel implements ActionListener,KeyListener{
	private static final long serialVersionUID = 1L;
	private JPanel emojiPanel;
	private static final int EMOJIS_PER_LINE = 4;
	private static final int MINIMUM_SEARCH_QUERY = 2;
	private BasicField searchField;
	private Emoji [] emojiList;
	public static void main(String [] args) {
		JFrame searchFrame = new JFrame();
		searchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		searchFrame.setSize(400,600);
		searchFrame.setContentPane(new EmojiSearchPane());
		searchFrame.setVisible(true);
	}
	public EmojiSearchPane() {
		super();
		setLayout(new BorderLayout());
		
		searchField = new BasicField("Search Emoji:");
		searchField.addKeyListener(this);
		add(searchField,BorderLayout.NORTH);
		
		emojiPanel = new JPanel();
		emojiPanel.setLayout(new GridLayout(0,EMOJIS_PER_LINE));
		JScrollPane emojiScroll = new JScrollPane(emojiPanel);
		emojiScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		emojiScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(emojiScroll,BorderLayout.CENTER);
		emojiList = EmojiUtils.getEmojiList();
		
		JLabel instructionLabel = new JLabel("Search for an emoji and select it!");
		add(instructionLabel,BorderLayout.SOUTH);
		
	}
	public void addContent(String searchQuery) {
		System.out.println("***" + searchQuery + "***");
		for(int i = 0; i < emojiList.length;i++) {
			searchQuery = searchQuery.toUpperCase();
			if(emojiList[i].description().toUpperCase().contains(searchQuery)) {
				JButton emojiButton = new JButton(emojiList[i].emojis());
				emojiButton.addActionListener(this);
				emojiPanel.add(emojiButton);
				
			}
		}
		emojiPanel.repaint();
		emojiPanel.revalidate();
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}
	/**
	 * Search for emojis based on the search query up to this point
	 * @param e
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		//Getting rid of old search results
		if(emojiPanel.getComponentCount() > 0) {
			emojiPanel.removeAll();
			emojiPanel.repaint();
			emojiPanel.revalidate();
		}
		//And getting new ones if necessary
		if(searchField.getValue().length() >= MINIMUM_SEARCH_QUERY) {
			addContent(searchField.getValue());
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
