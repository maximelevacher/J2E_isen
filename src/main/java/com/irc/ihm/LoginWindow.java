package com.irc.ihm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.irc.controller.Controller;

public class LoginWindow extends JFrame {
	private Controller controller;
	private JTextField userText;
	JOptionPane optionPaneConnecting = null;
	JDialog dialogConnecting = null;
	
	public LoginWindow() {
		setTitle("Login ChatDent");
		setSize(300, 125);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		add(panel);
		placeComponents(panel);

		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private void placeComponents(JPanel panel) {
		panel.setLayout(null);

		JLabel userLabel = new JLabel("Pseudonyme");
		userLabel.setBounds(10, 10, 80, 25);
		panel.add(userLabel);

		userText = new JTextField(20);
		userText.setBounds(100, 10, 160, 25);
		panel.add(userText);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(100, 50, 80, 25);
		loginButton.addMouseListener(loginButtonListener);
		panel.add(loginButton);
	}

	MouseListener loginButtonListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			controller.onClickOnLoginButton(userText.getText());
		}
	};

	public void showError(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void showConnectingBox(boolean show) {
		if (optionPaneConnecting == null) {
			optionPaneConnecting = new JOptionPane("Connexion au serveur...", JOptionPane.INFORMATION_MESSAGE);
			dialogConnecting = optionPaneConnecting.createDialog(this, "Connexion...");
			dialogConnecting.setModal(false);
		}
		optionPaneConnecting.setVisible(show);
		dialogConnecting.setVisible(show);
	}

	public void addListenener(Controller c) {
		controller = c;
	}

	public static void main(String[] args) {
		LoginWindow login = new LoginWindow();
	}
}
