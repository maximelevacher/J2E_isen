package com.irc.database;

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


public class DatabaseTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
	}
	
	/**
	 * Remplissage de la BDD
	 */
	public void testEntrerNmsg(int n) {
		
		MessageDAO msgDAO = new MessageDAO();
		
		/*try {
			for(int i=0;i<n;i++){	
				//msgDAO.create();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} catch (IOException e) {
			
		}*/
	}

}
