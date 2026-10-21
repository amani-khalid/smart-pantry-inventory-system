package repo;

import java.util.List;
import models.PantryItem;

/**
 * Data access contract for pantry items. Lets us swap file storage for a
 * database later without changing the service / UI code.
 */
public interface PantryRepository {

	// Load all items for the given user (returned to the service at startup).
	List<PantryItem> loadAll(String userId); // String userId

	// Save all items for the given user (called after every change).
	void saveAll(String userId, List<PantryItem> items);
}