package FileIOUtils;
/**
 * Basic GUI class for File selection.
 * @author jakjm
 * @version December 1st 2017.
 */
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
public class FileSelector extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JPanel outerPanel;
	public File currentFile;
	public JPanel innerPanel;
	public JPanel hud; //Heads up display panel.
	public JLabel directoryLabel;
	public JButton backButton;
	public JButton returnHome;
	public JPanel topPanel;
	public JPanel bottomPanel;
	public JScrollPane innerPaneScroll;
	public ArrayList<FileButton> buttonList;
	public File selectedFile;
	
	//The home directory of the user.
	public final static File homeFile = new File(System.getProperty("user.home"));
	public FileSelector(){
		super("Open File");
		this.setSize(600,300);
		this.setVisible(false);
		this.setEnabled(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		outerPanel = new JPanel();
		outerPanel.setSize(this.getSize());
		outerPanel.setVisible(true);
		outerPanel.setLayout(new BorderLayout());
		
		
		hud = new JPanel();
		hud.setLayout(new GridLayout(1,2));
		
		directoryLabel = new JLabel();
		directoryLabel.setText("");
		hud.add(directoryLabel);
		
		//Buttons for the hud
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1,2));
		
		backButton  = new JButton();
		backButton.add(new JLabel("<- Go Back ->"));
		backButton.addActionListener(this);
		backButton.setVisible(true);
		backButton.setEnabled(true);
		buttonsPanel.add(backButton);
		
		//Buttons for the hud
		returnHome = new JButton();
		returnHome.add(new JLabel("Back to Home"));
		returnHome.addActionListener(this);
		returnHome.setVisible(true);
		returnHome.setEnabled(true);
		buttonsPanel.add(returnHome);
		hud.add(buttonsPanel);
		
		
		outerPanel.add(hud,BorderLayout.NORTH);
		this.add(outerPanel);
		
		
		
		//Inner Panel for Buttons
		innerPanel = new JPanel();
		innerPanel.setSize((int)(0.8 *outerPanel.getSize().getWidth()),(int)(0.6 * outerPanel.getSize().getHeight()));
		innerPanel.setVisible(true);
		innerPanel.setLayout(new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		
		innerPaneScroll = new JScrollPane();
		innerPaneScroll.setViewportView(innerPanel);
		innerPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		innerPaneScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		innerPaneScroll.setVisible(true);
		outerPanel.add(innerPaneScroll,BorderLayout.CENTER);
		buttonList = new ArrayList<FileButton>();
		
		updateForFile(homeFile);
	}
	/**
	 * Updates the panel with the list of files under the currentDirectory.
	 * @param file
	 */
	public void updateForFile(File file){
		clearOldFileButtons();
		
		/*
		 * Getting the list of files under this file and adding them to the selectable button list.
		 */
		File [] fileList = file.listFiles();
	    for(int i = 0;i < fileList.length;i++){
	    	File currentFile = fileList[i];
	    	if(currentFile.getName().charAt(0) != '.'){
	    		FileButton button = new FileButton(currentFile,this);
	    		innerPanel.add(button);
	    		buttonList.add(button);
	    	}
	    }
	    currentFile = file;
	    if(currentFile.getPath().equals(homeFile.getPath())){
	    	returnHome.setEnabled(false);
	    }
	    else{
	    	returnHome.setEnabled(true);
	    }
	    
	    innerPanel.repaint();
	    innerPanel.revalidate();
	    outerPanel.repaint();
	    outerPanel.revalidate();
	    
	    directoryLabel.setText("Current Directory:" +"\"" + currentFile.getPath() + "\"");
	}
	public void clearOldFileButtons(){
		for(FileButton button : this.buttonList){
			innerPanel.remove(button);
		}
		this.buttonList.clear();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == returnHome)goHome();
		if(e.getSource() == backButton)goBack();
	}
	public void goBack(){
		if(currentFile.getParentFile() != null){
			updateForFile(currentFile.getParentFile());
		}
	}
	public void goHome(){
		if(!currentFile.getPath().equals(homeFile.getPath())){
			updateForFile(homeFile);
		}
	}
	public File getFile(){
		this.setVisible(true);
		while(this.selectedFile == null){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setVisible(false);
		return this.selectedFile;
	}
	public void select(File thisFile){
		selectedFile = thisFile;
	}
}
