package com.irc.metier;

public class Personne {
	private int id;
	private String nickname;
	public Personne(){
		
	}
	public Personne(int id,String nickname){
		this.id = id;
		this.nickname=nickname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}

