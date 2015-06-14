// MessageUpdateInventory.java by Matt Fritz
// September 11, 2010
// Handle updating the player's inventory items

package sockets.messages;

import util.InventoryItem;

public class MessageAddInventory extends MessageSecure
{
	private String username = "";
	private InventoryItem item;
	
	public MessageAddInventory() {super("MessageAddInventory");}
	
	public MessageAddInventory(String username, InventoryItem item)
	{
		super("MessageAddInventory");
		
		this.username = username;
		this.item = item;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public InventoryItem getItem() {
		return item;
	}

	public void setItem(InventoryItem item) {
		this.item = item;
	}
}