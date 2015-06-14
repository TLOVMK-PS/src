// MessageUpdateInventory.java by Matt Fritz
// September 11, 2010
// Handle updating the player's inventory items

package sockets.messages;

import java.util.ArrayList;

import util.InventoryItem;

public class MessageUpdateInventory extends MessageSecure
{
	private String username = "";
	private ArrayList<InventoryItem> inventory = new ArrayList<InventoryItem>();
	
	public MessageUpdateInventory() {super("MessageUpdateInventory");}
	
	public MessageUpdateInventory(String username, ArrayList<InventoryItem> inventory)
	{
		super("MessageUpdateInventory");
		
		this.username = username;
		this.inventory = inventory;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList<InventoryItem> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<InventoryItem> inventory) {
		this.inventory = inventory;
	}
}
