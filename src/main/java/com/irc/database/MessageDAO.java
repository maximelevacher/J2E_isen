package com.irc.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.irc.metier.Message;

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
		// TODO Auto-generated method stub
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
}
