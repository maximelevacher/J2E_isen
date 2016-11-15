package com.irc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSimple {

	public static void main(String[] args) {
		// Variables accueillant l'hote et le port sur lequel se connecter
		InetAddress hote = null;
		int port = 45612; // par défaut
		Socket socket = null;

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
			e.printStackTrace();
		}
		
		try {
			// Se connecte sur le serveur avec les informations recueillies depuis les arguments
			socket = new Socket(hote, port);
			// Récupère les flux d'entrée et de sortie du server
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Attends le message du serveur et l'affiche
			String serverInput = in.readLine();
			System.out.println("Message du server: " + serverInput);
			// Envoie au server un message
			System.out.println("Envoi du message: " + "Merci!");
			out.println("Merci!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
