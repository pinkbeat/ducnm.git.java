package com.cdit.test.portal;

import javax.swing.*;

import com.cdit.sync.ad.TransferToDB;

import java.awt.*;
import java.awt.event.*;

public class CheckUserMobile extends JFrame implements WindowListener{
	JTextField jtfText1, jtfUneditableText;
	String disp = "";
	MyAction handler  = null;
	JButton button;
	JButton close;
	TransferToDB db = null;
	
  public CheckUserMobile() {
	  
		super("CHECK USER MOBILE");
		 db = new TransferToDB();
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		jtfText1 = new JTextField(10);
		jtfUneditableText = new JTextField("KET QUA", 20);
		jtfUneditableText.setEditable(false);
		JLabel jLabel = new JLabel("Username: ");
		button = new JButton("Xem");
		button.setToolTipText("Click to see mobile");
		button.setFocusable(true); // How do I get focus on button on App launch?
//		button.requestFocus(true);
		close = new JButton("Close");
		JLabel jLabel2 = new JLabel("PS: please click \"Close\" when exit.");
		container.add(jLabel);
		container.add(jtfText1);
		container.add(button);
		container.add(close);
		container.add(jtfUneditableText);
		container.add(jLabel2);
		
		
		
		handler = new MyAction();
		close.addActionListener(handler);
		button.addActionListener(handler);
		jtfText1.addActionListener(handler);
		
		setSize(400, 200);
		setVisible(true);
	  }
  public class MyAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String text = (String) e.getActionCommand();
			if (text.equals("Xem")) {				
				jtfUneditableText.setText(db.getUserMobile(jtfText1.getText(), false));
			}else if(text.equals("Close")){
				if(db != null)
					db.closeConnection();
				System.exit(0);
			}else if(text.length() != 0){
				jtfUneditableText.setText(db.getUserMobile(jtfText1.getText(), false));
			}
		}
	}
	//Main Program that starts Execution
	public static void main(String args[])
	{
		CheckUserMobile test = new CheckUserMobile();
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if(db != null)
		  db.closeConnection();
		System.out.println("closed");
		System.exit(0);
	}
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		if(db != null)
			  db.closeConnection();
		System.out.println("closed");
		System.exit(0);
	}
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
