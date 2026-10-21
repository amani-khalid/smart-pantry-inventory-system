package application;

import models.PantryItem;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeParseException;
import repo.PantryRepository;
import interfaces.PantryObserver;
import managers.ShoppingListManager;

public class CategoryWindow3 extends JFrame implements PantryObserver {
	private String category;
	private DefaultTableModel tableModel;
	private JTable itemsTable;
	private JLabel countLabel;
	private List<PantryItem> allItems;
	private List<PantryItem> categoryItems;
	private TableRowSorter<DefaultTableModel> rowSorter;
	private JTextField searchField;
	private JButton clearSearchButton;
	private PantryRepository repository;
	private String userId;
	private ShoppingListManager manager;

	public CategoryWindow3(String category, String userId, PantryRepository repository, ShoppingListManager manager) {
		this.category = category;
		this.userId = userId;
		this.repository = repository; // STORE THE REPOSITORY
		this.categoryItems = loadCategoryItems(); // LOAD FROM REPOSITORY
		manager.addObserver(this);
		initializeUI();
	}

	public List<PantryItem> loadCategoryItems() {
		List<PantryItem> allItems = repository.loadAll(userId);
		return filterItemsByCategory(category, allItems);
	}

	private List<PantryItem> filterItemsByCategory(String category, List<PantryItem> items) {
		List<PantryItem> filtered = new ArrayList<>();
		for (PantryItem item : items) {
			if (item.getCategory().equalsIgnoreCase(category)) {
				filtered.add(item);
			}
		}
		return filtered;
	}

	private void initializeUI() {
		setTitle("Smart Pantry - " + category);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		setLocationRelativeTo(null);

		createMainPanel();
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
		button.setBackground(new Color(34, 155, 60));
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Terbuchet MS", Font.PLAIN, 10));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
		return button;
	}

	private void createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		mainPanel.setBackground(new Color(230, 245, 230));

		// Create header first
		JPanel headerPanel = createHeaderPanel();
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// Create table panel (this initializes itemsTable)
		JPanel tablePanel = createTablePanel();
		mainPanel.add(tablePanel, BorderLayout.CENTER);

		// Now update count with actual data
		updateItemCount();

		mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

		add(mainPanel);
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
		headerPanel.setBackground(new Color(230, 245, 230));

		// Left side: Title and count
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(new Color(230, 245, 230));

		JLabel titleLabel = new JLabel(category.toUpperCase() + " ITEMS");
		titleLabel.setFont(new Font("Terbuchet MS", Font.BOLD, 28));
		titleLabel.setForeground(new Color(80, 45, 20));

		countLabel = new JLabel("Loading...");
		countLabel.setFont(new Font("Terbuchet MS", Font.PLAIN, 14));
		countLabel.setForeground(new Color(80, 45, 20));

		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(countLabel, BorderLayout.SOUTH);

		// Right side: Search bar
		JPanel searchPanel = createSearchPanel();

		headerPanel.add(titlePanel, BorderLayout.CENTER);
		headerPanel.add(searchPanel, BorderLayout.EAST);

