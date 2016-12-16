package com.irc.server;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.irc.database.Database;
import com.irc.database.MessageDAO;
import com.irc.metier.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Cette classe gère un client connecté sur le serveur
 * @author Zak
 *
 */
public class ServerThread implements Runnable {
	/**
	 * Permet de logger des messages suivant le fichier de configuration log4j.properties
	 */
	static final Logger logger = Logger.getLogger(ServerThread.class);
	static final String logConfigPath = "conf/log4j.properties";

	private ServerMultiClient _serverMultiClient;
	private Thread _thread;
	private Socket _socket;
	private ObjectOutputStream _out;
	private ObjectInputStream _in;
	private String _nickName;
	private int _id;
	private volatile boolean _isRunning = true;
	
	/**
	 * Récupère le socket d'un client venant de se connecter et lance son thread
	 * @param s Le socket récupéré
	 * @param serverMultiClient Le serveur de connexion
	 * @throws IOException En cas d'impossibilité d'ouverture des flux de communication
	 */
	public ServerThread(Socket s, ServerMultiClient serverMultiClient) throws IOException {
		_socket = s;
		_serverMultiClient = serverMultiClient;
		
		try {
			openStreams();
		} catch (IOException e) {
			throw new IOException(e);
		}
		
		_thread = new Thread(this);
		_thread.start();
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	/**
	 * Récupère les flux d'entrée et de sortie du socket.
	 * @throws IOException S'il est impossible de récuperer les flux
	 */
	public void openStreams() throws IOException {
		_out = new ObjectOutputStream(_socket.getOutputStream());
		_in = new ObjectInputStream(_socket.getInputStream());
		logger.info("Ouvre les streams du client qui vient de se connecter.");
	}
	
	/**
	 * Ferme les flux de communication et le socket du client
	 */
	public void closeStreams() {
		_isRunning = false;
		try {
			_out.close();
			_in.close();
			_socket.close();
			_serverMultiClient.deleteFromServerThreadList(this);
			logger.info("Ferme les streams du client " + getNickName());
		} catch (IOException e) {
			logger.error("Problème lors de la fermeture des streams.");
		}
	}
	
	/**
	 * Envoie un message vers le client connecté
	 * @param message le message à envoyer
	 * @throws IOException Si l'envoi à échoué
	 */
	public void sendMessage(Object message) {
		try {
			_out.writeObject(message);
			logger.info(getNickName() + "| Envoi du message: " + message);
		} catch (IOException e) {
			logger.error("Impossible d'envoyer un message.", e);
			closeStreams();
		}
	}

	/**
	 * Recoit un message depuis le client connecté
	 * @return Un string contenant le message se terminant pas un \r ou \n
	 */
	public String receiveMessage() {
		String message = null;
		try {
			message = (String) _in.readObject();
			if (message == null) {
				throw new IOException("Fin de stream.");
			}
			logger.info("Message reçu: " + message);
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Impossible de recevoir un message.", e);
			closeStreams();
		}
		return message;
	}
	
	public String getNickName() {
		return _nickName;
	}

	public void setNickName(String nickName) {
		this._nickName = nickName;
	}

	/**
	 * Le thread analysant et traitant les messages envoyés depuis le client
	 */
	@Override
	public void run() {
		logger.info("Envoi du message de bienvenue:" + " Bienvenue sur le serveur!");
		while (_isRunning) {
			String clientInput = receiveMessage();
			if (clientInput != null) {
				 if (clientInput.startsWith("%nickname")) {
					 String nickname = clientInput.split(" ")[1];
					 // Check si le username est banni
					 if(_serverMultiClient.isNicknameBanned(nickname)) {
						 sendMessage("%nickname_banned");
					 } else {
						 // Check s'il est dispo
						 if(_serverMultiClient.isNicknameAvailable(nickname)) {
							 setNickName(nickname);
							 sendMessage("%nickname_ok");
							 sendMessage("Bienvenue sur le serveur!");
							 Database db = new Database();
							 sendMessage(db.findLastTenMessages());
							 _serverMultiClient.broadcastMessage(nickname + " vient de se connecter.", this);
							 _serverMultiClient.broadcastMessage(_serverMultiClient.getListOfNicknameConnected());
						 } else {
							 sendMessage("%nickname_taken");
						 }
					 }
				} else if (clientInput.startsWith("%ping")) {
					sendMessage("Pong!");
				} else if (clientInput.startsWith("%kick")) {
					String userToKick = clientInput.split(" ")[1];
					if (_serverMultiClient.kickClientFromServer(userToKick, false)) {
						sendMessage("%kick_ok");
						_serverMultiClient.broadcastMessage(userToKick + " a été kick du serveur.");
						_serverMultiClient.broadcastMessage(_serverMultiClient.getListOfNicknameConnected());
					} else {
						sendMessage("%kick_failed");
					}
				} else if (clientInput.startsWith("%ban")) {
					String userToKick = clientInput.split(" ")[1];
					if (_serverMultiClient.banClientFromServer(userToKick)) {
						sendMessage("%ban_ok");
						_serverMultiClient.broadcastMessage(userToKick + " a été ban du serveur.");
						_serverMultiClient.broadcastMessage(_serverMultiClient.getListOfNicknameConnected());
					} else {
						sendMessage("%ban_failed");
					}
				} else if (clientInput.startsWith("%getListConnected")) {
					_serverMultiClient.broadcastMessage(_serverMultiClient.getListOfNicknameConnected());
				} else if (clientInput.startsWith("%privateMessage")) {
					String privateUsername = clientInput.split(" ", 3)[1];
					String privateMessage = clientInput.split(" ", 3)[2];
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String dateNow = dateFormat.format(new Date());
					String messageToSend = dateNow + " | " + getNickName() + " > " + privateMessage;
					logger.info("Envoi d'un message privé à " + privateUsername + ": " + messageToSend);
					Message objPrivateMessage = new Message(privateMessage, getNickName(), privateUsername);
					if (_serverMultiClient.sendPrivateMessage(objPrivateMessage, privateUsername)) {
						logger.info("Message privé envoyé.");
					} else {
						logger.info("Message privé raté. Le receiver est offline.");
						sendMessage("%privateMessageReceiverOffline");
					}
					
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String dateNow = dateFormat.format(new Date());
					String messageToSend = dateNow + " | " + getNickName() + " > " + clientInput;
					Message objMessage = new Message(clientInput, getNickName(), "_everyone");
					logger.info("Envoi d'un broadcast à tous les autres: " + messageToSend);
					_serverMultiClient.broadcastMessage(objMessage);
					logger.info("Enregistrement du message dans la bdd...");
					try {
						MessageDAO messageDAO = new MessageDAO();
						messageDAO.create(objMessage);
						logger.info("Message enregistré.");
					} catch (NullPointerException e) {
						logger.error("Le message n'a pas pu être enregistré dans la bdd.", e);
					}
				}
			}
		}
		logger.info("Fermeture du thread serveur du client " + getNickName());
		closeStreams();
		if (getNickName() != null) {
			_serverMultiClient.broadcastMessage(getNickName() + " vient de se déconnecter.", this);
			_serverMultiClient.broadcastMessage(_serverMultiClient.getListOfNicknameConnected(), this);
		}
	}
}
