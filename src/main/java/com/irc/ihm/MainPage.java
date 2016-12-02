package com.irc.ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class MainPage extends JFrame {
	public MainPage(){
		this.setTitle("ChatDent");
		this.setSize(800,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLayout(new BorderLayout());
		this.add(new DisplayMessage(),BorderLayout.WEST);
		this.add(new ConnectedClient(), BorderLayout.EAST);
		this.add(new SendMessage(), BorderLayout.SOUTH);
		this.setVisible(true);
	}
}
