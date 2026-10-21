package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingList {
	private String listID;
	private Date dateCreated;
	private String userID; // <— owner
	private List<ShoppingListItem> entries;

	public ShoppingList(String userID) {
		this.listID = generateUniqueId();
		this.dateCreated = new Date();
		this.userID = userID;
		this.entries = new ArrayList<>();
	}

	// getters
	public String getListID() {
		return listID;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public String getUserID() {
		return userID;
	}

	public List<ShoppingListItem> getEntries() {
		return entries;
	}

	// add/merge
	public void addEntry(String name, double qty, String unit) {
		for (ShoppingListItem e : entries) {
			String n = e.getName();
			if (n != null && n.equalsIgnoreCase(name) && "To Buy".equals(e.getStatus())) {
				e.setQuantity(e.getQuantity() + qty);
				return;
			}
		}
		String id = "ITM-" + System.currentTimeMillis();
		entries.add(new ShoppingListItem(id, name, unit, qty));
	}

	public void removeEntry(String itemName) {
		entries.removeIf(e -> {
			String n = e.getName();
			return n != null && n.equalsIgnoreCase(itemName);
		});
	}

	private String generateUniqueId() {
		return "SL-" + System.currentTimeMillis();
	}

	// --- persistence helpers (used by manager.loadLists) ---
	public void setListIDForLoad(String id) {
		this.listID = id;
	}

	public void setDateCreatedForLoad(Date d) {
		this.dateCreated = d;
	}

}
