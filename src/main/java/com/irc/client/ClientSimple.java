package com.irc.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Cette classe implémente un client pouvant se connecter sur un serveur de chat
 * @author Zak
 *
 */
public class ClientSimple {
	/**
	 * Permet de logger des messages suivant le fichier de configuration log4j.properties
	 */
	static final Logger logger = Logger.getLogger(ClientSimple.class);
	static final String logConfigPath = "conf/log4j.properties";

	public final static int DEFAULT_PORT = 45612;
	private final static int CONNECT_TIMEOUT = 1000;
	
	private Socket _socket = null;
	private ObjectOutputStream _out = null;
	private ObjectInputStream _in = null;

	/**
	 * Connecte le client au serveur indiqué
	 * @param hote L'hote sur lequel le client doit se connecter
	 * @param port Le port sur lequel le client doit se connecter
	 * @throws IOException En cas de problème de connexion
	 */
	public void connectToServer(InetAddress hote, int port) throws IOException {
		_socket = new Socket();
		_socket.connect(new InetSocketAddress(hote, port), CONNECT_TIMEOUT);
		_out = new ObjectOutputStream(_socket.getOutputStream());
		_in = new ObjectInputStream(_socket.getInputStream());
		logger.info("Connecté au serveur: " + hote.toString() + ":" + port);
	}
	
	/**
	 * Demande au serveur de changer le pseudo du client
	 * @param nickname Le pseudo demandé
	 * @throws IOException 
	 */
	public void setNickName(String nickname) throws IOException {
		sendMessage("%nickname " + nickname);
	}

	/**
	 * Envoie un message au serveur
	 * @param message
	 * @throws IOException 
	 */
	public void sendMessage(String message) throws IOException {
		_out.writeObject(message);
		logger.info("Envoi du message: " + message);
	}
	
	/**
	 * Attends un message du serveur. La méthode est bloquante.
	 * @return Un string contenant le message se terminant par un \r ou \n
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public String receiveMessage() throws IOException, ClassNotFoundException {
		String message = null;
		message = (String) _in.readObject();
		logger.info("Message reçu: " + message);
		if(message == null) {
			throw new IOException("Impossible de récupérer un message.");
		}
		return message;
	}
	
	/**
	 * Ferme les canaux de communication et se deconnecte du serveur.
	 * @throws IOException Si le client n'arrive pas à se deconnecter
	 */
	public void disconnectFromServer() throws IOException {
		if(_socket != null) {
			_socket.close();
		}
		logger.info("Déconnexion du serveur.");
	}
}
