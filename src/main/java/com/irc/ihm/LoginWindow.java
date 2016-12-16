package com.irc.ihm;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.irc.controller.Controller;

public class LoginWindow extends JFrame {
	private Controller controller;
	private JTextField userText;
	private JPasswordField passwordField;
	JLabel passLabel;
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
		displayPasswordField(false);
	}

	private void placeComponents(JPanel panel) {
		panel.setLayout(null);

		JLabel userLabel = new JLabel("Pseudo");
		userLabel.setBounds(10, 10, 80, 25);
		panel.add(userLabel);

		userText = new JTextField(20);
		userText.setBounds(100, 10, 160, 25);
		addKeyListenerOnUserText();
		panel.add(userText);

		passLabel = new JLabel("Pass");
		passLabel.setBounds(10, 40, 80, 25);
		panel.add(passLabel);

		passwordField = new JPasswordField();
		passwordField.setBounds(100, 40, 160, 25);
		panel.add(passwordField);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(100, 70, 80, 25);
		loginButton.addMouseListener(loginButtonListener);
		panel.add(loginButton);
	}

	private void addKeyListenerOnUserText() {
		userText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(userText.getText().equals("Admin")) {
					displayPasswordField(true);
				} else {
					displayPasswordField(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	public void displayPasswordField(boolean b) {
		passLabel.setVisible(b);
		passwordField.setVisible(b);
	}

	MouseListener loginButtonListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			controller.onClickOnLoginButton(userText.getText(), new String(passwordField.getPassword()));
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
