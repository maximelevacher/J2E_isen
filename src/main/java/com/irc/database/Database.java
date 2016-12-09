package com.irc.database;

import java.util.ArrayList;
import java.sql.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class Database {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://89.89.15.87:150/irc";
	static Connection conn = null;
	Statement stmt = null;
	// Database credentials
	static final String USER = "irc";
	static final String PASS = "chatdat";

	/**
	 * Méthode qui va nous retourner notre instance
	 * et la créer si elle n'existe pas...
	 * @return
	 */
	public static Connection getInstance(){
		if(conn == null){
			try {
				// STEP 3: Open a connection
				System.out.println("Connecting to database...");
				conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return conn;	
	}

	/**
	 * Retourne une liste de chaine de caractère comprenant l'ensemble des
	 * messages correspondant aux id contenu dans les parametre de la fonction .
	 * Concernant les paramètres il faut spécifier les 2 id des personnes
	 * correspondant à la conversation exemple pour la conversation général on
	 * indique 1 et 12 pour l'utilisateur Zak
	 * <p>
	 * This method always returns immediately, the list of all messages between
	 * 2 persons The list is an arrayList of string
	 *
	 * @param idSender
	 *            an id of person who send the message
	 * @param idEmeter
	 *            an id of person who recive the message
	 * @return ArrayList of all messages between this 2 person
	 * @throws SQLException
	 */
	public ArrayList<String> findAllMessage(int idSender, int idEmeteur) throws SQLException {
		ArrayList<String> listMessages = new ArrayList<String>();
		stmt = (Statement) conn.createStatement();
		String sql;
		sql = "SELECT * FROM message WHERE ME_sender="+idSender+" and ME_emeteur="+idEmeteur+" and ME_emeteur="+idSender+" and ME_sender="+idEmeteur+" ORDER BY ME_num ASC";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			// Retrieve by column name
			String message = rs.getString("ME_text");
			listMessages.add(message);
		}
		return listMessages;
	}

	/**
	 * Permet d'ajouter un nouveau message dans la base de données Concernant
	 * les paramètres il faut spécifier les 2 id des personnes correspondant à
	 * la conversation ainsi que le texte à ajouter et le type de message
	 * <p>
	 * This method insert a new message in the database
	 *
	 * @param idSender
	 *            an id of person who send the message
	 * @param idEmeter
	 *            an id of person who recive the message
	 * @param message
	 *            Message to save in database
	 * @param typeMessage
	 *            Type de message à sauvegarder
	 * @throws SQLException
	 */
	public void insertMessage(int idSender,int idEmeter,String message,String type) throws SQLException {
	      stmt = (Statement) conn.createStatement();
	      
	      String sql = "INSERT INTO message(ME_sender,ME_emeteur,ME_text,ME_type)" +
	                   "VALUES ("+idSender+", "+idEmeter+", "+message+", "+type+")";
	      stmt.executeUpdate(sql);
	}
	
	/**
	 * Permet de récupérer les 10 derniers messages d'une conversation
	 * <p>
	 * This method insert a new message in the database
	 *
	 * @param idSender
	 *            an id of person who send the message
	 * @param idEmeter
	 *            an id of person who recive the message
	 * @return ArrayList of all messages between this 2 person
	 * @throws SQLException
	 */
	public ArrayList<String> findLastTenMessages(int idSender, int idEmeteur) throws SQLException {
		ArrayList<String> listMessages = new ArrayList<String>();
		stmt = (Statement) conn.createStatement();
		String sql;
		sql = "SELECT * FROM message WHERE ME_sender="+idSender+" and ME_emeteur="+idEmeteur+" and ME_emeteur="+idSender+" and ME_sender="+idEmeteur+" order by ME_num DESC Limit 0,10 ";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			// Retrieve by column name
			String message = rs.getString("ME_text");
			listMessages.add(message);
		}
		return listMessages;
	}
}
