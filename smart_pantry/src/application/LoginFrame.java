package application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

import managers.ShoppingListManager;
import managers.UserManager;
import repo.FilePantryRepository;
import repo.PantryRepository;
import service.PantryService;
import service.PantryServiceImpl;
import models.ShoppingListItem;
import models.User;
import models.PantryItem;
import models.ShoppingList;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Main login window for Smart Pantry.
 */
public class LoginFrame extends JFrame {
	private final UserManager userManager;
	private JTextField emailField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton signUpButton;

	public LoginFrame(UserManager userManager) {

		super("Smart Pantry - Login");
		// Add null check and initialize if needed
		if (userManager == null) {
			System.err.println("UserManager is null! Creating new instance...");
			this.userManager = new UserManager();
		} else {
			this.userManager = userManager;
		}
		initUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 600);
		setLocationRelativeTo(null);
	}

	private void initUI() {
		// Soft light background like the sketch
		getContentPane().setBackground(new Color(230, 245, 230));
		setLayout(new GridBagLayout()); // to center the card panel
		JPanel card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setBorder(new EmptyBorder(40, 60, 40, 60));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		// --- Icon ---
		ImageIcon logoIcon;
		java.net.URL url = getClass().getResource("images/logo_new.jpeg");
		if (url != null) {
			logoIcon = new ImageIcon(url);
		} else {
			// fallback to a file path if needed
			logoIcon = new ImageIcon("images/logo_new.jpeg");
		}
		Image scaled = logoIcon.getImage().getScaledInstance(190, 150, Image.SCALE_SMOOTH);
		logoIcon = new ImageIcon(scaled);
		JLabel logoLabel = new JLabel(logoIcon);
		logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// --- Title ---
		JLabel titleLabel = new JLabel("");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 26f));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setBorder(new EmptyBorder(20, 0, 30, 0));
		// --- Form panel (labels LEFT, fields centered & wide) ---
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		// Email label
		gbc.gridy = 0;
		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(emailLabel, gbc);
		// Email field
		gbc.gridy = 1;
		emailField = new JTextField(20);
		emailField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		emailField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(emailField, gbc);
		// Password label
		gbc.gridy = 2;
		gbc.insets = new Insets(15, 0, 5, 0);
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(passwordLabel, gbc);
		// Password field
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 5, 0);
		passwordField = new JPasswordField(20);
		passwordField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		passwordField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(passwordField, gbc);
		// --- Buttons row ---
		JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
		buttonsPanel.setBackground(Color.WHITE);
		buttonsPanel.setBorder(new EmptyBorder(30, 0, 10, 0));
		// Login button (blue)
		loginButton = new JButton("Login");
		Color primaryGreen = new Color(34, 155, 60);
		loginButton.setBackground(primaryGreen);
		loginButton.setFont(new Font("Terbuchet MS", Font.PLAIN, 14));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFocusPainted(false);
		loginButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		loginButton.setOpaque(true);
		loginButton.setContentAreaFilled(true);
		// Sign up button (white with green border & text)
		Color green = new Color(34, 155, 60);
		signUpButton = new JButton("Sign Up");
		signUpButton.setBackground(Color.WHITE);
		signUpButton.setForeground(green);
		signUpButton.setFocusPainted(false);
		signUpButton.setBorder(new LineBorder(green, 2, true));
		signUpButton.setFont(new Font("Terbuchet MS", Font.PLAIN, 14));
		signUpButton.setOpaque(true);
		signUpButton.setContentAreaFilled(true);
		buttonsPanel.add(loginButton);
		buttonsPanel.add(signUpButton);
		// --- Info text ---
		JLabel infoLabel = new JLabel(
				"<html><div style='text-align:center;'>If you have an account, click <b>Login</b><br>"
						+ "If not, click <b>Sign Up</b> for more information</div></html>",
				SwingConstants.CENTER);
		infoLabel.setForeground(new Color(120, 120, 130));
		infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
		infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		// Add components to card
		card.add(logoLabel);
		card.add(titleLabel);
		card.add(formPanel);
		card.add(buttonsPanel);
		card.add(infoLabel);
		// Center the card
		GridBagConstraints rootGbc = new GridBagConstraints();
		rootGbc.gridx = 0;
		rootGbc.gridy = 0;
		rootGbc.anchor = GridBagConstraints.CENTER;
		add(card, rootGbc);
		// --- Actions ---
		loginButton.addActionListener(e -> doLogin());
		signUpButton.addActionListener(e -> openSignUpDialog());
	}

	private void doLogin() {
		String email = emailField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();

		// 1) Block empty fields (prevents generic console errors)
		if (email.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Both email and password must be filled out.", "Missing Information",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		System.out.println("Attempting login for email: " + email);

		// 2) Try login with the shared UserManager
		boolean ok = userManager.login(email, password);
		if (!ok) {
			JOptionPane.showMessageDialog(this, "Login failed. Check your email or password.", "Login Failed",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		User current = userManager.getCurrentUser();
		System.out.println("Logged in user ID: " + current.getUserId());

		JOptionPane.showMessageDialog(this, "Welcome, " + current.getName() + "!", "Login Successful",
				JOptionPane.INFORMATION_MESSAGE);

		this.dispose();

		PantryRepository repo = new FilePantryRepository("pantryitems.txt");
		PantryService pantry = new PantryServiceImpl(repo, current.getUserId());
		ShoppingListManager shoppinglistmanager = new ShoppingListManager(pantry);
		ShoppingList currentshoppinglist = new ShoppingList(current.getUserId());

		Dashboard dashboard = new Dashboard(current.getUserId(), repo.loadAll(current.getUserId()), shoppinglistmanager,
				currentshoppinglist, current);
		dashboard.setVisible(true);
	}

	private void openSignUpDialog() {
		SignUpDialog dialog = new SignUpDialog(this, userManager);
		dialog.setVisible(true);
	}

	// Main entry point
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			UserManager mgr = new UserManager();
			new LoginFrame(mgr).setVisible(true);
		});
	}
}
