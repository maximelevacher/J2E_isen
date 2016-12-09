package com.irc.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.irc.metier.Message;
import com.irc.metier.Personne;

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

	@Override
	public Personne create(Personne obj) {
		// TODO Auto-generated method stub
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
