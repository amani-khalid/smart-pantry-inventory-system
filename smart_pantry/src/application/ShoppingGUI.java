package application;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import models.PantryItem;
import java.time.LocalDate;
import java.util.UUID;
import models.ShoppingList;
import models.ShoppingListItem;
import managers.ShoppingListManager;
import repo.FilePantryRepository;
import service.PantryServiceImpl;
import service.PantryService;
import repo.PantryRepository;
import java.time.LocalDate;
import java.util.UUID;
import models.User;
import application.CategoryWindow3;

public class ShoppingGUI extends JFrame implements ActionListener {

	private final ShoppingListManager manager;
	private ShoppingList currentList;
	private User user;
	private ShoppingListTableModel tableModel;
	private JTable shoppingTable;
	private JButton addButton;
	private JButton removeButton;
	private JButton AutoShoppingList;
	private JButton backButton;
	private PantryServiceImpl pantry;
	String cate;
	private CategoryWindow3 window;
	LocalDate date;

	public ShoppingGUI(User user, ShoppingListManager manager, ShoppingList currentList, CategoryWindow3 window) {
		super(user.getName() + " — Shopping List");
		this.user = user;
		this.manager = manager;
		this.currentList = currentList;
		this.pantry = (PantryServiceImpl) manager.getPantryService();
		this.window = window;

		tableModel = new ShoppingListTableModel(currentList, manager);
		shoppingTable = new JTable(tableModel);
		shoppingTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		shoppingTable.setRowHeight(35);
		shoppingTable.setFont(new Font("Terbuchet MS", Font.PLAIN, 12));
		shoppingTable.getTableHeader().setFont(new Font("Terbuchet MS", Font.BOLD, 12));
		shoppingTable.getTableHeader().setBackground(new Color(241, 255, 244));
		shoppingTable.setSelectionBackground(new Color(220, 237, 255));

		addButton = createGreenRoundedButton("Add Item 🛒");
		removeButton = createGreenRoundedButton("Remove Item ❌");
		AutoShoppingList = createGreenRoundedButton("Auto Generate Shopping List 🔁");
		backButton = createGreenRoundedButton("Back To Dashboard ⏪");

		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		AutoShoppingList.addActionListener(this);
		backButton.addActionListener(this);

		add(new JScrollPane(shoppingTable), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(AutoShoppingList);
		buttonPanel.add(backButton);
		// Background color
		addButton.setBackground(new Color(34, 155, 60)); // #229B3C bright green
		// Text (foreground) color
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(800, 400);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				new Dashboard(user.getUserId(), pantry.getAll(), manager, currentList, user).setVisible(true);
				dispose();
			}
		});

		setLocationRelativeTo(null);
		setVisible(true);

		manager.saveLists();
		tableModel.fireTableDataChanged();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) {
			JTextField nameField = new JTextField(15);
			JTextField qtyField = new JTextField(5);
			JTextField unitField = new JTextField(10);
			JTextField categoryField = new JTextField(10);
			JTextField thresholdField = new JTextField(10);
			JTextField expiryField = new JTextField(10);

			JPanel inputPanel = new JPanel(new GridLayout(6, 2));
			inputPanel.add(new JLabel("Item Name:"));
			inputPanel.add(nameField);
			inputPanel.add(new JLabel("Quantity:"));
			inputPanel.add(qtyField);
			inputPanel.add(new JLabel("Unit (e.g., pcs):"));
			inputPanel.add(unitField);
			inputPanel.add(new JLabel("Category:"));
			inputPanel.add(categoryField);
			inputPanel.add(new JLabel("Threshold > 0 :"));
			inputPanel.add(thresholdField);
			inputPanel.add(new JLabel("Expiry date YYYY-MM-DD :"));
			inputPanel.add(expiryField);

			int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add New Shopping Item",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				String name = nameField.getText().trim();
				String qtyString = qtyField.getText().trim();
				String unit = unitField.getText().trim();
				String category = categoryField.getText().trim();
				String threshold = thresholdField.getText().trim();
				String expiry = expiryField.getText().trim();

				if (name.isEmpty() || qtyString.isEmpty() || threshold.isEmpty() || expiry.isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"Item Name, Quantity, threshold and expiry date cannot be empty.", "Input Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					int qty = Integer.parseInt(qtyString);
					int th = Integer.parseInt(threshold);
					if (qty <= 0 || th <= 0) {
						JOptionPane.showMessageDialog(this, "Quantity and threshold must be greater than zero.",
								"Input Error", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if (unit.isEmpty())
						unit = "pcs";
					if (category.isEmpty() || (!category.equalsIgnoreCase("Fruits & Vegetables") & !category.equalsIgnoreCase("Snacks")
									& !category.equalsIgnoreCase("Canned & dried goods")))
						category = "Beverages & Others";
					else {
						category = categoryField.getText().trim();
					}
					// As before: add to shopping list only
					currentList.addEntry(name, qty, unit);
					this.cate = category;
					if (LocalDate.now().isAfter(LocalDate.parse(expiry))) {
						JOptionPane.showMessageDialog(this, "Item Already expired!", "Notice",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					this.date = LocalDate.parse(expiry); // local date

					manager.saveLists();
					tableModel.fireTableDataChanged();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Invalid Quantity or Threshold entered. Please enter a number.",
							"Input Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == removeButton) {
			String itemName = JOptionPane.showInputDialog(this, "Enter the name of the item to remove:", "Remove Item",
					JOptionPane.QUESTION_MESSAGE);
			if (!itemName.trim().isEmpty()) {
				currentList.removeEntry(itemName.trim());
				tableModel.fireTableDataChanged();
				manager.saveLists();
			}	
		} else if (e.getSource() == AutoShoppingList) {
			ShoppingList auto = manager.generateAutoList(user.getUserId());
			this.currentList = auto;
			this.tableModel = new ShoppingListTableModel(currentList, manager);
			shoppingTable.setModel(tableModel);
			tableModel.fireTableDataChanged();
		} else if (e.getSource() == backButton) {
			new Dashboard(user.getUserId(), pantry.getAll(), manager, currentList, user).setVisible(true);
			dispose();
		}
	}

	// ---- Table Model (checkbox → markPurchased) ----
	class ShoppingListTableModel extends AbstractTableModel {
		private final ShoppingList shoppingList;
		private final ShoppingListManager manager;
		private final String[] columnNames = { "Item Name", "Quantity", "Unit", "Status", "Purchased" };

		public ShoppingListTableModel(ShoppingList list, ShoppingListManager manager) {
			this.shoppingList = list;
			this.manager = manager;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public int getRowCount() {
			return shoppingList.getEntries().size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return col == 4;
		}

		@Override
		public Class<?> getColumnClass(int col) {
			if (col == 1)
				return Integer.class;
			if (col == 4)
				return Boolean.class;
			return String.class;
		}

		@Override
		public Object getValueAt(int row, int col) {
			ShoppingListItem item = shoppingList.getEntries().get(row);

			switch (col) {
			case 0:
				return item.getName();
			case 1:
				return item.getQuantity();
			case 2:
				return item.getUnit();
			case 3:
				return item.getStatus();
			case 4:
				return "Purchased".equalsIgnoreCase(item.getStatus());
			default:
				return null;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			if (col != 4 || !(value instanceof Boolean))
				return;

			boolean isChecked = (Boolean) value;
			ShoppingListItem item = shoppingList.getEntries().get(row);

			item.setStatus(isChecked ? "Purchased" : "To Buy");

			manager.markPurchased(shoppingList.getListID(), item.getName(), isChecked, cate, date);

			fireTableCellUpdated(row, 3);
			fireTableCellUpdated(row, 4);
		}

		@Override
		public void fireTableDataChanged() {
			super.fireTableDataChanged();
		}
	}
}