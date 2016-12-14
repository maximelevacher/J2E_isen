package com.irc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Cette classe gère le serveur et les opérations concernant tous les clients connectés
 * @author Zak
 *
 */
public class ServerMultiClient {
	/**
	 * Permet de logger des messages suivant le fichier de configuration log4j.properties
	 */
	static final Logger logger = Logger.getLogger(ServerMultiClient.class);
	static final String logConfigPath = "conf/log4j.properties";

	static final int CONNECTIONS_LIMIT = 100;
	static final int DEFAULT_PORT = 45612;

	private ServerSocket serverSocket = null;
	volatile private boolean isRunning = false;

	/**
	 * Contient les clients connectés au serveur
	 */
	private Vector<ServerThread> _tabServerThreads = new Vector<ServerThread>();

	/**
	 * Lance le serveur et commence à accepter les connexions vers celui-ci
	 */
	public void startServer() {
		logger.info("Lance le serveur sur le port: " + DEFAULT_PORT);
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger.info("Serveur lancé.");
		isRunning = true;
		while (isRunning) {
			logger.info("En attente de connexion de la part d'un client...");
			try {
				ServerThread newClient = new ServerThread(serverSocket.accept(), this);
				_tabServerThreads.addElement(newClient);
				if (_tabServerThreads.size() > CONNECTIONS_LIMIT) {
					newClient.closeStreams();
				}
				logger.info("Client connecté au serveur.");
			} catch (IOException e) {
				logger.error("Un client n'a pas réussi à se connecter.", e);
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Le serverSocket ne s'est pas fermé normalement.", e);
		}
	}

	/**
	 * Envoie un message à tous les clients connectés
	 * @param message Le message à envoyer
	 */
	public void broadcastMessage(String message) {
		for (ServerThread t : _tabServerThreads) {
			t.sendMessage(message);
		}
		logger.info("Envoi d'un broadcast: " + message);
	}

	/**
	 * Envoie un message à tous les clients connectés sauf celui passé en paramètre
	 * @param message Le message à envoyer
	 * @param s Le client à ignorer
	 */
	public void broadcastMessage(String message, ServerThread s) {
		for (ServerThread t : _tabServerThreads) {
			if (t.equals(s)) {
				continue;
			}
			t.sendMessage(message);
		}
		logger.info("Envoi d'un broadcast sauf à " + s.getNickName() + ": " + message);
	}

	/**
	 * Supprime un client de la liste des clients connectés
	 * @param s Le client à supprimer
	 */
	public void deleteFromServerThreadList(ServerThread s) {
		_tabServerThreads.remove(s);
	}

	public boolean isNicknameAvailable(String nickname) {
		for (ServerThread t : _tabServerThreads) {
			if (t.getNickName() == null) {
				continue;
			}
			if (t.getNickName().equals(nickname)) {
				return false;
			}
		}
		return true;
	}
	
	public Vector<String> getListOfNicknameConnected() {
		Vector<String> listNicknames = new Vector<String>();
		for (ServerThread t : _tabServerThreads) {
			listNicknames.add(t.getNickName());
		}
		return listNicknames;
	}

	public void shutdownServer() throws IOException {
		isRunning = false;
		serverSocket.close();
	}

	public int getNumberOfConnectedClients() {
		return _tabServerThreads.size();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(logConfigPath);
		ServerMultiClient server = new ServerMultiClient();
		server.startServer();
	}
}
