package com.irc.server;

import java.net.Socket;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerThread implements Runnable {
	static final Logger logger = Logger.getLogger(ServerThread.class);
	static final String logConfigPath = "conf/log4j.properties";

	private ServerMultiClient _serverMultiClient;
	private Thread _thread;
	private Socket _socket;
	private PrintWriter _out;
	private BufferedReader _in;
	private String _nickName;
	private int _id;
	private volatile boolean _isRunning = true;
	
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
	 * Récupère les flux d'entrée et de sortie 
	 * @throws IOException
	 */
	public void openStreams() throws IOException {
		_out = new PrintWriter(_socket.getOutputStream(), true);
		_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		logger.info("Ouvre les streams du client qui vient de se connecter.");
	}
	
	/**
	 * Ferme les flux et le socket
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
	
	public void sendMessage(String message) {
		_out.println(message);
		logger.info(getNickName() + "| Envoi du message: " + message);
	}

	public String receiveMessage() {
		String message = null;
		try {
			message = _in.readLine();
			if (message == null) {
				throw new IOException("Fin de stream.");
			}
			logger.info("Message reçu: " + message);
			if (message.startsWith("%nickname")) {
				setNickName(message.split(" ")[1]);
				message = null;
			}
		} catch (IOException e) {
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

	@Override
	public void run() {
		logger.info("Envoi du message de bienvenue:" + " Bienvenue sur le serveur!");
		sendMessage("Bienvenue sur le serveur!");
		while (_isRunning) {
			String clientInput = receiveMessage();
			if (clientInput != null) {
				logger.info("Envoi d'un broadcast à tous les autres: " + clientInput);
				_serverMultiClient.broadcastMessage(clientInput, this);
			}
		}
		logger.info("Fermeture du thread serveur du client " + getNickName());
		closeStreams();
	}
}