		return headerPanel;
	}

	private JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		searchPanel.setBackground(new Color(230, 245, 230));

		// Search icon
		JLabel searchIcon = new JLabel("🔍");
		searchIcon.setFont(new Font("Terbuchet MS", Font.PLAIN, 16));

		// Search field
		searchField = new JTextField(15);
		searchField.setPreferredSize(new Dimension(150, 30));
		searchField.setFont(new Font("Terbuchet MS", Font.PLAIN, 13));
		searchField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 245, 230)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		searchField.setToolTipText("Search items by name...");

		// Clear search button (initially hidden)
		clearSearchButton = new JButton("✕");
		clearSearchButton.setPreferredSize(new Dimension(30, 30));
		clearSearchButton.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		clearSearchButton.setBackground(new Color(230, 245, 230));
		clearSearchButton.setForeground(Color.WHITE);
		clearSearchButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		clearSearchButton.setFocusPainted(false);
		clearSearchButton.setVisible(false);

		// Add real-time search listener
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				performSearch();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				performSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				performSearch();
			}

			private void performSearch() {
				String searchText = searchField.getText().trim();
				clearSearchButton.setVisible(!searchText.isEmpty());

				if (searchText.isEmpty()) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(createSmartFilter(searchText));
				}
				updateItemCount();
			}
		});

		// Clear search button action
		clearSearchButton.addActionListener(e -> {
			searchField.setText("");
			searchField.requestFocus();
		});

		searchPanel.add(searchIcon);
		searchPanel.add(searchField);
		searchPanel.add(clearSearchButton);

		return searchPanel;
	}

	// Smart search filter
	private RowFilter<DefaultTableModel, Integer> createSmartFilter(String searchText) {
		return new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
				String itemName = (String) entry.getValue(1); // Column 1 = Name
				String searchLower = searchText.toLowerCase();
				String itemNameLower = itemName.toLowerCase();

				// Strategy 1: Exact match
				if (itemNameLower.equals(searchLower)) {
					return true;
				}

				// Strategy 2: Contains match
				if (itemNameLower.contains(searchLower)) {
					return true;
				}

				// Strategy 3: Multi-word search
				if (multiWordMatch(itemNameLower, searchLower)) {
					return true;
				}

				return false;
			}
		};
	}

	// Multi-word search
	private boolean multiWordMatch(String itemName, String search) {
		String[] searchWords = search.split("\\s+");
		for (String word : searchWords) {
			if (word.length() > 2 && itemName.contains(word)) {
				return true;
			}
		}
		return false;
	}

	private JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 245, 230)),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		tablePanel.setBackground(Color.WHITE);

		String[] columns = { "Item ID", "Name", "Unit", "Quantity", "Threshold", "Expiry Date" };

		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 3: // Quantity
				case 4: // Threshold
					return Double.class;
				case 5: // Expiry Date
					return LocalDate.class;
				default:
					return String.class;
				}
			}
		};

		// Create the table first
		itemsTable = new JTable(tableModel);
		itemsTable.setRowHeight(35);
		itemsTable.setFont(new Font("Terbuchet MS", Font.PLAIN, 13));
		itemsTable.getTableHeader().setFont(new Font("Terbuchet MS", Font.BOLD, 13));
		itemsTable.setSelectionBackground(new Color(230, 245, 230));

		// Setup row sorter for search functionality
		rowSorter = new TableRowSorter<>(tableModel);
		itemsTable.setRowSorter(rowSorter);

		TableColumnModel columnModel = itemsTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(80);
		columnModel.getColumn(1).setPreferredWidth(120);
		columnModel.getColumn(2).setPreferredWidth(50);
		columnModel.getColumn(3).setPreferredWidth(70);
		columnModel.getColumn(4).setPreferredWidth(80);
		columnModel.getColumn(5).setPreferredWidth(100);

		JScrollPane scrollPane = new JScrollPane(itemsTable);
		tablePanel.add(scrollPane, BorderLayout.CENTER);

		populateTableWithData();

		return tablePanel;
	}

	private void populateTableWithData() {
		tableModel.setRowCount(0); // Clear any existing data
		categoryItems = loadCategoryItems(); // RELOAD FROM FILE

		for (PantryItem item : categoryItems) {
			tableModel.addRow(new Object[] { item.getitemID(), item.getName(), item.getUnit(), item.getQuantity(),
					item.getThreshold(), item.getExpiryDate() });
		}
		updateItemCount();
	}

	private void updateItemCount() {
		if (countLabel != null && tableModel != null && itemsTable != null) {
			int visibleRows = (searchField != null && !searchField.getText().isEmpty()) ? itemsTable.getRowCount()
					: tableModel.getRowCount();
			countLabel.setText(visibleRows + " items"
					+ (searchField != null && !searchField.getText().isEmpty() ? " (filtered)" : ""));
		}
	}

	private void showAddItemDialog() {
		JTextField nameField = new JTextField(15);
		JTextField quantityField = new JTextField(5);
		JTextField unitField = new JTextField(10);
		JTextField thresholdField = new JTextField(5);
		JTextField expiryField = new JTextField(10);

		unitField.setText("pcs");
		thresholdField.setText("1.0");

		JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		inputPanel.add(new JLabel("Item Name:"));
		inputPanel.add(nameField);
		inputPanel.add(new JLabel("Quantity:"));
		inputPanel.add(quantityField);
		inputPanel.add(new JLabel("Unit (e.g., pcs, kg, L):"));
		inputPanel.add(unitField);
		inputPanel.add(new JLabel("Threshold:"));
		inputPanel.add(thresholdField);
		inputPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
		inputPanel.add(expiryField);

		int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add New " + category + " Item",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String name = nameField.getText().trim();
			String quantityString = quantityField.getText().trim();
			String unit = unitField.getText().trim();
			String thresholdString = thresholdField.getText().trim();
			String expiryDate = expiryField.getText().trim();

			if (name.isEmpty() || quantityString.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Item Name and Quantity cannot be empty.", "Input Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (isItemAlreadyExists(name)) {
				JOptionPane.showMessageDialog(this,
						"Item '" + name + "' already exists in " + category + " category.\n"
								+ "Please use Restock instead of adding duplicate items.",
						"Duplicate Item", JOptionPane.WARNING_MESSAGE);
				return;
			}

			try {
				double quantity = Double.parseDouble(quantityString);
				double threshold = Double.parseDouble(thresholdString);

				if (quantity <= 0) {
					JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Input Error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (threshold < 0) {
					JOptionPane.showMessageDialog(this, "Threshold cannot be negative.", "Input Error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (unit.isEmpty()) {
					unit = "pcs";
				}

				// Add LocalDate parsing here
				LocalDate expiryLocalDate = null;
				if (!expiryDate.isEmpty()) {
					try {
						expiryLocalDate = LocalDate.parse(expiryDate);
					} catch (DateTimeParseException ex) {
						JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD format.",
								"Date Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				String newItemId = generateNextItemId();

				// Fix the table row - removed status and use LocalDate object
				tableModel.addRow(new Object[] { newItemId, name, unit, quantity, threshold, expiryLocalDate });

				PantryItem newItem = new PantryItem(newItemId, name, category, quantity, unit, threshold,
						expiryLocalDate);
				categoryItems.add(newItem);
				List<PantryItem> allUserItems = repository.loadAll(userId);
				allUserItems.add(newItem);
				repository.saveAll(userId, allUserItems);
				updateItemCount();

				JOptionPane.showMessageDialog(this,
						"Item '" + name + "' added successfully to " + category + " category!", "Success",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Invalid number format for Quantity or Threshold.", "Input Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private String generateNextItemId() {
		List<PantryItem> allItems = repository.loadAll(userId);
		int maxId = 1000;

		for (PantryItem item : allItems) {
			try {
				String idStr = item.getitemID().replace("ITEM_", "");
				int idNum = Integer.parseInt(idStr);
				if (idNum > maxId) {
					maxId = idNum;
				}
			} catch (NumberFormatException e) {
			}
		}
		return "ITEM_" + (maxId + 1);
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setBackground(new Color(230, 245, 230));
		JButton addButton = createGreenRoundedButton("Add New Item");
		JButton consumeButton = createGreenRoundedButton("Consume Item");
		JButton restockButton = createGreenRoundedButton("Restock Item");
		JButton backButton = createGreenRoundedButton("Back To Dashboard");
		addButton.addActionListener(e -> showAddItemDialog());
		consumeButton.addActionListener(e -> consumeSelectedItem());
		restockButton.addActionListener(e -> restockSelectedItem());
		backButton.addActionListener(e -> dispose());
		buttonPanel.add(addButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(consumeButton);
		buttonPanel.add(restockButton);
		buttonPanel.add(backButton);
		return buttonPanel;
	}

	private void consumeSelectedItem() {
		int selectedRow = itemsTable.getSelectedRow();

		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an item to consume.", "No Item Selected",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int modelRow = itemsTable.convertRowIndexToModel(selectedRow);

		String itemId = (String) tableModel.getValueAt(modelRow, 0);
		String itemName = (String) tableModel.getValueAt(modelRow, 1);
		double currentQuantity = (Double) tableModel.getValueAt(modelRow, 3);
		String unit = (String) tableModel.getValueAt(modelRow, 2);

		String input = JOptionPane.showInputDialog(this, "How much " + itemName
				+ " would you like to consume?\nCurrent quantity: " + currentQuantity + " " + unit, "Consume Item",
				JOptionPane.QUESTION_MESSAGE);

		if (input == null || input.trim().isEmpty()) {
			return;
		}

		try {
			double consumeAmount = Double.parseDouble(input.trim());

			if (consumeAmount <= 0) {
				JOptionPane.showMessageDialog(this, "Consumption amount must be greater than zero.", "Invalid Amount",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (consumeAmount > currentQuantity) {
				JOptionPane.showMessageDialog(this,
						"Cannot consume more than available quantity.\nAvailable: " + currentQuantity + " " + unit,
						"Insufficient Quantity", JOptionPane.ERROR_MESSAGE);
				return;
			}

			double newQuantity = currentQuantity - consumeAmount;
			double threshold = (Double) tableModel.getValueAt(modelRow, 4);

			tableModel.setValueAt(newQuantity, modelRow, 3);

			String newStatus = (newQuantity <= threshold) ? "To Buy" : "Purchased";
			tableModel.setValueAt(newStatus, modelRow, 6);

			PantryItem item = categoryItems.get(modelRow);
			item.setQuantity(newQuantity);
			List<PantryItem> allUserItems = repository.loadAll(userId);
			// Find and update the item in the main list
			for (int i = 0; i < allUserItems.size(); i++) {
				if (allUserItems.get(i).getitemID().equals(itemId)) {
					allUserItems.set(i, item);
					break;
				}
			}
			repository.saveAll(userId, allUserItems);
			JOptionPane
					.showMessageDialog(this,
							"Consumed " + consumeAmount + " " + unit + " of " + itemName + ".\nNew quantity: "
									+ newQuantity + " " + unit,
							"Consumption Successful", JOptionPane.INFORMATION_MESSAGE);

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Please enter a valid number for consumption amount.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void restockSelectedItem() {
		int selectedRow = itemsTable.getSelectedRow();

		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an item to restock.", "No Item Selected",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int modelRow = itemsTable.convertRowIndexToModel(selectedRow);

		String itemId = (String) tableModel.getValueAt(modelRow, 0);
		String itemName = (String) tableModel.getValueAt(modelRow, 1);
		double currentQuantity = (Double) tableModel.getValueAt(modelRow, 3);
		String unit = (String) tableModel.getValueAt(modelRow, 2);

		String input = JOptionPane.showInputDialog(this,
				"How much " + itemName + " would you like to add?\nCurrent quantity: " + currentQuantity + " " + unit,
				"Restock Item", JOptionPane.QUESTION_MESSAGE);

		if (input == null || input.trim().isEmpty()) {
			return;
		}

		try {
			double restockAmount = Double.parseDouble(input.trim());

			if (restockAmount <= 0) {
				JOptionPane.showMessageDialog(this, "Restock amount must be greater than zero.", "Invalid Amount",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			double newQuantity = currentQuantity + restockAmount;

			// Update the table
			tableModel.setValueAt(newQuantity, modelRow, 3);

			// Update the local category items list
			PantryItem item = categoryItems.get(modelRow);
			item.setQuantity(newQuantity);

			// SAVE CHANGES TO REPOSITORY
			List<PantryItem> allUserItems = repository.loadAll(userId);
			// Find and update the item in the main list
			for (int i = 0; i < allUserItems.size(); i++) {
				PantryItem currentItem = allUserItems.get(i);
				if (currentItem.getitemID().equals(itemId)) {
					currentItem.setQuantity(newQuantity);
					break;
				}
			}
			// Save all items back to file
			repository.saveAll(userId, allUserItems);

			JOptionPane.showMessageDialog(this, "Restocked " + restockAmount + " " + unit + " of " + itemName
					+ ".\nNew quantity: " + newQuantity + " " + unit, "Restock Successful",
					JOptionPane.INFORMATION_MESSAGE);

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Please enter a valid number for restock amount.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean isItemAlreadyExists(String newItemName) {
		for (PantryItem item : categoryItems) {
			if (item.getName().equalsIgnoreCase(newItemName)) {
				return true;
			}
		}
		return false;
	}

	private JButton createStyledButton(String text, Color backgroundColor) {
		JButton button = new JButton(text);

		button.setBackground(backgroundColor);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Terbuchet MS", Font.BOLD, 13));
		button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);

		// Hover effect
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(backgroundColor.darker());
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(backgroundColor);
			}
		});

		return button;
	}

	@Override
	public void pantryChanged() {
		SwingUtilities.invokeLater(this::refreshFromRepository);
	}

	// Public helper that truly reloads from repository
	private void refreshFromRepository() {
		tableModel.setRowCount(0);
		categoryItems = loadCategoryItems();
		for (PantryItem item : categoryItems) {
			tableModel.addRow(new Object[] { item.getitemID(), item.getName(), item.getUnit(), item.getQuantity(),
					item.getThreshold(), item.getExpiryDate() });
		}
		updateItemCount();
	}
}