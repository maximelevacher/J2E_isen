package com.irc.metier;

import java.sql.Date;

public class Message {
	private int num;
	private String message;
	private Personne sender;
	private Personne emmetter;
	private Date date;
	
	public Message() {
	}
	public Message(int num,String message,Personne sender, Personne emmetter){
		this.num = num;
		this.message = message;
		this.sender=sender;
		this.emmetter = emmetter;
		java.util.Date date_util = new java.util.Date();
		this.date =new java.sql.Date(date_util.getTime());
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Personne getSender() {
		return sender;
	}

	public void setSender(Personne sender) {
		this.sender = sender;
	}

	public Personne getEmmetter() {
		return emmetter;
	}

	public void setEmmetter(Personne emmetter) {
		this.emmetter = emmetter;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String toString(){
		return " "+sender.getNickname()+" > "+getMessage();
	}
}
