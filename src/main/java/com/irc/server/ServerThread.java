package com.irc.server;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerThread implements Runnable {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		System.out.println("Envoi message");
		sendMessage("Bienvenue sur le server!");
		_serverMultiClient.broadcastMessage("Salut a tous!");
		while (_isRunning) {
			String clientInput = receiveMessage();
			System.out.println("Message du client: " + clientInput);
			if (clientInput.equals("/quit")) {
				_isRunning = false;
				System.out.println("recu quit");
			}
			if (clientInput != null) {
				System.out.println("Envoi broadcast : " + clientInput);
				_serverMultiClient.broadcastMessage(clientInput, _id);
			}
		}
		System.out.println("Quit server thread");
		closeStreams();
	}
}
