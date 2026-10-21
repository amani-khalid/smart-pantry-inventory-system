package application;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import models.PantryItem; // <-- from your PantryItem code
import repo.FilePantryRepository; // <-- CSV repository
import service.PantryService;
import service.PantryServiceImpl;
import managers.ShoppingListManager;
import models.ShoppingList;
import models.User;
import repo.PantryRepository;
import managers.UserManager;
import models.PantryCharacter;

public class Dashboard extends JFrame {
	// Color Palette
	Color darkBrown = new Color(80, 45, 20); // deep brown
	Color softPurple = new Color(200, 180, 220); // soft pastel purple
	// Category buttons
	private JButton btnFruitsVegetables;
	private JButton btnCannedDried;
	private JButton btnSnacks;
	private JButton btnBeveragesOthers;
	// Shopping list button
	private JButton btnShoppingList;
	private JButton btnBackToLogin;
	// SHARED DATA
	private List<PantryItem> allItems;
	private ShoppingListManager shoppingListManager;
	private ShoppingList currentShoppingList;
	private String currentUserId;
	private User current;
	private PantryRepository repository;
	private CategoryWindow3 window;
	private UserManager userManager;

	public Dashboard(String userId, List<PantryItem> allItems, ShoppingListManager manager, ShoppingList currentList,
			User current) { // added user as an attribute
		this.currentUserId = userId;
		this.allItems = allItems;
		this.shoppingListManager = manager;
		this.currentShoppingList = currentList;
		this.current = current; // user
		this.repository = new FilePantryRepository("pantryitems.txt"); // CREATE REPOSITORY
		// Basic window settings
		setTitle("Dashboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
		// Use BorderLayout for main frame
		setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(new Color(230, 245, 230));
		// Title: “Dashboard”
		JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Terbuchet MS", Font.BOLD, 28));
		add(titleLabel, BorderLayout.NORTH);
		// your styling
		titleLabel.setFont(new Font("Terbuchet MS", Font.BOLD, 32));
		titleLabel.setForeground(darkBrown);
		// Middle: 4 category buttons in a 2x2 grid
		JPanel categoriesPanel = new JPanel();
		categoriesPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2 rows x 2 cols
		btnFruitsVegetables = new JButton("Fruits & Vegetables");
		btnFruitsVegetables.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		btnFruitsVegetables.setBackground(Color.WHITE);
		btnFruitsVegetables.setForeground(darkBrown);
		btnFruitsVegetables.setBorder(BorderFactory.createLineBorder(darkBrown, 2));
		btnFruitsVegetables.setMargin(new Insets(4, 4, 4, 4));
		btnCannedDried = new JButton("Canned & Dried Goods");
		btnCannedDried.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		btnCannedDried.setBackground(Color.WHITE);
		btnCannedDried.setForeground(darkBrown);
		btnCannedDried.setBorder(BorderFactory.createLineBorder(darkBrown, 2));
		btnCannedDried.setMargin(new Insets(4, 4, 4, 4));
		btnSnacks = new JButton("Snacks");
		btnSnacks.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		btnSnacks.setBackground(Color.WHITE);
		btnSnacks.setForeground(darkBrown);
		btnSnacks.setBorder(BorderFactory.createLineBorder(darkBrown, 2));
		btnSnacks.setMargin(new Insets(4, 4, 4, 4));
		btnBeveragesOthers = new JButton("Beverages & Others");
		btnBeveragesOthers.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		btnBeveragesOthers.setBackground(Color.WHITE);
		btnBeveragesOthers.setForeground(darkBrown);
		btnBeveragesOthers.setBorder(BorderFactory.createLineBorder(darkBrown, 2));
		btnBeveragesOthers.setMargin(new Insets(4, 4, 4, 4));
		categoriesPanel.add(btnFruitsVegetables);
		categoriesPanel.add(btnCannedDried);
		categoriesPanel.add(btnSnacks);
		categoriesPanel.add(btnBeveragesOthers);
		add(categoriesPanel, BorderLayout.CENTER);
		// Bottom: Shopping List button
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.setBackground(new Color(230, 245, 230));
		btnShoppingList = new JButton("Shopping List");
		btnShoppingList.setBackground(softPurple);
		btnShoppingList.setForeground(darkBrown);
		btnShoppingList.setFont(new Font("Terbuchet MS", Font.PLAIN, 16)); // bigger font
		btnShoppingList.setPreferredSize(new Dimension(180, 55)); // bigger button
		bottomPanel.add(btnShoppingList);
		add(bottomPanel, BorderLayout.SOUTH);
		btnBackToLogin = new JButton("Back to Login");
		btnBackToLogin.setBackground(new Color(255, 150, 150)); // Light red color
		btnBackToLogin.setForeground(darkBrown);
		btnBackToLogin.setFont(new Font("Terbuchet MS", Font.PLAIN, 16));
		btnBackToLogin.setPreferredSize(new Dimension(180, 55)); // bigger button
		bottomPanel.add(btnBackToLogin);
		// Hand cursor for all buttons
		Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		btnFruitsVegetables.setCursor(hand);
		btnCannedDried.setCursor(hand);
		btnSnacks.setCursor(hand);
		btnBeveragesOthers.setCursor(hand);
		btnShoppingList.setCursor(hand);
		btnBackToLogin.setCursor(hand);
		// CONNECT BUTTONS TO OTHER WINDOWS
		// UPDATE all category button actions:
		btnFruitsVegetables.addActionListener(
				e -> new CategoryWindow3("Fruits & Vegetables", currentUserId, repository, shoppingListManager)
						.setVisible(true));
		btnCannedDried.addActionListener(
				e -> new CategoryWindow3("Canned & Dried Goods", currentUserId, repository, shoppingListManager)
						.setVisible(true));
		btnSnacks.addActionListener(
				e -> new CategoryWindow3("Snacks", currentUserId, repository, shoppingListManager).setVisible(true));
		btnBeveragesOthers.addActionListener(
				e -> new CategoryWindow3("Beverages & Others", currentUserId, repository, shoppingListManager)
						.setVisible(true));
		// Shopping List button → opens ShoppingListGUI
		btnShoppingList.addActionListener(
				e -> new ShoppingGUI(this.current, shoppingListManager, currentShoppingList, window).setVisible(true));
		btnBackToLogin.addActionListener(e -> {
			this.dispose(); // Close dashboard
			new LoginFrame(this.userManager).setVisible(true); // Replace with your actual login frame class name
		});
	}

	private JButton createGreenRoundedButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 40); // 25 = corner radius
				super.paintComponent(g2);
				g2.dispose();
			}

			@Override
			public void setContentAreaFilled(boolean b) {
				// Disable default background fill
			}
		};

		// Smooth green style
		button.setBackground(new Color(34, 155, 60)); // #229B3C
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Terbuchet MS", Font.PLAIN, 10));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
		return button;
	}

	public JButton getBtnFruitsVegetables() {
		return btnFruitsVegetables;
	}

	public JButton getBtnCannedDried() {
		return btnCannedDried;
	}

	public JButton getBtnSnacks() {
		return btnSnacks;
	}

	public JButton getBtnBeveragesOthers() {
		return btnBeveragesOthers;
	}

	public JButton getBtnShoppingList() {
		return btnShoppingList;
	}

	public JButton getBtnBackToLogin() {
		return btnBackToLogin;
	}
}