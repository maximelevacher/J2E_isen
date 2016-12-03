package com.irc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

public class ServerMultiClient {

	private ServerSocket serverSocket = null;
	private int defaultPort = 45612;
	private boolean isRunning = true;
	private Vector<ServerThread> _tabServerThreads = new Vector<ServerThread>();

	public ServerMultiClient() {
		System.out.println("Starting the server on the port : " + defaultPort);
		try {
			serverSocket = new ServerSocket(defaultPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server started!");
		while (isRunning) {
			System.out.println("Waiting for connection ...");
			try {
				ServerThread newClient = new ServerThread(serverSocket.accept(), this);
				_tabServerThreads.addElement(newClient);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Server accepted a client.");
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastMessage(String message) {
		for (ServerThread t : _tabServerThreads) {
			t.sendMessage(message);
		}
	}

	public static void main(String[] args) {
		ServerMultiClient server = new ServerMultiClient();
	}
}