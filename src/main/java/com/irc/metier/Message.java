package com.irc.metier;

import java.io.Serializable;
import java.sql.Date;

public class Message implements Serializable {
	private int num;
	private String message;
	private Personne sender;
	private Personne emmetter;
	private String sSender;
	private String sReceiver;
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

	public Message(String message, String sender, String receiver) {
		this.setsSender(sender);
		this.setsReceiver(receiver);
		this.setMessage(message);
		java.util.Date date_util = new java.util.Date();
		this.date = new java.sql.Date(date_util.getTime());
	}
	
	public Message(String message, String sender, String receiver, Date date) {
		this.setsSender(sender);
		this.setsReceiver(receiver);
		this.setMessage(message);
		this.setDate(date);
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
	
	public String getsSender() {
		return sSender;
	}
	public void setsSender(String sSender) {
		this.sSender = sSender;
	}
	
	public String getsReceiver() {
		return sReceiver;
	}
	
	public void setsReceiver(String sReceiver) {
		this.sReceiver = sReceiver;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
}
