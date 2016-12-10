package com.irc.metier;

public class Personne {
	private long id;
	private String nickname;

	public Personne() {

	}

	public Personne(int id, String nickname) {
		this.id = id;
		this.nickname = nickname;
	}

	public void setId(long l) {
		this.id = l;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public long getId() {
		return this.id;
	}
}
