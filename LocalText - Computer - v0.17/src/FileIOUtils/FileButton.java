package FileIOUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * 
 * @author jakjm
 * @version November 27th 2017
 */
public class FileButton extends JButton implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel buttonLabel;
	File thisFile;
	FileSelector fileSelector; //The file selector that this button is a part of.
	public FileButton(File file,FileSelector fileSelector){
		super();
		this.thisFile = file;
		this.setVisible(true);
		this.setEnabled(true);
	    this.addActionListener(this);
		buttonLabel = new JLabel(file.getName());
		this.add(buttonLabel);
		
		this.fileSelector = fileSelector;
	}
	public void actionPerformed(ActionEvent e) {
		if(thisFile.isDirectory()){
		this.fileSelector.updateForFile(thisFile);
		}
		else{
		fileSelector.select(thisFile);
		}
	}
}
