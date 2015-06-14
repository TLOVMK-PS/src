// GridObject.java by Matt Fritz
// November 14, 2010
// Simple class to be extended by Stationary and Moving objects on the grid

package gridobject;

import java.awt.image.BufferedImage;

import interfaces.GridSortable;

public class GridObject implements GridSortable
{
	private int x = 0; // X-coord of the object
	private int y = 0; // Y-coord of the object
	
	private int baseY = 0; // Y-coord at the base of the object
	
	private String name = ""; // name of the object
	
	private String imagePath = "";
	
	private BufferedImage image;
	
	public GridObject() {}
	
	public GridObject(String name, int x, int y, BufferedImage image)
	{
		this.name = name;
		
		this.x = x;
		this.y = y;
		
		this.image = image;
		
		baseY = y + image.getHeight();
	}

	public String getName() {
		return name;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image)
	{
		this.image = image;
		baseY = y + image.getHeight();
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	
	public void setImagePath(String imagePath) {this.imagePath = imagePath;}
	public String getImagePath() {return imagePath;}
	
	public int getBaseY() {return baseY;}
	
	public String toString()
	{
		return name + "," + x + "," + y + "," + imagePath;
	}
}
