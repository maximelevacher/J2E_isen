package com.irc.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.irc.client.ClientSimple;
import com.irc.ihm.GUI;

public class Controller {
	ClientSimple client = null;
	GUI view = null;
	private volatile boolean _isRunning = true;

	public Controller(ClientSimple c, GUI v) {
		client = c;
		view = v;
		
		try {
			c.connectToServer(InetAddress.getLocalHost(), ClientSimple.DEFAULT_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		Thread threadReceiveMessages = new Thread() {
			public void run() {
				while(_isRunning) {
					try {
						String message = c.receiveMessage();
						v.appendMessageToArea(message);
					} catch (IOException e) {
						e.printStackTrace();
						c.disconnectFromServer();
					}
				}
			}
		};
		threadReceiveMessages.start();
	}

	public void onClickOnSendMessage(String message) {
		client.sendMessage(message);
	}
	
	public static void main(String[] args) {
		ClientSimple client = new ClientSimple();
		GUI view = new GUI();
		Controller controller = new Controller(client, view);
		view.addListenener(controller);
	}
}
