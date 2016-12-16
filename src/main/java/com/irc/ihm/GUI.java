package com.irc.ihm;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.Color;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.Logger;

import com.irc.client.ClientSimple;
import com.irc.controller.Controller;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Cette classe implémente une interface graphique pour un client de chat
 * @author Maxime L.
 *
 */
public class GUI extends JFrame implements MouseListener, ChangeListener, ActionListener {
	/**
	 * Permet de logger des messages suivant le fichier de configuration log4j.properties
	 */
	static final Logger logger = Logger.getLogger(GUI.class);
	static final String logConfigPath = "conf/log4j.properties";
	
	private Controller controller;
	
	private class TabPanelMessageArea {
		JPanel panel;
		JTextArea textAreaMessages;
		TabPanelMessageArea(JPanel p, JTextArea t) {
			panel = p;
			textAreaMessages = t;
		}
	}
	
	Map<String, TabPanelMessageArea> mapTextAreaReceiveMessage = new HashMap<String, TabPanelMessageArea>();
	JTabbedPane messageArea = null;
	JTextArea textAreaSendMessage = null;
	JButton sendButton = null;
	JList<String> listConnected = null;
	JMenuItem menuItemReconnect = null;
	
	public GUI() {
		setTitle("ChatDent");
		setSize(900, 700);
		setResizable(false);
		this.addMouseListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// on initialise la bar de menu
		menuBar();
		getContentPane().setLayout(new BorderLayout(0, 0));
		// on cré la partie liste des messages et liste des personne connecté
		JSplitPane messageConnected = new JSplitPane();
		messageConnected.setResizeWeight(0.5);
		messageConnected.setEnabled(false);
		getContentPane().add(messageConnected, BorderLayout.CENTER);
		// Génère une première tab correspondant à notre partie général
		messageArea = new JTabbedPane(JTabbedPane.TOP);
		messageArea.addChangeListener(this);
		messageConnected.setLeftComponent(messageArea);
		getTabOfUser("_General");
		// Liste des personnes connecté
		listConnected = new JList<String>();
		messageConnected.setRightComponent(listConnected);
		listConnected.addMouseListener(mouseListener);
		// on affiche la partie d'envoi du message
		Panel espaceSendMessage = new Panel();
		getContentPane().add(espaceSendMessage, BorderLayout.SOUTH);
		espaceSendMessage.setLayout(new BorderLayout(0, 0));
		espaceSendMessage.add(sendMessage(), BorderLayout.SOUTH);
		setVisible(false);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		GUI g = new GUI();
		g.setVisible(true);
	}

	public void addListenener(Controller c) {
		controller = c;
	}
	
	public void updateListConnected(Vector<String> v) {
		listConnected.setListData(v);
		/* Inutile ... à voir
		// On prend toutes les tabs et on les désactive, sauf le general (premier index)
		for (int i = 1; i < messageArea.getTabCount(); i ++) {
			setEnableTabOfUser(messageArea.getTitleAt(i), false);
		}
		// Active ceux qui sont connectés
		for (String s : v) {
			setEnableTabOfUser(s, true);
		}
		*/
	}

	protected JPanel sendMessage() {
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JPanel panel_2 = new JPanel();
		textAreaSendMessage = new JTextArea();
		JScrollPane sp = new JScrollPane(textAreaSendMessage);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_2.add(sp);
		textAreaSendMessage.setColumns(50);
		textAreaSendMessage.setRows(5);
		textAreaSendMessage.setBorder(border);
		textAreaSendMessage.setLineWrap(true);
		textAreaSendMessage.setDisabledTextColor(Color.GRAY);
		textAreaSendMessage.addKeyListener(sendEnterButtonListener);

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

		menuItemReconnect = new JMenuItem("Se Reconnecter");
		mnFichier.add(menuItemReconnect);

		return menuBar;
	}

	protected void getTabOfUser(String nomClient) {
		if (mapTextAreaReceiveMessage.containsKey(nomClient)) {
			return;
		}
		JPanel panel = new JPanel();
		JTextArea textArea = addMessageList();
		String nameDisplay = nomClient;
		
		panel.setLayout(new GridLayout());
		JScrollPane sp = new JScrollPane(textArea);
		panel.add(sp);
		
		TabPanelMessageArea tPaneMsgArea = new TabPanelMessageArea(panel, textArea);
		
		if (nomClient.equals("_General")) {
			nameDisplay = "General";
		}
		
		mapTextAreaReceiveMessage.put(nomClient, tPaneMsgArea);
		messageArea.addTab(nameDisplay, null, panel, null);
	}
	
