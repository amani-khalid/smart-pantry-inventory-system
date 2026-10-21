package models;

import java.time.LocalDate;

/**
 * PantryItem extends Item and adds pantry-specific fields: category, quantity,
 * threshold and expiry date.
 */
public class PantryItem extends Item {

	private String category;
	private double quantity;
	private double threshold;
	private LocalDate expiryDate;
	public static int pantryItemCounter = 0;

	// Full constructor (used when loading from file)
	public PantryItem(String id, String name, String category, double quantity, String unit, double threshold,
			LocalDate expiryDate) {

		// id, name, unit from Item
		super(id, name, unit);
		this.category = category;
		this.quantity = quantity;
		this.threshold = threshold;
		this.expiryDate = expiryDate;
		pantryItemCounter++;
	}

	// Constructor for NEW items (ID will be set later)
	public PantryItem(String name, String category, int quantity, String unit, int threshold, LocalDate expiryDate) {

		this(null, name, category, quantity, unit, threshold, expiryDate);
	}

	// -------- Getters & setters for extra fields --------
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	// -------- Helper methods used by the service --------

	// quantity < threshold → low stock
	public boolean isLowStock() {
		return quantity < threshold;
	}

	// expiryDate before today → expired
	public boolean isExpired(LocalDate today) {
		return expiryDate != null && expiryDate.isBefore(today);
	}

	// expiryDate within next 15 days → about to expire
	public boolean isAboutToExpire(LocalDate today) {
		if (expiryDate == null)
			return false;
		LocalDate limit = today.plusDays(15);
		return !expiryDate.isBefore(today) && !expiryDate.isAfter(limit);
	}

	@Override
	public String toString() {
		return "PantryItem{" + "id='" + getitemID() + '\'' + ", name='" + getName() + '\'' + ", category='" + category
				+ '\'' + ", quantity=" + quantity + ", unit='" + getUnit() + '\'' + ", threshold=" + threshold
				+ ", expiryDate=" + expiryDate + '}';
	}
}