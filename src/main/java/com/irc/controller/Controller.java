package com.irc.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.irc.client.ClientSimple;
import com.irc.ihm.GUI;
import com.irc.ihm.LoginWindow;
import com.irc.metier.Message;

/**
 * Cette classe fait le lien entre l'IHM et le client
 * @author Zak
 *
 */
public class Controller {
	static final Logger logger = Logger.getLogger(Controller.class);
	static final String logConfigPath = "conf/log4j.properties";
	
	private static final String pathServerConfFile = "conf/servers.txt";

	private static final String adminPassword = "admin";

	static enum States {
		START, LOGIN, CONNECTION, CONNECTED, DISCONNECTED, SERVER_PROBLEM, KICKED
	}
	
	public States state = States.START;
	
	ClientSimple client = null;
	GUI view = null;
	LoginWindow login = null;

	private Thread threadReceiveMessages = null;
	
	private volatile boolean _isRunning = true;
	private String _username = null;
	
	public Controller(ClientSimple c, GUI v, LoginWindow l) {
		client = c;
		view = v;
		login = l;
	}
	
	public boolean startClient() {
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
				if(!i.hasNext()) {
					logger.error("N'a pu se connecter à aucun serveur.", e);
					//System.exit(1);
					return false;
				}
			}
		}
		
		_isRunning = true;
		threadReceiveMessages = new Thread() {
			public void run() {
				while(_isRunning) {
					try {
						Object objReceived= client.receiveMessage();
						String message = null;
						// Si on recoit une commande du serveur
						if (objReceived instanceof String) {
							message = (String) objReceived;
							if(checkMessageCommand(message)) {
								view.appendMessageToUserTab(message);
							}
						// Si on recoit un message
						} else if (objReceived instanceof Message) {
							Message msgReceived = (Message) objReceived;
							DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							String dateMessage = dateFormat.format(msgReceived.getDate());
							String messageToDisplay = dateMessage + " | " + msgReceived.getsSender() + " > " + msgReceived.getMessage();
							if (msgReceived.getsReceiver().equals("_everyone")) {
								view.appendMessageToUserTab(messageToDisplay);
							} else {
								view.appendMessageToUserTab(messageToDisplay, msgReceived.getsSender());
							}
						// Si on recoit une liste
						} else if (objReceived instanceof Vector) {
							try {
								// Si on recoit une liste de personnes connectées
								if (((Vector) objReceived).get(0) instanceof String) {
									view.updateListConnected((Vector<String>) objReceived);
								// Si on recoit une liste de messages
								} else if (((Vector) objReceived).get(0) instanceof Message) {
									for (Message m : (Vector<Message>) objReceived) {
										DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
										view.appendMessageToUserTab(dateFormat.format(m.getDate()) + " | " + m.getsSender() + " > " + m.getMessage());
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								// Envoie un vecteur vide de string pour quand même update la liste des users
								view.updateListConnected(new Vector<String>());
								logger.error("Le vector reçu était vide.");
							}
						}
					} catch (IOException | ClassNotFoundException e) {
						_isRunning = false;
						logger.error("Probleme lors de la réception du message.", e);
						try {
							client.disconnectFromServer();
							setState(Controller.States.DISCONNECTED);
						} catch (IOException e1) {
							logger.error("Probleme lors de la deconnexion.", e1);
						}
					}
				}
			}
		};
		threadReceiveMessages.start();
		return true;
	}
	
	public void stopClient() {
		try {
			client.disconnectFromServer();
			if (threadReceiveMessages != null) {
				threadReceiveMessages.join();
			}
		} catch (IOException | InterruptedException e) {
			logger.error("Probleme lors de la deconnexion.", e);
		}
	}
	
	/**
	 * Vérifie si le message est une commande ou réponse du serveur.<br/>
	 * Si elle l'est, elle la traite puis indique s'il faut afficher le message ou pas.
	 * @param message le message à traiter
	 * @return Si le message doit être affiché ou pas
	 */
	private boolean checkMessageCommand(String message) {
		// Indique de ne pas afficher le message par défaut
		boolean displayMessage = false;
		if (message.equals("%nickname_ok")) {
			setState(Controller.States.CONNECTION);
		} else if (message.equals("%nickname_taken")) {
			login.showError("Connexion impossible", "Le pseudonyme est déjà pris");
			_username = null;
			setState(Controller.States.DISCONNECTED);
		} else if (message.equals("%privateMessageReceiverOffline")) {
			login.showError("Message privé annulé", "Impossible d'envoyer un message privé.\nL'utilisateur est déconnecté.");
		} else if (message.equals("%kick_failed")) {
			login.showError("Kick impossible", "Le kick a echoué.");
		} else if (message.equals("%kick_ok")) {
			logger.info("Kick réussi.");
		} else if (message.equals("%kicked")) {
			login.showError("Kicked!", "Vous avez été kick!");
			setState(Controller.States.KICKED);
		} else {
			// Si ce n'est pas une commande on affiche le message
			displayMessage = true;
		}
		return displayMessage;
	}

	public void onClickOnSendMessage(String message) {
		try {
			client.sendMessage(message);
		} catch (IOException e) {
			logger.error("Impossible d'envoyer un message.", e);
			try {
				client.disconnectFromServer();
			} catch (IOException e1) {
			}
		}
	}
	
	public void onClickOnSendMessage(String message, String username) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String dateMessage = dateFormat.format(new Date());
			String messageToDisplay = dateMessage + " | " + get_username() + " > " + message;
			view.appendMessageToUserTab(messageToDisplay, username);
			client.sendMessage("%privateMessage " + username + " " + message);
		} catch (IOException e) {
			logger.error("Impossible d'envoyer un message.", e);
			try {
				client.disconnectFromServer();
			} catch (IOException e1) {
			}
		}
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
	
	public void onClickOnLoginButton(String username, String password) {
		if (username.equals("Admin") && !password.equals(adminPassword)) {
			login.showError("Connexion admin impossible", "Le mot de passe du compte Admin est incorrect.");
			return;
		}
		if (username == null || username.isEmpty()) {
			login.showError("Connexion impossible", "Le pseudonyme ne peut pas être vide.");
		} else if (username.startsWith("_")) {
			login.showError("Connexion impossible", "Le pseudonyme ne peut pas commencer par un '_'");
		} else if (!username.matches("^[a-zA-Z0-9]*$")) {
			login.showError("Connexion impossible", "Le pseudonyme ne peut contenir que des chiffres et lettres sans accents.");
		}
		else {
			try {
				client.setNickName(username);
				_username = username;
			} catch (IOException | NullPointerException e) {
				logger.error("Impossible de définir le pseudonyme.", e);
				setState(States.DISCONNECTED);
			}
		}
	}
	
	public void askForKickUser(String username) {
		try {
			client.sendMessage("%kick " + username);
		} catch (IOException e) {
			logger.error("Impossible d'envoyer un message.", e);
			try {
				client.disconnectFromServer();
			} catch (IOException e1) {
			}
		}
	}

	public boolean isClientAdmin() {
		if (get_username().equals("Admin")) {
			return true;
		} else {
			return false;
		}
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
		LoginWindow login = new LoginWindow();

		Controller controller = new Controller(client, viewConnected, login);

		viewConnected.addListenener(controller);
		login.addListenener(controller);
		
		int nbRetries = 0;

		while (true) {
			switch(controller.getState()) {
				case START:
					login.showConnectingBox(true);
					if(controller.startClient()) {
						login.setVisible(true);
						viewConnected.setVisible(false);
						if (controller.get_username() != null) {
							controller.setState(Controller.States.CONNECTION);
							try {
								client.setNickName(controller.get_username());
							} catch (IOException e) {
								controller.setState(Controller.States.DISCONNECTED);
							}
						} else {
							controller.setState(Controller.States.LOGIN);
						}
					} else {
						controller.setState(Controller.States.DISCONNECTED);
					}
					login.showConnectingBox(false);
					break;
				case LOGIN:
					break;
				case CONNECTION:
					viewConnected.setVisible(true);
					login.setVisible(false);
					viewConnected.enableUserEntries(true);
					controller.setState(Controller.States.CONNECTED);
					break;
				case CONNECTED:
					nbRetries = 0;
					break;
				case DISCONNECTED:
					viewConnected.enableUserEntries(false);
					nbRetries++;
					controller.stopClient();
					viewConnected.appendMessageToUserTab("Déconnecté du serveur. Reconnexion...");
					if (nbRetries >= 3) {
						controller.setState(Controller.States.SERVER_PROBLEM);
					} else {
						controller.setState(Controller.States.START);
					}
					break;
				case KICKED:
					viewConnected.enableUserEntries(false);
					controller.stopClient();
					viewConnected.appendMessageToUserTab("Vous avez été kick!");
					controller.setState(Controller.States.SERVER_PROBLEM);
					break;
				case SERVER_PROBLEM:
					logger.error("Impossible de se connecter à un serveur après 3 tentatives.");
					login.showError("Connexion impossible", "Impossible de se connecter à un serveur après 3 tentatives.");
					// Attendre une action de l'utilisateur sur la reconnexion
					while(true) {
						if (viewConnected.isVisible()) {
							
						} else {
							System.exit(1);
						}
					}
					//break;
			}
		}
	}
}
