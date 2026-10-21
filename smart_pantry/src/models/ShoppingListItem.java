package models;
// The class should extend the Item superclass

public class ShoppingListItem extends Item {

	private double quantity; // amount of the item
	private String status; // e.g., "To Buy", "Purchased"

	// Constructor for manual entry (will change)
	public ShoppingListItem(String itemName, String id, String unit, double quantity) {
		super(itemName, id, unit); // calling item constructor
		this.quantity = quantity;
		this.status = "To Buy"; // Default status in shopping list
	}

	// Constructor for automatic generation (or loading from file for each user)
	public ShoppingListItem(String id, String itemName, String unit, double quantity, String status) {
		super(id, itemName, unit); // calling item constructor
		this.quantity = quantity;
		this.status = status;
	}

	// Getters
	public double getQuantity() {
		return quantity;
	}

	public String getStatus() {
		return status;
	}

	// Setters
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// Utility for file saving (to be customized with actual delimiter)
	@Override
	public String toString() {
		return super.toString() + String.format(" Quantity: %d, Status: %s", getQuantity(), getStatus());
	}
}
