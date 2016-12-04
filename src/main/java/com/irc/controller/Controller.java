package com.irc.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.irc.client.ClientSimple;
import com.irc.ihm.GUI;

/**
 * Cette classe fait le lien entre l'IHM et le client
 * @author Zak
 *
 */
public class Controller {
	ClientSimple client = null;
	GUI view = null;
	private volatile boolean _isRunning = true;

	public Controller(ClientSimple c, GUI v) {
		client = c;
		view = v;
		
		try {
			client.connectToServer(InetAddress.getLocalHost(), ClientSimple.DEFAULT_PORT);
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
						String message = client.receiveMessage();
						view.appendMessageToArea(message);
					} catch (IOException e) {
						e.printStackTrace();
						client.disconnectFromServer();
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
