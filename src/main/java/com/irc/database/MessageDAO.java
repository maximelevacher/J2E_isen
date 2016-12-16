package com.irc.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.irc.metier.Message;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class MessageDAO extends DAO<Message>{

	@Override
	public Message find(long id) throws SQLException {
		
		Message message = new Message();
		String sql = "SELECT * FROM message WHERE ME_num="+id;
		try {
			ResultSet result = (ResultSet) connect.createStatement().executeQuery(sql);
			if(result.first()){
				while (result.next()) {
					// Retrieve by column name
					message.setMessage(result.getString("ME_id"));
				//	message.setDate((Date)result.getString("ME_date"));
					message.setSender(new PersonneDAO().find(result.getLong("ME_sender")));
					message.setEmmetter(new PersonneDAO().find(result.getLong("ME_emeteur")));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	@Override
	public Message create(Message obj) {
		Connection connection = null;
		PreparedStatement stmt = null;
		String preparedSql = 	"INSERT INTO message(ME_sSender,ME_sReceiver,ME_text,ME_type)" +
				 				"VALUES (?, ?, ?, 'text')";
		try {
			connection = Database.connectToDb();
			stmt = (PreparedStatement) connect.prepareStatement(preparedSql);
			stmt.setObject(1, obj.getsSender());
			stmt.setObject(2, obj.getsReceiver());
			stmt.setObject(3, obj.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
		try {
			stmt.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public Message update(Message obj) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void delete(Message obj) {
		// TODO Auto-generated method stub
		
	}
	
	public Message findLastMessage(long id) throws SQLException {
		Message message = new Message();
		String sql = "SELECT * FROM message WHERE ME_num="+id+" ORDER BY DESC LIMIT 0,1";
		try {
			ResultSet result = (ResultSet) connect.createStatement().executeQuery(sql);
			if(result.first()){
				while (result.next()) {
					// Retrieve by column name
					message.setMessage(result.getString("ME_id"));
				//	message.setDate((Date)result.getString("ME_date"));
					message.setSender(new PersonneDAO().find(result.getLong("ME_sender")));
					message.setEmmetter(new PersonneDAO().find(result.getLong("ME_emeteur")));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	

	
}
