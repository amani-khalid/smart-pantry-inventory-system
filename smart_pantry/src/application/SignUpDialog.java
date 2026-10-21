package application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import managers.UserManager;

/**
 * Dialog for creating a new account.
 */
public class SignUpDialog extends JDialog {
	private final UserManager userManager;
	private JTextField nameField;
	private JTextField emailField;
	private JTextField phoneField;
	private JPasswordField passwordField;

	public SignUpDialog(JFrame owner, UserManager userManager) {
		super(owner, "Sign Up", true);
		this.userManager = userManager;
		initUI();
		setSize(500, 650);
		setLocationRelativeTo(owner);
	}

	private void initUI() {
		getContentPane().setBackground(new Color(230, 245, 230));
		setLayout(new GridBagLayout());
		JPanel card = new JPanel();
		card.setBackground(Color.WHITE);
		card.setBorder(new EmptyBorder(40, 60, 40, 60));
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		// Icon

		ImageIcon logoIcon;
		java.net.URL url = getClass().getResource("images/logo_new.jpeg");
		if (url != null) {
			logoIcon = new ImageIcon(url);
		} else {
			// fallback to a file path if needed
			logoIcon = new ImageIcon("images/logo_new.jpeg");
		}
		Image scaled = logoIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
		logoIcon = new ImageIcon(scaled);
		JLabel logoLabel = new JLabel(logoIcon);
		logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Title
		JLabel titleLabel = new JLabel("Create Account");
		titleLabel.setFont(new Font("Terbuchet MS", Font.PLAIN, 18));

		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setBorder(new EmptyBorder(20, 0, 25, 0));
		// Form
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		// Name
		gbc.gridy = 0;
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(nameLabel, gbc);
		gbc.gridy = 1;
		nameField = new JTextField(20);
		nameField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		nameField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(nameField, gbc);
		// Email
		gbc.gridy = 2;
		gbc.insets = new Insets(15, 0, 5, 0);
		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(emailLabel, gbc);
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 5, 0);
		emailField = new JTextField(20);
		emailField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		emailField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(emailField, gbc);
		// Phone
		gbc.gridy = 4;
		gbc.insets = new Insets(15, 0, 5, 0);
		JLabel phoneLabel = new JLabel("Phone (e.g. 05********):");
		phoneLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(phoneLabel, gbc);
		gbc.gridy = 5;
		gbc.insets = new Insets(5, 0, 5, 0);
		phoneField = new JTextField(20);
		phoneField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		phoneField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(phoneField, gbc);
		// Password
		gbc.gridy = 6;
		gbc.insets = new Insets(15, 0, 5, 0);
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
		formPanel.add(passwordLabel, gbc);
		gbc.gridy = 7;
		gbc.insets = new Insets(5, 0, 10, 0);
		passwordField = new JPasswordField(20);
		passwordField.setBorder(BorderFactory.createLineBorder(new Color(220, 223, 230)));
		passwordField.setPreferredSize(new Dimension(260, 36));
		formPanel.add(passwordField, gbc);
		// --- Buttons ---
		JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
		buttonsPanel.setBackground(Color.WHITE);
		buttonsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
		// Sign Up (green filled)
		JButton signUpSubmit = new JButton("Sign Up");
		Color green = new Color(0, 153, 0);
		signUpSubmit.setBackground(green);
		signUpSubmit.setForeground(Color.WHITE);
		signUpSubmit.setFocusPainted(false);
		signUpSubmit.setBorder(new EmptyBorder(10, 10, 10, 10));
		signUpSubmit.setOpaque(true);
		signUpSubmit.setContentAreaFilled(true);
		// Back button (white)
		JButton backButton = new JButton("Back to Login");
		backButton.setBackground(Color.WHITE);
		backButton.setForeground(Color.BLACK);
		backButton.setBorder(new LineBorder(new Color(200, 200, 200), 1));
		backButton.setFocusPainted(false);
		backButton.setOpaque(true);
		backButton.setContentAreaFilled(true);
		buttonsPanel.add(signUpSubmit);
		buttonsPanel.add(backButton);
		// Add to card
		card.add(logoLabel);
		card.add(titleLabel);
		card.add(formPanel);
		card.add(buttonsPanel);
		// Center card
		GridBagConstraints rootGbc = new GridBagConstraints();
		rootGbc.gridx = 0;
		rootGbc.gridy = 0;
		add(card, rootGbc);
		// Actions
		signUpSubmit.addActionListener(e -> doSignUp());
		backButton.addActionListener(e -> dispose());
	}

	// import javax.swing.JOptionPane;

	private void doSignUp() {
		String name = nameField.getText().trim();
		String email = emailField.getText().trim();
		String phone = phoneField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();

		// 1) Empty-field guard
		if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields are required (Name, Email, Phone, Password).",
					"Missing Information", JOptionPane.WARNING_MESSAGE);
			// put focus on the first empty field (nice UX)
			if (name.isEmpty())
				nameField.requestFocusInWindow();
			else if (email.isEmpty())
				emailField.requestFocusInWindow();
			else if (phone.isEmpty())
				phoneField.requestFocusInWindow();
			else
				passwordField.requestFocusInWindow();
			return;
		}

		// 2) (Optional) quick client-side checks so you get nice popups instead of
		// console errors
		if (!email.contains("@") || !email.contains(".")) {
			JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email",
					JOptionPane.WARNING_MESSAGE);
			emailField.requestFocusInWindow();
			return;
		}
		if (!phone.matches("\\d{10,15}")) {
			JOptionPane.showMessageDialog(this, "Phone must be 10–15 digits.", "Invalid Phone",
					JOptionPane.WARNING_MESSAGE);
			phoneField.requestFocusInWindow();
			return;
		}
		if (password.length() < 6) {
			JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Weak Password",
					JOptionPane.WARNING_MESSAGE);
			passwordField.requestFocusInWindow();
			return;
		}

		// 3) Normal registration
		boolean ok = userManager.registerUser(name, email, phone, password);
		if (!ok) {
			JOptionPane.showMessageDialog(this, "Email already registered. Please use another.", "Sign Up Failed",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JOptionPane.showMessageDialog(this, "Signup successful! You can now log in.", "Success",
				JOptionPane.INFORMATION_MESSAGE);
		dispose(); // close dialog and return to login
	}
}
