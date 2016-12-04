package com.irc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Cette classe implémente un client pouvant se connecter sur un serveur de chat
 * @author Zak
 *
 */
public class ClientSimple implements Runnable {
	/**
	 * Permet de logger des messages suivant le fichier de configuration log4j.properties
	 */
	static final Logger logger = Logger.getLogger(ClientSimple.class);
	static final String logConfigPath = "conf/log4j.properties";

	public final static int DEFAULT_PORT = 45612;

	private Socket _socket = null;
	private PrintWriter _out = null;
	private BufferedReader _in = null;
	private volatile boolean _isRunning = true;

	/**
	 * Connecte le client au serveur indiqué
	 * @param hote L'hote sur lequel le client doit se connecter
	 * @param port Le port sur lequel le client doit se connecter
	 * @throws IOException En cas de problème de connexion
	 */
	public void connectToServer(InetAddress hote, int port) throws IOException {
		_socket = new Socket(hote, port);
		_out = new PrintWriter(_socket.getOutputStream(), true);
		_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		logger.info("Connecté au serveur: " + hote.toString() + ":" + port);
	}
	
	/**
	 * Demande au serveur de changer le pseudo du client
	 * @param nickname Le pseudo demandé
	 */
	public void setNickName(String nickname) {
		sendMessage("%nickname " + nickname);
	}

	/**
	 * Envoie un message au serveur
	 * @param message
	 */
	public void sendMessage(String message) {
		_out.println(message);
		logger.info("Envoi du message: " + message);
	}
	
	/**
	 * Attends un message du serveur. La méthode est bloquante.
	 * @return Un string contenant le message se terminant pas un \r ou \n
	 * @throws IOException
	 */
	public String receiveMessage() throws IOException {
		String message = null;
		message = _in.readLine();
		return message;
	}
	
	/**
	 * Ferme les canaux de communication et se deconnecte du serveur.
	 */
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

	public boolean isRunning() {
		return _isRunning;
	}

	@Override
	public void run() {
		logger.info("Lancement du thread receiveMessage.");
		while(_isRunning) {
			String message = null;
			try {
				message = receiveMessage();
				if (message == null) {
					throw new IOException("Probleme lors de la réception d'un message non reçu.");
				}
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
			throw new RuntimeException();
		}
		Thread threadClient = new Thread(client);
		threadClient.start();
		
		Scanner scanner = new Scanner(System.in);

		System.out.println("Entrez votre pseudo:");
		client.setNickName(scanner.nextLine());

		while(client.isRunning()) {
			String input = scanner.nextLine();
			if (input.equals("/quit")) {
				logger.info("Commande entrée: " + "/quit");
				client.disconnectFromServer();
				break;
			}
			client.sendMessage(input);
		}
		client.disconnectFromServer();
	}
}
