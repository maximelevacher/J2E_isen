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
	
	public ServerThread(Socket s, ServerMultiClient serverMultiClient) {
		_socket = s;
		_serverMultiClient = serverMultiClient;
		
		openStreams();
		
		_thread = new Thread(this);
		_thread.start();
	}
	
	/**
	 * Récupère les flux d'entrée et de sortie 
	 */
	public void openStreams() {
		try {
			_out = new PrintWriter(_socket.getOutputStream(), true);
			_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ferme les flux et le socket
	 */
	public void closeStreams() {
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
		boolean bQuit = false;
		_serverMultiClient.broadcastMessage("Salut a tous!");
		while (!bQuit) {
			String clientInput = receiveMessage();
			System.out.println("Message du client: " + clientInput);
			if (clientInput.equals("/quit")) {
				bQuit = true;
				System.out.println("recu quit");
			}
			System.out.println("Envoi broadcast : " + clientInput);
			_serverMultiClient.broadcastMessage(clientInput);
		}
		System.out.println("Quit server thread");
		closeStreams();
		return;
	}
}
