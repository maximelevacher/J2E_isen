package com.irc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSimple {

	public static void main(String[] args) {
		// Port sur lequel écouter
		int portNumber = 45612;

		try {
			// Crée un socket en attente de connexion sur le port défini
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while (true) {
				System.out.println("En attente de connexion au serveur sur le port: " + portNumber);
				// Attends une connexion et l'accepte (action bloquante)
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connexion acceptée.");
				// Récupère le flux de sortie du client connecté
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				// Récupère le flux d'entrée du client connecté
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				// Envoie un message d'accueil au client connecté
				System.out.println("Envoi du message: " + "Bienvenue sur le serveur.");
				out.println("Bienvenue sur le serveur.");
				// Attends que le client réponde et affiche sa réponse
				String clientInput = in.readLine();
				System.out.println("Message du client: " + clientInput);
				// Ferme la connexion au client
				clientSocket.close();
				System.out.println("Connexion terminée.");
				System.out.println("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
