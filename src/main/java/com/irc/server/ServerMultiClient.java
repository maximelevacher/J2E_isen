package com.irc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServerMultiClient {
	static final Logger logger = Logger.getLogger(ServerMultiClient.class);
	static final String logConfigPath = "conf/log4j.properties";

	static final int CONNECTIONS_LIMIT = 5;

	private ServerSocket serverSocket = null;
	private int defaultPort = 45612;
	private boolean isRunning = true;
	private Vector<ServerThread> _tabServerThreads = new Vector<ServerThread>();

	public ServerMultiClient() {
		logger.info("Lance le serveur sur le port: " + defaultPort);
		try {
			serverSocket = new ServerSocket(defaultPort);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger.info("Serveur lancé.");
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

	public void broadcastMessage(String message) {
		for (ServerThread t : _tabServerThreads) {
			t.sendMessage(message);
		}
		logger.info("Envoi d'un broadcast: " + message);
	}

	public void broadcastMessage(String message, ServerThread s) {
		for (ServerThread t : _tabServerThreads) {
			if (t.equals(s)) {
				continue;
			}
			t.sendMessage(message);
		}
		logger.info("Envoi d'un broadcast sauf à " + s.getNickName() + ": " + message);
	}

	public void deleteFromServerThreadList(ServerThread s) {
		_tabServerThreads.remove(s);
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(logConfigPath);
		ServerMultiClient server = new ServerMultiClient();
	}
}
