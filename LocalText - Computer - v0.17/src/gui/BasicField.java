package gui;
import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * An input field for user data.
 * @author jakjm
 * @version March 26th 2017
 */
public class BasicField extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField inputField;
	public BasicField(String fieldName) {
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		
		this.label = new JLabel();
		this.label.setText(fieldName);
		this.add(label);
		
		this.inputField = new JTextField();
		this.inputField.setSize(200,30);
		//Prevents stretching or squashing.
		this.inputField.setMinimumSize(inputField.getSize());
		this.inputField.setMaximumSize(inputField.getSize());
		this.add(inputField);
	}
	/**
	 * Overrides the add Key listener method to add it to the input field.
	 */
	public void addKeyListener(KeyListener keyListener) {
		this.inputField.addKeyListener(keyListener);
	}
	public BasicField(String fieldName,int cols) {
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.label = new JLabel();
		this.label.setText(fieldName);
		this.add(label);
	    inputField = new JTextField(cols);
	    this.add(inputField);
	}
	public BasicField(String fieldName,Color color){
		this(fieldName);
		this.setBackground(color);
	}
	/**
	 * Disables or enables the field.
	 */
	public void setEnabled(boolean enabled){
		inputField.setEnabled(enabled);
	}
	/**
	 * Empties the field of text.
	 */
	public void clear(){
		inputField.setText("");
	}
	public void setValue(String text){
		inputField.setText(text);
	}
	/**
	 * Inserts a string of text at the cursor position
	 */
	public void insertTextAtCursor(String string) {
		int caretPos = inputField.getCaretPosition();
		String currentText = inputField.getText();
		String newText = currentText.substring(0,caretPos) + string + currentText.substring(caretPos);
		inputField.setText(newText);
	}
	/**
	 * Returns the value in the text field.
	 * @return the value in the textField.
	 */
	public String getValue(){
		return inputField.getText();
	}
	public int parseIntValue(){
		return Integer.parseInt(getValue());
	}
	public float parseFloatValue(){
		return Float.parseFloat(getValue());
	}
	
}
