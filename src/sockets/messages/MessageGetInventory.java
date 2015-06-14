// MessageGetInventory.java by Matt Fritz
// March 24, 2010
// Handle sending a player his inventory

package sockets.messages;

import java.util.ArrayList;

import util.InventoryItem;

public class MessageGetInventory extends MessageSecure
{
	private ArrayList<InventoryItem> inventory = new ArrayList<InventoryItem>();
	
	public MessageGetInventory()
	{
		super("MessageGetInventory");
	}
	
	public MessageGetInventory(ArrayList<InventoryItem> inventory)
	{
		super("MessageGetInventory");
		
		this.inventory = inventory;
	}

	public ArrayList<InventoryItem> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<InventoryItem> inventory) {
		this.inventory = inventory;
	}
}
