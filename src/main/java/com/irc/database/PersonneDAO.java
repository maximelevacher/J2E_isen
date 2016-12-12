package com.irc.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.irc.metier.Message;
import com.irc.metier.Personne;
import com.mysql.jdbc.Statement;

public class PersonneDAO extends DAO<Personne>{

	@Override
	public Personne find(long id) throws SQLException {
		Personne personne = new Personne();
		String sql = "SELECT * FROM client WHERE CL_num="+id;
		try {
			ResultSet result = (ResultSet) connect.createStatement().executeQuery(sql);
			if(result.first()){
				while (result.next()) {
					// Retrieve by column name
					personne.setNickname(result.getString("CL_nickname"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return personne;
	}

	public Personne findByNickname(String nickname) throws SQLException {
		Personne personne = new Personne();
		String sql = "SELECT * FROM client WHERE CL_nickname='"+nickname+"'";
		try {
			ResultSet result = (ResultSet) connect.createStatement().executeQuery(sql);
			if(result.first()){
				// Retrieve by column name
				personne.setNickname(result.getString("CL_nickname"));
				personne.setId(result.getLong("CL_id"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return personne;
	}	
	

	@Override
	public Personne create(Personne obj) {
		 Statement stmt = null;
		try {
			stmt = (Statement) connect.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
		String sql = "Insert into client(CL_nickname) VALUES ('"+obj.getNickname()+"')";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Personne update(Personne obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Personne obj) {
		// TODO Auto-generated method stub
		
	}

}
