package com.irc.database;

import java.util.ArrayList;
import java.sql.*;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class Database {
	  // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/silex_api";
	   Connection conn = null;
	   Statement stmt =null;
	   //  Database credentials
	   static final String USER = "irc";
	   static final String PASS = "chatdat";
	   public Database() throws ClassNotFoundException{
		   this.conn = createConnection();
	   }
	   public Connection createConnection() throws ClassNotFoundException{
		   try{
			   //STEP 2: Register JDBC driver
			   Class.forName("com.mysql.jdbc.Driver");

			   //STEP 3: Open a connection
			   System.out.println("Connecting to database...");
			   conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);
		   }catch(SQLException e){
			   e.printStackTrace();
		   }
		   return conn;
	   }
	   /**
	    * Retourne une liste de chaine de caractère comprenant l'ensemble 
	    * des messages correspondant aux id contenu dans les parametre de la fonction . 
	    * Concernant les paramètres il faut spécifier les 2 id des personnes correspondant
	    * à la conversation
	    * exemple pour la conversation général on indique 1 et 12 pour l'utilisateur Zak
	    * <p>
	    * This method always returns immediately, the list of all messages between 2 persons 
	    * The list is an arrayList of string 
	    *
	    * @param  idSender  an id of person who send the message
	    * @param  idEmeter  an id of person who recive the message
	    * @return ArrayList of all messages between this 2 person
	    * @throws SQLException 
	    */
	   public ArrayList<String> findAllMessage(int idSender, int idEmeteur) throws SQLException{
		   ArrayList<String> listMessages = new ArrayList<String>();
		   stmt = (Statement) conn.createStatement();
		   String sql;
           sql = "SELECT * FROM message";
		   ResultSet rs = stmt.executeQuery(sql);
		   while(rs.next()){
		         //Retrieve by column name
		         String message = rs.getString("ME_text");
		         listMessages.add(message);
		    }
		   return listMessages;
	   }

	   
}
