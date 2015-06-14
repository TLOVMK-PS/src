// InventoryItem.java by Matt Fritz
// March 22, 2010
// Handles an item in a player's inventory

package util;

import java.io.Serializable;

public class InventoryItem implements Serializable, Comparable<InventoryItem>
{
	// constants for types of inventory
	public static final int FURNITURE = 0;
	public static final int PIN = 1;
	public static final int POSTER = 2;
	public static final int CLOTHING = 3;
	
	private String name = "";
	private String id = "";
	private int type = FURNITURE;
	
	public InventoryItem(String name, String id, int type)
	{
		this.name = name;
		this.id = id;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	// compare inventory items by name
	public int compareTo(InventoryItem otherItem)
	{
		return name.compareTo(otherItem.getName());
	}
}
