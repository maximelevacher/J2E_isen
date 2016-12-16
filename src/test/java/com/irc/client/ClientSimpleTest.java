package com.irc.client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import com.irc.controller.Controller;
import com.irc.ihm.GUI;
import com.irc.ihm.LoginWindow;
import com.irc.metier.Message;

public class ClientSimpleTest {
	
	ServerSocket ss = null;
	Vector<ServerSideClient> connectedClients = null;
	Thread thread = null;
	volatile boolean isRunning = true;
	
	static Vector<String> messageTestReceiveSequence = new Vector<String>();
	
	class ServerSideClient implements Runnable {
		Socket _s;
		ObjectOutputStream _out;
		ObjectInputStream _in;
		Vector<String> messageRecus = new Vector<String>();
		
		ServerSideClient(Socket s) {
			_s = s;
			try {
				_in = new ObjectInputStream(_s.getInputStream());
				_out = new ObjectOutputStream(_s.getOutputStream());
			} catch (IOException e) {
			}

		}

		@Override
		public void run() {
			while(true) {
				try {
					String message = (String) _in.readObject();
					if (message == null) {
						throw new IOException("Le client est déconnecté");
					}
					messageRecus.addElement(message);
					if (message.startsWith("%nickname")) {
						String nickname = message.split(" ")[1];
						_out.writeObject("Nickname:" + nickname);
					} else if (message.equals("Start Test Receive Sequence")) {
						for (String s : messageTestReceiveSequence) {
							_out.writeObject(s);
						}
					} else if (message.equals("Disconnect Server")) {
						_s.close();
					}
				} catch (IOException | ClassNotFoundException e) {
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
		}
	}

	@Test
	public void testConnexionReussie() {
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.sendMessage("Test");
		} catch (IOException e) {
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
		try {
			client.disconnectFromServer();
		} catch (IOException e1) {
			fail("Impossible de se deconnecter.");
		}
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
				Thread.sleep(100);
			} catch (InterruptedException e) {
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
			Object objReceived = null;
			do {
				objReceived = client.receiveMessage();
				if (objReceived instanceof String) {
					messageRecus.add((String)objReceived);
				}
			} while (!objReceived.equals("Stop Test"));
			assertEquals(messageTestReceiveSequence, messageRecus);
		} catch (IOException | ClassNotFoundException e) {
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
		} catch (IOException | ClassNotFoundException e) {
			assert(true);
		}
	}
	
	@Test
	public void testSetNickName() {
		ClientSimple client = new ClientSimple();
		
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			client.setNickName("Zak");
			Object objReceived = client.receiveMessage();
			String message = null;
			if (objReceived instanceof String) {
				message = (String) objReceived;
			}
			assertEquals("Nickname:Zak", message);
		} catch (IOException | ClassNotFoundException e) {
		}
	}
	
	@Test
	public void connectAdminTest() throws InterruptedException, IOException {
		ClientSimple AdminTest1 = new ClientSimple();
		GUI viewConnectedTest1 = new GUI();
		LoginWindow loginTest1 = new LoginWindow();
		AdminTest1.connectToServer(InetAddress.getLocalHost(), 55555);
		Controller AdminTestIHM = new Controller (AdminTest1, viewConnectedTest1, loginTest1 );
		
		AdminTestIHM.startClient();
		AdminTestIHM.onClickOnLoginButton("Admin","admin");
		try {
			AdminTest1.sendMessage("Test");
		} catch (IOException e) {
			fail("La connexion n'a pas réussie.");
		}
		
		while(true);
	}
	
	

}


