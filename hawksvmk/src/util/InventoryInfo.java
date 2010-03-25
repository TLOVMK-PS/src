// InventoryInfo.java by Matt Fritz
// December 5, 2009
// Describes an inventory item

package util;

import java.io.Serializable;

public class InventoryInfo implements Serializable
{
	private String id = "";
	private String name = "";
	private String path = "";
	private String cardPath = "";
	
	public InventoryInfo(String id, String name, String path, String cardPath)
	{
		this.id = id;
		this.name = name;
		this.path = path;
		this.cardPath = cardPath;
	}
	
	public String getID() {return id;}
	public String getName() {return name;}
	public String getPath() {return path;}
	public String getCardPath() {return cardPath;}
	
	public void setID(String id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setPath(String path) {this.path = path;}
	public void setCardPath(String cardPath) {this.cardPath = cardPath;}
}
