package com.irc.ihm;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.irc.controller.Controller;
import com.irc.database.MessageDAO;
import com.irc.database.PersonneDAO;
import com.irc.metier.Message;
import com.irc.metier.Personne;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;

/**
 * Cette classe implémente une interface graphique pour un client de chat
 * @author Maxime L.
 *
 */
public class GUI extends JFrame implements MouseListener, ChangeListener, ActionListener {
	private Controller controller;
	// DAO personne
	PersonneDAO personneDAO = new PersonneDAO();
	// Information de la personne
	Personne personne = null;
	// Les TabbedPane
	JTabbedPane messageArea = null;
	JTabbedPane mainTabbedPane = null;
	// Liste des connecté
	JList listConnected = null;
	// Les TextField
	JTextField inputNickname= null;
	// Les TextArea
	JTextArea textAreaReceiveMessage = null;
	JTextArea textAreaSendMessage= null;
	// Les SplitPane
	JSplitPane messageConnected = null;
	//Les JPanel
	JPanel authJpanel = null;
	JPanel espaceSendMessage= null;
	// Les Buttons
	JButton sendButton = null;
	JButton buttonWithoutAuth=null;
	JButton buttonFaceBook=null;
	JButton buttonAdmin=null;
	JButton seConnecter = null;
	public GUI() {
		setTitle("ChatDent");
		setSize(900, 700);
		this.addMouseListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// on initialise la bar de menu
		menuBar();
		getContentPane().setLayout(new BorderLayout(0, 0));
		// on cré la partie liste des messages et liste des personne connecté
		messageConnected = new JSplitPane();
		messageConnected.setResizeWeight(0.99);
		getContentPane().add(messageConnected, BorderLayout.CENTER);
		// on génère la partie demandant l'authentification
		messageConnected.setLeftComponent(choixAuthentification());
		authentificationPanel(messageArea,"Authentification");
		authJpanel =new JPanel();
		messageConnected.setRightComponent(authJpanel);
		// Génère une première tab correspondant à notre partie général
		//messageConnected.setLeftComponent(addMessageGeneral());
		//addPanelTab(messageArea, "General");
		// Liste des personnes connecté
	//	JList<String> list = new JList<String>(updateConnected());
		//messageConnected.setRightComponent(list);
	//	list.addMouseListener(mouseListener);
		// on affiche la partie d'envoi du message
		/*Panel espaceSendMessage = new Panel();
		getContentPane().add(espaceSendMessage, BorderLayout.SOUTH);
		espaceSendMessage.setLayout(new BorderLayout(0, 0));
		espaceSendMessage.add(sendMessage(), BorderLayout.SOUTH);*/
		setVisible(true);
	}

	public static void main(String[] args) {
		new GUI();
	}

	public void addListenener(Controller c) {
		controller = c;
	}

	protected DefaultListModel<String> updateConnected() {
		DefaultListModel<String> listModel;
		listModel = new DefaultListModel<String>();
		listModel.addElement("Maxime Esperandieu");
		listModel.addElement("Zakaria Yahi");
		listModel.addElement("Brandon Matheon");
		listModel.addElement("Levacher Maxime");
		return listModel;
	}

	protected JPanel sendMessage() {
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel panel_2 = new JPanel();
		textAreaSendMessage = new JTextArea();
		panel_2.add(textAreaSendMessage);
		textAreaSendMessage.setColumns(50);
		textAreaSendMessage.setRows(5);
		textAreaSendMessage.setBorder(border);

		sendButton = new JButton("Send");
		sendButton.setName("send");
		sendButton.addMouseListener(sendButtonListener);
		panel_2.add(sendButton);
		sendButton.setVerticalAlignment(SwingConstants.TOP);
		return panel_2;
	}

	protected JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFichier = new JMenu("Fichier");
		menuBar.add(mnFichier);

		JMenuBar menuBar_1 = new JMenuBar();
		mnFichier.add(menuBar_1);

