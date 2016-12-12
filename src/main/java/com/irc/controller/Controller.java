package com.irc.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.irc.client.ClientSimple;
import com.irc.ihm.GUI;
import com.irc.ihm.LoginWindow;

/**
 * Cette classe fait le lien entre l'IHM et le client
 * @author Zak
 *
 */
public class Controller {
	static final Logger logger = Logger.getLogger(Controller.class);
	static final String logConfigPath = "conf/log4j.properties";
	
	private static final String pathServerConfFile = "conf/servers.txt";

	static enum States {
		LOGIN, CONNECTION, CONNECTED
	}
	
	public States state = States.LOGIN;
	
	ClientSimple client = null;
	GUI view = null;

	private volatile boolean _isRunning = true;
	private String _username = null;
	
	public Controller(ClientSimple c, GUI v) {
		client = c;
		view = v;
	}
	
	public void startClient() {
		// Récupère les serveurs depuis le fichier et tente de se connecter à chacun de ceux-ci
		boolean hasConnected = false;
		LinkedHashMap<InetAddress, Integer> serveurs = loadServersFromFile(pathServerConfFile);
		Iterator<Map.Entry<InetAddress, Integer>> i = serveurs.entrySet().iterator();
		while(!hasConnected) {
			try {
				// On a traversé toute la liste, on arrête le programme.
				if(!i.hasNext()) {
					throw new IOException("Tous les serveurs sont hors ligne.");
				}
				// On tente de se connecter au prochain serveur
				Map.Entry<InetAddress, Integer> e = (Entry<InetAddress, Integer>) i.next();
				client.connectToServer(e.getKey(), e.getValue());
				// Si aucune exception est levée c'est qu'on a reussi, on sort alors de la boucle
				hasConnected = true;
			} catch (IOException e) {
				logger.error("N'a pu se connecter à aucun serveur.", e);
				System.exit(1);
			}
		}

		Thread threadReceiveMessages = new Thread() {
			public void run() {
				while(_isRunning) {
					try {
						String message = client.receiveMessage();
						view.appendMessageToArea(message);
					} catch (IOException e) {
						e.printStackTrace();
						try {
							client.disconnectFromServer();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		threadReceiveMessages.start();
	}

	public void onClickOnSendMessage(String message) {
		client.sendMessage(message);
	}
	
	/**
	 * Retourne la liste des serveurs contenus dans le fichier donné en paramètre
	 * @param path Le fichier à lire
	 */
	public LinkedHashMap<InetAddress, Integer> loadServersFromFile(String path) {
		// Crée une liste de serveurs vide
		LinkedHashMap<InetAddress, Integer> serveurs = new LinkedHashMap<InetAddress, Integer>();
		List<String> lines = null;
		try {
			// Lis toutes les lignes du fichier de config
			lines = Files.readAllLines(Paths.get(path));
			// Pour chaque ligne, ajoute le serveur dans la liste
	        for (String line : lines) {
	        	InetAddress host = InetAddress.getByName(line.split(":")[0]);
	        	int port = Integer.parseInt(line.split(":")[1]);
	        	serveurs.put(host, port);
	        }
		} catch (IOException e) {
			try {
				// Si on arrive pas à ouvrir le fichier, on ajoute le serveur par défaut (localhost)
				serveurs.put(InetAddress.getLocalHost(), ClientSimple.DEFAULT_PORT);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
		return serveurs;
	}
	
	public void onClickOnLoginButton(String username) {
		_username = username;
		state = States.CONNECTION;
	}
	
	public States getState() {
		return state;
	}
	
	public void setState(States s) {
		state = s;
	}
	
	public String get_username() {
		return _username;
	}

	public void set_username(String _username) {
		this._username = _username;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(logConfigPath);

		ClientSimple client = new ClientSimple();
		GUI viewConnected = new GUI();
		
		Controller controller = new Controller(client, viewConnected);

		viewConnected.addListenener(controller);

		LoginWindow login = new LoginWindow();
		login.addListenener(controller);
		
		while (true) {
			switch(controller.getState()) {
				case LOGIN:
					break;
				case CONNECTION:
					viewConnected.setVisible(true);
					controller.startClient();
					client.setNickName(controller.get_username());
					controller.setState(Controller.States.CONNECTED);
					break;
				case CONNECTED:
					break;
			}
		}
	}
}
