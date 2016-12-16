package com.irc.server;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.irc.client.ClientSimple;
@Ignore
public class ServerSimpleIT {

	static Thread threadServerSimple = null;
	/**
	 * Lance le serveur avant de commencer les tests
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		threadServerSimple = new Thread() {
			public void run() {
				ServerSimple.main(new String[]{});
			}
		};

		threadServerSimple.start();
	}

	/**
	 * Arrête le serveur après la fin de tous les tests
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		threadServerSimple.interrupt();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1Client() throws InterruptedException, ExecutionException {
		testNbClients(1, 1);
	}
	
	@Test
	public void test2Clients() throws InterruptedException, ExecutionException {
		testNbClients(2, 1);
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
				ClientSimple client = new ClientSimple();
				client.connectToServer(InetAddress.getLocalHost(), ClientSimple.DEFAULT_PORT);
				try {
					client.receiveMessage();
					client.sendMessage("test");
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

}
