package gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class for an editable text area field.
 * @author jakjm
 */
public class BasicArea extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private JLabel label;
	public BasicArea(String labelText){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		label = new JLabel();
		label.setText(labelText);
		this.add(label);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		//Setting up a scroll pane for our JTextArea.
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);
	}
	public void setEditable(boolean editable) {
		textArea.setEditable(editable);
	}
	/**
	 * Appends the selected text to the BasicArea.
	 * @param text
	 */
	public void appendText(String text) {
		textArea.append(text);
	}
	public void setValue(String value){
		textArea.setText(value);
	}
	public String getValue(){
		return textArea.getText();
	}
	public void clear(){
		textArea.setText("");
	}
}
