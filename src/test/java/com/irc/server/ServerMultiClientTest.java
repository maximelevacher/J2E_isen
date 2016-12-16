package com.irc.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.irc.client.ClientSimple;
import com.irc.ihm.GUI;
import com.irc.ihm.LoginWindow;
import com.irc.controller.Controller;
import com.irc.database.MessageDAO;
import com.irc.metier.Message;

public class ServerMultiClientTest {

	@Test
	public void testStartAndShutdownServer() {
		// On lance le serveur et un service de recupération des exceptions
		final ServerMultiClient server = new ServerMultiClient();
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<?> future = es.submit(new Runnable() {
			public void run() {
				try {
					server.startServer();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		// Si l'exception récupérée est celle du serveur, le test a échoué
		try {
			future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			fail("Le serveur ne s'est pas lancé");
		// Au bout d'une seconde, on teste si le serveur s'est lancé
		} catch (TimeoutException e) {
			assertTrue(server.isRunning());
		}
	}

	@Test
	public void testStartServerFail() {
		// On lance un serveur qui fonctionne dans un thread
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);

		// Puis on lance le serveur qui va rater son lancement
		ServerMultiClient serverFail = new ServerMultiClient();
		try {
			serverFail.startServer();
			// Si le serveur démarre, le test échoue
			fail("Le serveur a démarré");
		} catch (Exception e) {
			// On verifie qu'il a bien raté son lancement
			assert(true);
		} finally {
			// On arrete le serveur qui fonctionne
			try {
				server.shutdownServer();
			} catch (IOException e) {
			}
		}
	}

	@Test
	public void testConnect1Client() {
		// On lance le serveur
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);

		try {
			// On lance 1 client qui doit réussir à se connecter
			testNbClients(1,1);
		} catch (InterruptedException | ExecutionException e) {
		} finally {
			try {
				server.shutdownServer();
			} catch (IOException e) {
			}
		}
	}

	@Test
	public void testConnectConnectionsLimitClients() {
		// On lance le serveur
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);

		try {
			testNbClients(server.CONNECTIONS_LIMIT, server.CONNECTIONS_LIMIT);
		} catch (InterruptedException | ExecutionException e) {
		} finally {
			try {
				server.shutdownServer();
			} catch (IOException e) {
			}
		}
	}

	@Test
	public void test1OClientsSend500msg(){
		
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);
		int j;
		for (j=0;j<10;j++){
			try {
				// On envoie 500 messages
				testSendNMessage(500);
			} catch (Exception e) {
			} finally {
				try {
					server.shutdownServer();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Test
	public void testConnectOverConnectionsLimit() {
		// On lance le serveur
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);

		try {
			// On lance 2 fois plus de clients que la limite
			testNbClients(server.CONNECTIONS_LIMIT * 2, server.CONNECTIONS_LIMIT);
			assertEquals(server.CONNECTIONS_LIMIT, server.getNumberOfConnectedClients());
		} catch (InterruptedException | ExecutionException e) {
		} finally {
			try {
				server.shutdownServer();
			} catch (IOException e) {
			}
		}
	}
	
	@Test
	public void testenvoie500msg() {
		// On lance le serveur
		final ServerMultiClient server = new ServerMultiClient();
		startServer(server);

		try {
			// On envoie 500 messages
			testSendNMessage(500);
		} catch (Exception e) {
		} finally {
			try {
				server.shutdownServer();
			} catch (IOException e) {
			}
		}
	}


	/**
	 * Lance un serveur déjà instancié et attends qu'il soit bien lancé.
	 * @param server Le serveur à lancer et déjà instancié
	 */
	private void startServer(final ServerMultiClient server) {
		Thread t = new Thread() {
			public void run() {
				server.startServer();
			}
		};
		t.start();
		while(!server.isRunning());
	}


	/**
	 * Permet de lancer plusieurs clients simultanément
	 * et de tester le nombre de clients ayant réussi à se connecter
	 * @param nbClients				le nombre de clients à lancer
	 * @param nbValidConnections	le nombre de clients qui doivent avoir une connexion valide
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void testNbClients(final int nbClients, final int nbValidConnections) throws InterruptedException, ExecutionException {
		if(nbClients < nbValidConnections) {
			fail("Le nombre de connexions valides doit être inférieur au nombre de clients à connecter.");
		}
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				// Lance le client et renvoie son status de connexion (0 reussie, -1 refusée)
				Socket client = new Socket();
				client.connect(new InetSocketAddress(InetAddress.getLocalHost(), ServerMultiClient.DEFAULT_PORT));
				try {
					ObjectInputStream in = new ObjectInputStream(client.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

					// On envoie un message
					out.writeObject("%ping");
					// Et on tente de lire une réponse du serveur.
					// Si elle renvoie null, le serveur a fermé la connexion
					Thread.sleep(1500);
					if(in.readObject() == null) {
						throw new Exception("Client n'a pas pu se connecter");
					}
				} catch (Exception e) {
					return -1;
				}
				return 0;
			}
		};
		// Crée un nombre (nbClients) de threads et récupère leur sortie depuis la fonction call
		List<Callable<Integer>> tasks = Collections.nCopies(nbClients, task);
		ExecutorService executorService = Executors.newFixedThreadPool(nbClients);
		List<Future<Integer>> futures = executorService.invokeAll(tasks);
		List<Integer> resultList = new ArrayList<Integer>(futures.size());
		for (Future<Integer> future : futures) {
			resultList.add(future.get());
		}
		// On vérifie que le nombre de clients et bien égal au nombre de réponses
		assertEquals(nbClients, futures.size());
		// Crée la liste des résultats espérés
		List<Integer> expectedList = new ArrayList<Integer>(nbClients);
		// Ajoute le nombre de connexions valides
		for (int i = 0; i < nbValidConnections; i++) {
			expectedList.add(0);
		}
		// Ajoute le nombre de connexions invalides, soit nbClients - nbValidConnections
		for (int i = nbValidConnections; i < nbClients; i++) {
			expectedList.add(-1);
		}
		Collections.sort(resultList);
		Collections.sort(expectedList);
		// On compare les deux listes
		assertEquals(expectedList, resultList);
	}
	
	
	/**
	 * Envoie des n messages
	 */
	private void testSendNMessage(int n) {
		
		int i;
		ClientSimple client = new ClientSimple();
		try {
			client.connectToServer(InetAddress.getLocalHost(), 55555);
			for(i=0;i<n;i++){
					
				client.sendMessage("Bonjour" + i);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} catch (IOException e) {
		}
	}
	

	public void sendMessageTest() throws InterruptedException, IOException {
		ClientSimple clientTest1 = new ClientSimple();
		GUI viewConnectedTest1 = new GUI();
		LoginWindow loginTest1 = new LoginWindow();
		
		Controller clientTestIHM = new Controller (clientTest1, viewConnectedTest1, loginTest1 );
		
		clientTestIHM.startClient();
		clientTestIHM.onClickOnLoginButton("Testeur1");
		Message msg = new Message();
		
		for(int i=0; i<500; i++)
		{
			clientTest1.sendMessage("Salut" + i);
			Thread.sleep(250);
		}
		while(true);
	}
	
	

}
