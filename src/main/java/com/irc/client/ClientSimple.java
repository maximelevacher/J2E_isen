package com.irc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ClientSimple implements Runnable {
	
	static final Logger logger = Logger.getLogger(ClientSimple.class);
	static final String logConfigPath = "conf/log4j.properties";

	private Socket _socket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	public final static int DEFAULT_PORT = 45612;
	private volatile boolean _isRunning = true;
	
	public void connectToServer(InetAddress hote, int port) throws IOException {
		_socket = new Socket(hote, port);
		_out = new PrintWriter(_socket.getOutputStream(), true);
		_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		logger.info("Connecté au serveur: " + hote.toString() + ":" + port);
	}
	
	public void sendMessage(String message) {
		_out.println(message);
		logger.info("Envoi du message: " + message);
	}
	
	public String receiveMessage() throws IOException {
		String message = null;
		message = _in.readLine();
		return message;
	}
	
	public void disconnectFromServer() {
		_isRunning = false;
		_out.close();
		try {
			_in.close();
			_socket.close();
			logger.info("Déconnexion du serveur.");
		} catch (IOException e) {
			logger.error("Erreur lors de la déconnexion.", e);
		}
	}

	@Override
	public void run() {
		logger.info("Lancement du thread receiveMessage.");
		while(_isRunning) {
			String message = null;
			try {
				message = receiveMessage();
				logger.info("Message reçu: " + message);
			} catch (IOException e) {
				logger.error("Thread receiveMessage ne reçoit pas.", e);
				disconnectFromServer();
			}
		}
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(logConfigPath);
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
			logger.error("Erreur argument hote.", e);
		}
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(hote, port);
		} catch (IOException e) {
			logger.error("Impossible de se connecter au serveur:" + hote.toString() + ":" + port, e);
		}
		Thread threadClient = new Thread(client);
		threadClient.start();
		
		Scanner scanner = new Scanner(System.in);
		boolean isRunning = true;
		while(isRunning) {
			String input = scanner.nextLine();
			client.sendMessage(input);
			if (input.equals("/quit")) {
				logger.info("Commande entrée: " + "/quit");
				isRunning = false;
			}
		}
		client.disconnectFromServer();
	}
}