	private void focusTabOfUser(String username) {
		getTabOfUser(username);
		for (int i = 0; i < messageArea.getTabCount(); i++) {
			if (messageArea.getTitleAt(i).equals(username)) {
				messageArea.setSelectedIndex(i);
				break;
			}
		}
	}
	
	public void setEnableTabOfUser(String username, boolean b) {
		for (int i = 0; i < messageArea.getTabCount(); i++) {
			if (messageArea.getTitleAt(i).equals(username)) {
				messageArea.setEnabledAt(i, b);
				break;
			}
		}
	}

	protected JTextArea addMessageList() {
		JTextArea textAreaReceiveMessage = new JTextArea();
		textAreaReceiveMessage.setText("");
		textAreaReceiveMessage.setLineWrap(true);
		textAreaReceiveMessage.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textAreaReceiveMessage.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		return textAreaReceiveMessage;
	}
	
	/**
	 * Active ou désactive les entrées utilisateur si le serveur est déconnecté.
	 * @param b
	 */
	public void enableUserEntries(boolean b) {
		textAreaSendMessage.setEditable(b);
		sendButton.setEnabled(b);
		listConnected.setEnabled(b);
		if (b) {
			textAreaSendMessage.setBackground(Color.WHITE);
		} else {
			textAreaSendMessage.setBackground(Color.LIGHT_GRAY);
		}
		// Si on désactive les entrées, on autorise la possibilté de se reconnecter
		menuItemReconnect.setEnabled(!b);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		logger.info("You clicked the button, using an ActionListener");
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
	
	private void sendMessageAndEmptyTextField() {
		if(textAreaSendMessage.getText().length() != 0) {
			String receiver = messageArea.getTitleAt(messageArea.getSelectedIndex());
			if (receiver.equals("General")) {
				controller.onClickOnSendMessage(textAreaSendMessage.getText());
			} else {
				controller.onClickOnSendMessage(textAreaSendMessage.getText(), receiver);
			}
			textAreaSendMessage.setText("");
			logger.info("Appui ou clic sur envoi. Message à " + receiver + ": " + textAreaSendMessage.getText());
		}
	}

	KeyAdapter sendEnterButtonListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				e.consume();
				sendMessageAndEmptyTextField();
			}
		}
	};

	MouseListener sendButtonListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			sendMessageAndEmptyTextField();
		}
	};
	MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			JList theList = (JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(mouseEvent)) {
				int index = theList.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Object o = theList.getModel().getElementAt(index);
					// System.out.println("Double-clicked on: " + o.toString());
					focusTabOfUser(o.toString());
				}
			}
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				if (controller.isClientAdmin()) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
						theList.setSelectedIndex(index);
						getAdminPopupMenu(o.toString()).show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
						logger.info("L'Admin a clic droit sur un utilisateur.");
					}
				}
			}

		}
	};

	private JPopupMenu getAdminPopupMenu(String username) {
		JPopupMenu menu = new JPopupMenu("Popup Admin");
		JMenuItem itemKick = new JMenuItem("Kick");
		JMenuItem itemBan = new JMenuItem("Ban");
		menu.add(itemKick);
		menu.add(itemBan);
		itemKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.askForKickUser(username);
				logger.info("L'Admin a demandé un kick sur: " + username);
			}
		});
		itemBan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.askForBanUser(username);
				logger.info("L'Admin a demandé un ban sur: " + username);
			}
		});
		return menu;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
		int selectedIndex = tabbedPane.getSelectedIndex();
		//JOptionPane.showMessageDialog(null, "Selected Index: " + selectedIndex);
		

	}

	public void appendMessageToUserTab(String message) {
		JTextArea t = mapTextAreaReceiveMessage.get("_General").textAreaMessages;
		t.setText(t.getText() + message + System.lineSeparator());
	}
	
	public void appendMessageToUserTab(String message, String username) {
		getTabOfUser(username);
		JTextArea t = mapTextAreaReceiveMessage.get(username).textAreaMessages;
		t.setText(t.getText() + message + System.lineSeparator());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
