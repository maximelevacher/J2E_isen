package com.irc.ihm;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SendMessage extends JPanel implements MouseListener{
	protected JTextField writeMessage = new JTextField();
	protected JButton sendMessage = new JButton("Envoyer");
	public SendMessage(){
		this.setSize(100, 600);
		this.setVisible(true);
		this.add(writeMessage);
		writeMessage.setPreferredSize(new Dimension(500, 100));
		this.add(sendMessage);
		sendMessage.setPreferredSize(new Dimension(100, 50));
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
