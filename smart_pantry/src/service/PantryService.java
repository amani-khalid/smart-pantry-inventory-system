package service;

import java.util.List;
import models.PantryItem;

/*
 This interface lists all actions that the Pantry Dashboard can do.
 The UI talks to this interface, not to the file directly.
 */
public interface PantryService {

	// Returns ALL pantry items as a list.
	List<PantryItem> getAll();

	// Adds a new pantry item (create).
	void add(PantryItem item);

	// Updates an existing pantry item using its ID.
	void edit(String id, PantryItem updated);

	// Deletes an item using its ID.
	void delete(String id);

	// Reduces quantity by "amount" (quantity cannot go below 0).
	void consume(String id, double amount);

	// Increases quantity by "amount".
	void restock(String id, double d);

	// Searches items by name OR category (case-insensitive).
	List<PantryItem> searchByNameOrCategory(String q);

	// Returns items that expire in 15 days or less.
	List<PantryItem> aboutToExpire();

	// Returns items where quantity < threshold.
	List<PantryItem> lowStock();

	// Returns { totalCount , lowCount , soonExpiringCount }
	int[] headerCounts();
}
