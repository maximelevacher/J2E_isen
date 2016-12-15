package com.irc.ihm;

import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.TextField;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DisplayMessage extends JPanel {
	protected JTextField displayMessage = new JTextField();
	public DisplayMessage(){
		this.setSize(550, 400);
		this.setVisible(true);
		displayMessage.setPreferredSize( new Dimension( 550, 400 ) );
		this.add(displayMessage);
	}
}
