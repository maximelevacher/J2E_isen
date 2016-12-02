package com.irc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientSimple implements Runnable {
	
	private Socket _socket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	public final static int DEFAULT_PORT = 45612;
	private volatile boolean _isRunning = true;
	
	public void connectToServer(InetAddress hote, int port) throws IOException {
		_socket = new Socket(hote, port);
		_out = new PrintWriter(_socket.getOutputStream(), true);
		_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
	}
	
	public void sendMessage(String message) {
		_out.println(message);
	}
	
	public String receiveMessage() {
		String message = null;
		try {
			message = _in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			disconnectFromServer();
		}
		return message;
	}
	
	public void disconnectFromServer() {
		_isRunning = false;
		_out.close();
		try {
			_in.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(_isRunning) {
			String message = null;
			message = receiveMessage();
			System.out.println("Message reçu: " + message);
		}
	}
	
	public static void main(String[] args) {
		InetAddress hote = null;
		int port = ClientSimple.DEFAULT_PORT;
		// Récupère les informations de connexion depuis les arguments du programme
		try {
			if (args.length >= 1) {
				hote = InetAddress.getByName(args[0]);
			} else {
				hote = InetAddress.getLocalHost();
			}

			if (args.length == 2) {
				port = Integer.parseInt(args[1]);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(hote, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread threadClient = new Thread(client);
		threadClient.start();
		
		Scanner scanner = new Scanner(System.in);
		boolean isRunning = true;
		while(isRunning) {
			String input = scanner.nextLine();
			System.out.println("Envoi message: " + input);
			client.sendMessage(input);
			if (input.equals("/quit")) {
				System.out.println("Recu /quit message");
				isRunning = false;
			}
		}
		client.disconnectFromServer();
	}
}
