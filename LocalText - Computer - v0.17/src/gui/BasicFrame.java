package gui;


import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * Basic frame for any application. Shortens the amount of code needed to create larger programs. 
 * @author jakjm
 * @version January 10th, 2016
 */
public class BasicFrame extends JFrame
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public BasicFrame(String name){
	   super(name);
	   super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
   public BasicFrame(String name,Dimension size){
	   this(name);
	   super.setSize(size);
	   super.setPreferredSize(size);
   }
   public BasicFrame(String name,Dimension size,int closeOperation) {
	   this(name,size);
	   super.setDefaultCloseOperation(closeOperation);
   }
   
}
