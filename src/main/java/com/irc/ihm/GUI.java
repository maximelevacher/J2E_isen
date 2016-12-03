package com.irc.ihm;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.Color;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;

public class GUI extends JFrame implements MouseListener, ChangeListener {
	JTabbedPane messageArea = null;

	public G() {
		setTitle("ChatDent");
		setVisible(true);
		setSize(900, 700);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// on initialise la bar de menu
		menuBar();
		getContentPane().setLayout(new BorderLayout(0, 0));
		// on cré la partie liste des messages et liste des personne connecté
		JSplitPane messageConnected = new JSplitPane();
		messageConnected.setResizeWeight(0.8);
		getContentPane().add(messageConnected, BorderLayout.CENTER);
		// Génère une première tab correspondant à notre partie général
		messageArea = new JTabbedPane(JTabbedPane.TOP);
		messageArea.addChangeListener(this);
		messageConnected.setLeftComponent(messageArea);
		addPanelTab(messageArea, "General");
		// Liste des personnes connecté
		JList<String> list = new JList<String>(updateConnected());
		messageConnected.setRightComponent(list);
		list.addMouseListener(mouseListener);
		// on affiche la partie d'envoi du message
		Panel espaceSendMessage = new Panel();
		getContentPane().add(espaceSendMessage, BorderLayout.SOUTH);
		espaceSendMessage.setLayout(new BorderLayout(0, 0));
		espaceSendMessage.add(sendMessage(), BorderLayout.SOUTH);

	}

	public static void main(String[] args) {
		new G();

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
		JTextArea textArea_1 = new JTextArea();
		panel_2.add(textArea_1);
		textArea_1.setColumns(50);
		textArea_1.setRows(5);
		textArea_1.setBorder(border);

		JButton sendButton = new JButton("Send");
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

	protected JTabbedPane addPanelTab(JTabbedPane messageArea, String nomClient) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.add(addMessageList());
		messageArea.addTab(nomClient, null, panel, null);
		return messageArea;
	}

	protected JTextArea addMessageList() {
		JTextArea textArea = new JTextArea();
		textArea.setText("");
		return textArea;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

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

	MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			JList theList = (JList) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 2) {
				int index = theList.locationToIndex(mouseEvent.getPoint());
				if (index >= 0) {
					Object o = theList.getModel().getElementAt(index);
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

}
