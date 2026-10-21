package managers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import javax.swing.JOptionPane;
import models.PantryItem;
import service.PantryService;
import models.ShoppingListItem;
import models.ShoppingList;
import java.io.*;
import interfaces.PantryObserver;

public class ShoppingListManager implements PantryObserver {
	private static final String FILE_NAME = "shopping_lists.txt";
	private final PantryService pantry;
	protected final List<ShoppingList> allLists;

	public ShoppingListManager(PantryService pantry) {
		this.pantry = pantry;
		this.allLists = loadLists();
	}

	public PantryService getPantryService() {
		return pantry;
	}

	private final List<PantryObserver> observers = new ArrayList<>();

	public void addObserver(PantryObserver o) {
		observers.add(o);
	}

	public void removeObserver(PantryObserver o) {
		observers.remove(o);
	}

	private void notifyObservers() {
		for (PantryObserver o : new ArrayList<>(observers))
			o.pantryChanged();
	}

	// ---- Query helpers ----
	public List<ShoppingList> getListsForUser(String userID) {
		List<ShoppingList> out = new ArrayList<>();
		for (ShoppingList l : allLists)
			if (l.getUserID().equals(userID))
				out.add(l);
		return out;
	}

	public ShoppingList getOrCreateListForUser(String userID) {
		List<ShoppingList> mine = getListsForUser(userID);
		if (!mine.isEmpty())
			return mine.get(0);
		ShoppingList l = new ShoppingList(userID);
		allLists.add(l);
		saveLists();
		return l;
	}

	// ---- Auto-generate from pantry low stock ----
	public ShoppingList generateAutoList(String userID) {
		ShoppingList list = new ShoppingList(userID);
		List<PantryItem> low = pantry.lowStock(); // service is already user-scoped in main()
		for (PantryItem it : low) {
			double qtyToBuy = it.getThreshold() - it.getQuantity();
			if (qtyToBuy > 0)
				list.addEntry(it.getName(), qtyToBuy, it.getUnit());
		}
		allLists.add(list);
		saveLists();
		return list;
	}

	// ---- Purchased → pantry update ----
	public void markPurchased(String listID, String itemName, boolean purchased, String cat, LocalDate date) {
		ShoppingList list = getListByID(listID);
		if (list == null)
			return;
		for (ShoppingListItem e : list.getEntries()) {
			if (!itemName.equalsIgnoreCase(e.getName()))
				continue;
			e.setStatus(purchased ? "Purchased" : "To Buy");
			if (purchased)
				restockOrAdd(e, cat, date); // only touch pantry when buying
			break;
		}
		saveLists();
		notifyObservers();
	}

	private static String setCategory(String in) {
		if (in.equalsIgnoreCase("Fruits & Vegetables"))
			return "Fruits & Vegetables";
		else if (in.equalsIgnoreCase("Snacks"))
			return "Snacks";
		else if (in.equalsIgnoreCase("Canned and dried goods"))
			return "Canned and dried goods";
		else
			return "Beverages & Others";
	}

	private void restockOrAdd(ShoppingListItem e, String cat, LocalDate date) {
		PantryItem found = findPantryItemByName(e.getName());
		if (found != null) {
			pantry.restock(found.getitemID(), e.getQuantity());
		} else {
			PantryItem toAdd = new PantryItem("ITEM_" + (1000 + PantryItem.pantryItemCounter), // check again
					e.getName(), setCategory(cat), e.getQuantity(),
					(e.getUnit() == null || e.getUnit().isBlank()) ? "pcs" : e.getUnit(), Math.max(1, e.getQuantity()),
					date);
			pantry.add(toAdd);
		}
	}

	private PantryItem findPantryItemByName(String name) {
		for (PantryItem it : pantry.searchByNameOrCategory(name)) {
			if (it.getName().equalsIgnoreCase(name))
				return it;
		}
		return null;
	}

	public ShoppingList getListByID(String listID) {
		for (ShoppingList l : allLists)
			if (l.getListID().equals(listID))
				return l;
		return null;
	}

	// ---- Persistence: ALL users' lists in one file ----
	public void saveLists() {
		try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
			for (ShoppingList list : allLists) {
				Date epoch = list.getDateCreated() != null ? list.getDateCreated() : new Date();
				// out.println("LIST: " + list.getListID() + "| Date: " + epoch + "| User ID: "
				// + list.getUserID());
				out.println("LIST|" + list.getListID() + "|" + epoch + "|" + list.getUserID());
				for (ShoppingListItem it : list.getEntries()) {
					String name = safe(it.getName());
					String unit = safe(it.getUnit());
					String status = (it.getStatus() == null ? "To Buy" : it.getStatus());
					// out.println("ITEM :" + name + "| Quantity:" + it.getQuantity() + "| Unit: " +
					// unit + "| Status:"
					// + status);
					out.println("ITEM|" + name + "|" + it.getQuantity() + "|" + unit + "|" + status);
				}
				out.println("END");
			}
		} catch (IOException e) {
			System.err.println("Error saving shopping lists: " + e.getMessage());
		}
	}

	private String safe(String s) {
		return s == null ? "" : s.replace("|", "/");
	}

	private List<ShoppingList> loadLists() {
		List<ShoppingList> result = new ArrayList<>();
		File f = new File(FILE_NAME);
		if (!f.exists())
			return result;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			ShoppingList current = null;
			while ((line = br.readLine()) != null) {
				if (line.isBlank())
					continue;
				String[] p = line.split("\\|", -1);
				switch (p[0]) {
				case "LIST": {
					if (current != null)
						result.add(current);
					// LIST|listID|epoch|userID
					if (p.length < 4) {
						current = null;
						break;
					}
					current = new ShoppingList(p[3]);
					current.setListIDForLoad(p[1]);

					try {
						current.setDateCreatedForLoad(new java.util.Date(Long.parseLong(p[2])));
					} catch (NumberFormatException ignored) {
					}
					break;
				}
				case "ITEM": {
					if (current == null || p.length < 5)
						break;
					String name = p[1];
					double qty = 0;
					try {
						qty = Integer.parseInt(p[2]);
					} catch (Exception ignored) {
					}
					String unit = p[3];
					String status = p[4];
					// String id = "ITM-" + System.nanoTime();
					String id = name;
					try {
						qty = Double.parseDouble(p[2]);
					} catch (Exception ignored) {
					}
					ShoppingListItem sli = new ShoppingListItem(name, id, unit, qty, status);
					// ShoppingListItem sli = new ShoppingListItem(name, id, unit, qty, status);
					current.getEntries().add(sli);
					break;
				}
				case "END": {
					if (current != null) {
						result.add(current);
						current = null;
					}
					break;
				}
				}
			}
			if (current != null)
				result.add(current);
		} catch (IOException e) {
			System.err.println("Error loading shopping lists: " + e.getMessage());
		}
		return result;
	}

	@Override
	public void pantryChanged() {
		// TODO Auto-generated method stub
	}
}