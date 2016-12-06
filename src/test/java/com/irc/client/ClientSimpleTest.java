package com.irc.client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientSimpleTest {
	
	ServerSocket ss = null;
	Vector<ServerSideClient> connectedClients = null;
	Thread thread = null;
	volatile boolean isRunning = true;
	
	static Vector<String> messageTestReceiveSequence = new Vector<String>();
	
	class ServerSideClient implements Runnable {
		Socket _s;
		PrintWriter _out;
		BufferedReader _in;
		Vector<String> messageRecus = new Vector<String>();
		
		ServerSideClient(Socket s) {
			_s = s;
			try {
				_in = new BufferedReader(new InputStreamReader(_s.getInputStream()));
				_out = new PrintWriter(_s.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			while(true) {
				try {
					String message = _in.readLine();
					messageRecus.addElement(message);
					if (message.startsWith("%nickname")) {
						String nickname = message.split(" ")[1];
						_out.println("Nickname:" + nickname);
					} else if (message.equals("Start Test Receive Sequence")) {
						for (String s : messageTestReceiveSequence) {
							_out.println(s);
						}
					} else if (message.equals("Disconnect Server")) {
						_s.close();
					}
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		messageTestReceiveSequence.add("Test");
		messageTestReceiveSequence.add("");
		messageTestReceiveSequence.add("ЁЂЃЄЅІЇЈЉЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя");
		messageTestReceiveSequence.add("찦차를 타고 온 펲시맨과 쑛다리 똠방각하");
		messageTestReceiveSequence.add("Stop Test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ss = new ServerSocket();
		ss.setReuseAddress(true);
		ss.bind(new InetSocketAddress(55555));
		
		connectedClients = new Vector<ServerSideClient>();
		isRunning = true;
		
		thread = new Thread() {
			public void run() {
				while(isRunning) {
					try {
						ServerSideClient sc = new ServerSideClient(ss.accept());
						Thread tsc= new Thread(sc);
						tsc.start();
						connectedClients.addElement(sc);
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}

	@After
	public void tearDown() throws Exception {
		isRunning = false;

		try {
			ss.close();
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testConnexionReussie() {
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.sendMessage("Test");
		} catch (IOException e) {
			e.printStackTrace();
			fail("La connexion n'a pas réussie.");
		}
	}
	
	@Test
	public void testConnexionRatee() {
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55554);
			client.sendMessage("Test");
			fail("Le client a réussi à se connecter.");
		} catch (IOException e) {
		}
		
		client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getByName("8.8.8.8"), 55555);
			client.sendMessage("Test");
			fail("Le client a réussi à se connecter.");
		} catch (IOException e) {
		}
	}
	
	@Test
	public void testDisconnect() {
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
		} catch (IOException e) {
		}
		client.disconnectFromServer();
		try {
			client.receiveMessage();
			fail("Le client est resté connecté.");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testEnvoiMessage() {
		ClientSimple client = new ClientSimple();
		Vector<String> listOfMessageToSend = new Vector<String>();
		
		listOfMessageToSend.add("Test");
		listOfMessageToSend.add("");
		listOfMessageToSend.add("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*");
		listOfMessageToSend.add("!@#$%^&*()`~");
		listOfMessageToSend.add("ЁЂЃЄЅІЇЈЉЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя");
		listOfMessageToSend.add("찦차를 타고 온 펲시맨과 쑛다리 똠방각하");

		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			for (String s : listOfMessageToSend) {
				client.sendMessage(s);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertEquals(listOfMessageToSend, connectedClients.get(0).messageRecus);
		} catch (IOException e) {
		}
	}
	
	@Test
	public void testReceiveMessage() {
		ClientSimple client = new ClientSimple();
		Vector<String> messageRecus = new Vector<String>();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.sendMessage("Start Test Receive Sequence");
			String message = null;
			do {
				message = client.receiveMessage();
				messageRecus.add(message);
			} while (!message.equals("Stop Test"));
			assertEquals(messageTestReceiveSequence, messageRecus);
		} catch (IOException e) {
		}
	}
	
	@Test
	public void testReceiveMessageServerDisconnected() {
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.sendMessage("Disconnect Server");
		} catch (IOException e) {
		}
		try {
			client.receiveMessage();
			fail("Le message a bien été reçu du serveur.");
		} catch (IOException e) {
			assertEquals("Impossible de récupérer un message.", e.getMessage());
		}
	}
	
	@Test
	public void testSetNickName() {
		ClientSimple client = new ClientSimple();
		
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.setNickName("Zak");
			String message = client.receiveMessage();
			assertEquals("Nickname:Zak", message);
		} catch (IOException e) {
		}
	}
}
