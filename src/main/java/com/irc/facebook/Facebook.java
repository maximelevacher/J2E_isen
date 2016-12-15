package com.irc.facebook;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class Facebook implements MouseListener {
	private static final String MY_APP_SECRET = "1000dc0449cfcaa1224efff069a6d759";
	private static final String MY_ACCESS_TOKEN = "34902821436e324fa9031c9e54fa2ac4";
	private static final String MY_APP_ID = "1177222612325888";
	static JFrame mainFrame = null;
	static JButton test = null;
	static JLabel messageLabel = null;

	public static void main(String[] args) {
		//initFrame();

	}
/*
	public static void initFrame() {
		mainFrame = new JFrame("Authentification");
		mainFrame.setSize(800, 600);
		JPanel mainPanel = new JPanel();
		mainFrame.setContentPane(mainPanel);
		test = new JButton("Authentification");
		test.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		    	//Déclaration des variable de Stockage
				String nickName = null;
				String accessTocken;
		    	// URL Permettant d'acceder à l'api graph de facebook
		    	String URL = "https://www.facebook.com/v2.8/dialog/oauth?type=user_agent&client_id=1177222612325888%20&redirect_uri=http://maximelevacher.fr";
		    	// défini que le driver a utiliser est Chrome
		    	System.setProperty("webdirver.chrome.driver", "chromedriver.exe");
		    	// Déclaer et initialise le WebDriver
				WebDriver driver = new ChromeDriver();
				//récupère l'url
				driver.get(URL);
				// on exécute l'ouverture de la fenêtre
				while (true) {
					if (!driver.getCurrentUrl().contains("facebook.com")) {
						// on récupère l'URI
						String uri = driver.getCurrentUrl();
						// on récupère le token d'accès au donnée de l'utilisateur
						accessTocken = uri.replaceAll(".*#access_token=(.+)&.*", "$1");
						// on créer une variable de type Client facebook afin de récupérer les informations
						FacebookClient fbClient = new DefaultFacebookClient(accessTocken);
						// on créer un nouvelle User
						User client = fbClient.fetchObject("me", User.class);	
						// on récupère le nickname
						nickName = client.getName();
						// on l'affiche
						System.out.println(nickName);
						// on ferme le driver
						driver.quit();
					}

				}
		    }
		});
		mainPanel.add(test);
		messageLabel = new JLabel();
		mainPanel.add(messageLabel);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
*/

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
