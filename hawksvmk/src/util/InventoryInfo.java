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
	private String iconPath = "";
	private int tiles = 0;
	private int price = -1;
	private int ratingIndex = 0;
	
	public InventoryInfo(String id, String name, String path, String cardPath, String iconPath, int tiles, int price, int ratingIndex)
	{
		this.id = id;
		this.name = name;
		this.path = path;
		this.cardPath = cardPath;
		this.iconPath = iconPath;
		this.tiles = tiles;
		this.price = price;
		this.ratingIndex = ratingIndex;
	}
	
	public String getID() {return id;}
	public String getName() {return name;}
	public String getPath() {return path;}
	public String getCardPath() {return cardPath;}
	public String getIconPath() {return iconPath;}
	public int getTiles() {return tiles;}
	public int getPrice() {return price;}
	public int getRatingIndex() {return ratingIndex;}
	
	public void setID(String id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setPath(String path) {this.path = path;}
	public void setCardPath(String cardPath) {this.cardPath = cardPath;}
	public void setIconPath(String iconPath) {this.iconPath = iconPath;}
	public void setPrice(int price) {this.price = price;}
	public void setRatingIndex(int ratingIndex) {this.ratingIndex = ratingIndex;}
}
