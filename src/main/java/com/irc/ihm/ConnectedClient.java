package com.irc.ihm;

import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.TextField;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectedClient extends JPanel {
	protected JTextField connectedClient = new JTextField();
	public ConnectedClient(){
		this.setSize(200, 400);
		this.setVisible(true);
		connectedClient.setPreferredSize( new Dimension( 200, 400 ) );
		this.add(connectedClient);
	}
}
