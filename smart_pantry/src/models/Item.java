package models;

public class Item {

	protected String itemID;
	protected String name;
	protected String unit;

	public Item() {

	}

	public Item(String itemID, String name, String unit) {
		this.itemID = itemID;
		this.name = name;
		this.unit = unit;
	}

	public String getitemID() {
		return itemID;
	}

	public void setitemID(String id) {
		this.itemID = itemID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getInfo() {
		return "ID: " + itemID + ", Name: " + name + ", Unit: " + unit;
	}

	@Override
	public String toString() {
		return getInfo();
	}
}