		JMenu mnTest = new JMenu("Quit");
		menuBar_1.add(mnTest);
		return menuBar;
	}
	
	public JTabbedPane choixAuthentification(){
		messageArea = new JTabbedPane(JTabbedPane.TOP);
		messageArea.addChangeListener(this);
		return messageArea;
		
	}
	public JTabbedPane addMessageGeneral(){
		messageArea = new JTabbedPane(JTabbedPane.TOP);
		messageArea.addChangeListener(this);
		return messageArea;
	}

	protected JTabbedPane authentificationPanel(JTabbedPane messageArea, String nomClient) {
		messageArea.addTab(nomClient, null, selectAutentification(), null);
		return messageArea;
	}
	
	protected JTabbedPane addPanelTab(JTabbedPane messageArea, String nomClient) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.add(addMessageList());
		messageArea.addTab(nomClient, null, panel, null);
		return messageArea;
	}
	/*
	 * JPanel qui nous permet de faire afficher les possibilité d'authentification
	 */
	protected JPanel selectAutentification() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		buttonAdmin = new JButton("Connexion Admin");
		buttonAdmin.addMouseListener(mouseAuth);
		buttonFaceBook = new JButton("Connexion Facebook");
		buttonFaceBook.addMouseListener(mouseFacebook);
		buttonWithoutAuth = new JButton("Général");
		buttonWithoutAuth.addMouseListener(mouseWithoutAuth);
		/*panel.add(buttonAdmin,BorderLayout.CENTER);
		panel.add(buttonFaceBook,BorderLayout.CENTER);
		panel.add(buttonWithoutAuth, BorderLayout.CENTER);*/
		 GridBagConstraints c = new GridBagConstraints();
		    c.gridx = 0;
		    c.gridy = 6;

		    panel.add(buttonAdmin, c);

		    c = new GridBagConstraints();
		    c.gridx = 0;
		    c.gridy = 3;
		    panel.add(buttonFaceBook, c);

		    c = new GridBagConstraints();
		    c.gridx = 0;
		    c.gridy = 0;
		    panel.add(buttonWithoutAuth, c);

		return panel;
	}
	/*
	 * TextArea conteant les différents messages
	 */
	protected JTextArea addMessageList() {
		textAreaReceiveMessage = new JTextArea();
		textAreaReceiveMessage.setText("");
		return textAreaReceiveMessage;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		 System.out.println("You clicked the button, using an ActionListener");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		//sysou

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
	MouseListener sendButtonListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			if(textAreaSendMessage.getText().length()!=0){
				controller.onClickOnSendMessage(textAreaSendMessage.getText());
				textAreaSendMessage.setText("");
			}else{
				
			}
		}
	};
	MouseListener mouseWithoutAuth = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			authJpanel = new JPanel();
			JLabel text= new JLabel("NickName");
			 inputNickname = new JTextField();
			 inputNickname.setName("nickname");
			seConnecter = new JButton("Se connecter");
			seConnecter.setName("seconnecter");
			seConnecter.addMouseListener(mouseConnect);
			inputNickname.setPreferredSize(new Dimension(200, 30));
			authJpanel.add(text);
			authJpanel.add(inputNickname);
			authJpanel.add(seConnecter);
			messageConnected.setRightComponent(authJpanel);
			
		}
	};
	MouseListener mouseAuth = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			if(textAreaSendMessage.getText().length()!=0){
				controller.onClickOnSendMessage(textAreaSendMessage.getText());
				textAreaSendMessage.setText("");
			}else{
				
			}
		}
	};
	/*
	 * Mouse Listner du bouton se connecter
	 * Lors du click sur le bouton se connecter de la partie authentification
	 * on a vérifier si le nickname existe
	 * si le nickname existe alors on le récupère
	 * Sinon on l'ajoute
	 * 
	 * Une fois l'utilisateur récupéré on fait afficher l'espace permettant d'interragir entre les utulisateurs
	 */
	MouseListener mouseConnect = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			// on récupère le nickname
			if(inputNickname.getText().length()!=0){
				try {
					personne = personneDAO.findByNickname(inputNickname.getText());
					if(personne.getNickname()!=null){
						personne = personneDAO.findByNickname(inputNickname.getText());
					}else{
						personne.setNickname(inputNickname.getText());
						personneDAO.create(personne);
					}
					// on ouvre le Text area 
					messageConnected.setLeftComponent(addMessageGeneral());
					addPanelTab(messageArea, "General");
					// on ouvre la liste des connecté
					JList<String> list = new JList<String>(updateConnected());
					messageConnected.setRightComponent(list);
					list.addMouseListener(mouseListener);
					// on affiche la partie d'envoi du message
					espaceSendMessage = new JPanel();
					getContentPane().add(espaceSendMessage, BorderLayout.SOUTH);
					espaceSendMessage.setLayout(new BorderLayout(0, 0));
					espaceSendMessage.add(sendMessage(), BorderLayout.SOUTH);
					setVisible(true);
					} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	MouseListener mouseFacebook = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			if(textAreaSendMessage.getText().length()!=0){
				System.out.println(textAreaSendMessage.getText());
				controller.onClickOnSendMessage(textAreaSendMessage.getText());
				textAreaSendMessage.setText("");
			}else{
				
			}
		}
	};
	MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			listConnected = (JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 2) {
				int index = listConnected.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Object o = listConnected.getModel().getElementAt(index);
					// System.out.println("Double-clicked on: " + o.toString());
					addPanelTab(messageArea, o.toString());
				}
			}
		}
	};

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
		int selectedIndex = tabbedPane.getSelectedIndex();
		//JOptionPane.showMessageDialog(null, "Selected Index: " + selectedIndex);
		

	}

	public void appendMessageToArea(String message) {
		 textAreaReceiveMessage.setText(textAreaReceiveMessage.getText() + message + System.lineSeparator());
		MessageDAO messageDAO = new MessageDAO();
		Message message1 = new Message();
		messageDAO.create(message1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
