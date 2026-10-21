package service;

import repo.PantryRepository;
import models.PantryItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import repo.FilePantryRepository;
import models.User;

/*
  This class DOES the actual work.
  It stores items in a List, updates them, validates them,
  and saves everything to the CSV file after each change.
 */
public class PantryServiceImpl implements PantryService {

	private final PantryRepository repo; // used for file reading/writing
	private final String userId; // used by repository
	private final List<PantryItem> items; // in-memory list of pantry items
	private User user;

	public PantryServiceImpl(PantryRepository repo, String userId) {
		this.repo = repo;
		this.userId = userId;

		// Load all items from file at startup
		this.items = new ArrayList<>(repo.loadAll(userId));
	}

	// ---- READ METHODS -----

	// Returns a COPY of the list so UI cannot change it by accident.
	@Override
	public List<PantryItem> getAll() {
		return new ArrayList<>(items); // Use the in-memory list that was already loaded
	}

	// Counts total, low stock, and expiring soon items.
	@Override
	public int[] headerCounts() {
		int low = 0;
		int soon = 0;
		LocalDate today = LocalDate.now();

		for (PantryItem it : items) {
			if (it.isLowStock())
				low++; // quantity < threshold
			if (it.isAboutToExpire(today))
				soon++; // expires within 15 days
		}
		return new int[] { items.size(), low, soon };
	}

	// --- ADD / EDIT / DELETE ----

	// Adds a new pantry item.
	@Override
	public void add(PantryItem item) {
		validate(item); // check values are valid
		items.add(item); // add item to memory list
		persist(); // save list to file
	}

	// Updates an existing item using its ID.
	@Override
	public void edit(String id, PantryItem updated) {
		int index = indexOf(id); // find item position in list
		if (index < 0)
			return; // item not found → do nothing

		validate(updated); // check new values are valid

		// Keep same ID but replace other fields
		PantryItem original = items.get(index);
		PantryItem fixed = new PantryItem(original.getitemID(), // keep same ID
				updated.getName(), // apply updated values
				updated.getCategory(), updated.getQuantity(), updated.getUnit(), updated.getThreshold(),
				updated.getExpiryDate());

		items.set(index, fixed); // replace old item with new one
		persist(); // save to file
	}

	// Removes an item completely.
	@Override
	public void delete(String id) {
		int index = indexOf(id);
		if (index < 0)
			return;
		items.remove(index); // delete from list
		persist(); // save to file
	}

	// ---- STOCK ACTIONS ----

	// Reduces quantity. Cannot go below 0.
	@Override
	public void consume(String id, double amount) {
		if (amount < 0)
			return;

		int index = indexOf(id);
		if (index < 0)
			return;

		PantryItem it = items.get(index);
		double newQty = Math.max(0, it.getQuantity() - amount); // prevent negative
		it.setQuantity(newQty);

		persist();
	}

	// Adds more quantity.
	@Override
	public void restock(String id, double amount) {
		if (amount < 0)
			return;

		int index = indexOf(id);
		if (index < 0)
			return;

		PantryItem it = items.get(index);
		it.setQuantity(it.getQuantity() + amount);

		persist();
	}

	// ---- SEARCH / FILTER ----

	// Search by name or category (case-insensitive)
	@Override
	public List<PantryItem> searchByNameOrCategory(String q) {
		String query = q == null ? "" : q.toLowerCase();
		List<PantryItem> out = new ArrayList<>();

		for (PantryItem it : items) {
			String name = it.getName().toLowerCase();
			String cat = it.getCategory().toLowerCase();

			// If name OR category contains the search text
			if (name.contains(query) || cat.contains(query)) {
				out.add(it);
			}
		}
		return out;
	}

	// Items that expire in 15 days or less.
	@Override
	public List<PantryItem> aboutToExpire() {
		LocalDate today = LocalDate.now();
		List<PantryItem> out = new ArrayList<>();

		for (PantryItem it : items) {
			if (it.isAboutToExpire(today)) {
				out.add(it);
			}
		}
		return out;
	}

	// Items where quantity < threshold.
	@Override
	public List<PantryItem> lowStock() {
		List<PantryItem> out = new ArrayList<>();
		for (PantryItem it : items) {
			if (it.isLowStock()) {
				out.add(it);
			}
		}
		return out;
	}

	// ---- HELPER METHODS ----

	// Finds the index of an item using its ID.
	private int indexOf(String id) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getitemID().equals(id)) {
				return i;
			}
		}
		return -1;
	}

	// Basic validation for add/edit operations.
	private void validate(PantryItem it) {
		if (it.getName().isBlank())
			throw new IllegalArgumentException("Name is required.");
		if (it.getCategory().isBlank())
			throw new IllegalArgumentException("Category is required.");
		if (it.getUnit().isBlank())
			throw new IllegalArgumentException("Unit is required.");
		if (it.getQuantity() < 0)
			throw new IllegalArgumentException("Quantity must be >= 0.");
		if (it.getThreshold() < 0)
			throw new IllegalArgumentException("Threshold must be >= 0.");
		if (it.getExpiryDate() == null)
			throw new IllegalArgumentException("Expiry date is required.");
		if (it.getExpiryDate().isBefore(LocalDate.now()))
			throw new IllegalArgumentException("Expiry cannot be in the past.");
	}

	// Writes all items to the CSV file.
	private void persist() {
		repo.saveAll(userId, items);
	}
}