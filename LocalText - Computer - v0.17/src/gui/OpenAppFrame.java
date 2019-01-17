package gui;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel for opening the app.
 * @author jordan
 * @version February 13th 2018 v0.15
 */
public class OpenAppFrame implements ActionListener{
	public BasicFrame appOpenFrame;
	public JPanel openAppPanel;
	public BasicField codeField;
	public JButton scanIPButton;
	public JButton manuallyEnterButton;
	public JButton connectButton;
	public JTextField ipEnterField;
	public JComboBox <String> ipSelectionBox;
	public ArrayList <InetAddress> ipSelectionList;
	public String mode;
	public static final String SCAN_MODE = "SCAN";
	public static final String ENTER_MODE = "ENTER";
	//Panel for ip entry.
	public JPanel ipPanel;
	public final Font TITLE_FONT = new Font(Font.SERIF,Font.PLAIN,32);
	/**
	 * Creates a opening app frame with an action listener for when the connect button is pressed!
	 * @param connectAction
	 */
	public OpenAppFrame(ActionListener connectAction) {
		//Creating the frame.
		appOpenFrame = new BasicFrame("Select IP and passcode",new Dimension(400,400));
		
		//Adding our panel
		openAppPanel = new JPanel();
		appOpenFrame.setContentPane(openAppPanel);
		openAppPanel.setLayout(new GridLayout(5,1));
		
		//Adding panel contents.
		JLabel titleLabel = new JLabel("LocalText");
		titleLabel.setFont(TITLE_FONT);
		openAppPanel.add(titleLabel);
		
	
		
		//Adding the ip selection panel.
		ipPanel = new JPanel();
		ipPanel.setLayout(new BoxLayout(ipPanel,BoxLayout.X_AXIS));
		
		
		
		//Label
		JLabel ipLabel = new JLabel("IP Address:");
		ipPanel.add(ipLabel);
		
		//Panel for the actual selection of ipAddress.
		JPanel ipSelectionPanel = new JPanel();
		ipSelectionPanel.setLayout(new GridLayout(2,2));
		
		
		//Manual entry field.
		ipEnterField = new JTextField();
		ipSelectionPanel.add(ipEnterField);
		
		//Selection box
		ipSelectionBox = new JComboBox<String>();
		ipSelectionPanel.add(ipSelectionBox);
		
		//Button to manually enter ip address.
		manuallyEnterButton = new JButton("Manually Enter");
		manuallyEnterButton.addActionListener(this);
		ipSelectionPanel.add(manuallyEnterButton);
		
		//Scan Button
		scanIPButton = new JButton("Scan Subnet");
		scanIPButton.addActionListener(this);
		//Adding the option panel to the ipPanel.
		ipSelectionPanel.add(scanIPButton);
		ipPanel.add(ipSelectionPanel);
		openAppPanel.add(ipPanel);
		
		//Pincode field
		codeField = new BasicField("Pincode:");
		openAppPanel.add(codeField);
		
		//Connect button with the connect action listener.
		connectButton = new JButton("Connect");
		connectButton.addActionListener(connectAction);
		openAppPanel.add(connectButton);
		
		//Setting the frame visible.
		appOpenFrame.setVisible(true);
		
		//Sets the mode to manual by default.
		manualEntry();
	}
	public String getPincode() {
		return codeField.getValue();
	}
	/**
	 * Returns the ip that has been selected by the user.
	 * @return the selected ip address.
	 */
	public String getIP() {
		if(mode == SCAN_MODE) {
			//Ip without the slash in front.
			return ((String)(ipSelectionBox.getSelectedItem())).substring(1);
		}
		else if(mode == ENTER_MODE) {
			return ipEnterField.getText();
		}
		return null;
	}
	/**
	 * Manually enter the ip address.
	 */
	private void manualEntry() {
		//Enabling and disabling components as appropriate.
		ipSelectionBox.setEnabled(false);
		manuallyEnterButton.setEnabled(false);
		scanIPButton.setEnabled(true);
		ipEnterField.setEnabled(true);
		mode = ENTER_MODE;
	}
	/**
	 * Fills the ip combo box with new selections for the ip address.
	 * Also switches the gui to 
	 */
	private void refreshIPBox (){
		ipSelectionList = netUtils.IpUtils.ipScan();
		ipSelectionBox.removeAllItems();
		openAppPanel.repaint();
		openAppPanel.revalidate();
		for(InetAddress ip : ipSelectionList) {
			String data = ip.toString();
			ipSelectionBox.addItem(data);	
		}
		//Enabling and disabling components as appropriate.
		ipSelectionBox.setEnabled(true);
		manuallyEnterButton.setEnabled(true);
		scanIPButton.setEnabled(false);
		ipEnterField.setEnabled(false);
		mode = SCAN_MODE;
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == scanIPButton) {
			refreshIPBox();
		}
		else if(e.getSource() == manuallyEnterButton) {
			manualEntry();
		}
	}
}
