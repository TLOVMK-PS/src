// Tile.java by Matt Fritz
// October 28, 2009
// Represents a tile on the room grid

package tiles;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

import util.AppletResourceLoader;
import util.GameConstants;

public class Tile implements Serializable
{
	// tile types
	public static final int TILE_EXIT = 0;
	public static final int TILE_NOGO = 1;
	public static final int TILE_WALK = 2;
	
	private int width = 64; // tile width
	private int height = 32; // tile height
	
	private int type = TILE_WALK; // tile type
	
	private String dest = ""; // destination that the tile leads to
	
	private int row = 0; // grid row
	private int col = 0; // grid column
	
	private int x = 0; // absolute x-coordinate on the screen
	private int y = 0; // absolute y-coordinate on the screen
	
	private Tile parent = null;
	private int g = 0; // 10 for an orthogonal move, 14 for a diagonal move
	private int h = 0; // distance to the target
	
	private ImageIcon image; // the image file for the tile
	
	private String directionRelativeToParent = GameConstants.CONST_DIRECTION_SOUTH_EAST; // the direction that the player would face when walking to this tile
	
	public Tile() {}
	
	public Tile(int row, int col, int type, String dest)
	{
		this.row = row;
		this.col = col;
		this.type = type;
		this.dest = dest;
		
		// set the absolute coordinates
		setAbsoluteCoordinates();
	}
	
	public Tile(int row, int col, String typeString, String dest)
	{
		this.row = row;
		this.col = col;
		this.dest = dest;
		
		// set the tile image
		setTypeFromString(typeString);
		
		// set the absolute coordinates
		setAbsoluteCoordinates();
	}
	
	public String getDest() {
		return dest;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	// set the tile's absolute X-Y coordinates on the screen
	public void setAbsoluteCoordinates()
	{
		if(row % 2 == 0) // set coordinates for an even row
		{
			x = col * width;
			y = row * (height / 2);
		}
		else
		{
			// set coordinates for an odd row
			x = col * width + (width / 2);
			y = row * (height / 2);
		}
	}
	
	public int getF() // cost of moving to this tile (F = G + H)
	{
		return g + h;
	}
	
	public int getG() {return g;}
	public void setG(int g) {this.g = g;}
	public int getH() {return h;}
	public void setH(int h) {this.h = h;}
	
	public void setParent(Tile parent)
	{
		this.parent = parent;
	}
	
	public Tile getParent() {return parent;}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return col;
	}

	public void setColumn(int col) {
		this.col = col;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public Image getImage() {
		return image.getImage();
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	// return a String version of the "type" integer variable
	public String getTypeString()
	{
		if(type == Tile.TILE_EXIT) {return "EXIT";}
		if(type == Tile.TILE_NOGO) {return "NOGO";}
		if(type == Tile.TILE_WALK) {return "WALK";}
		
		return "NOGO";
	}
	
	// set the integer "type" variable given a String
	public void setTypeFromString(String typeString)
	{
		if(typeString.contains("EXIT"))
		{
			type = Tile.TILE_EXIT;
		}
		else if(typeString.contains("NOGO"))
		{
			type = Tile.TILE_NOGO;
		}
		else if(typeString.contains("WALK"))
		{
			type = Tile.TILE_WALK;
		}
		else
		{
			type = Tile.TILE_NOGO;
		}
	}
	
	// return the direction of this tile relative to another tile
	public String getDirectionRelativeToTile(Tile relTile, boolean flipDirection)
	{
		// set the proper X and Y coordinates for this tile and the relative tile
		int myX = getX(), myY = getY();
		int yourX = relTile.getX(), yourY = relTile.getY();
		
		// check to see if the direction needs to be flipped
		if(flipDirection)
		{
			// reverse the X coordinates
			myX = relTile.getX();
			yourX = getX();
		}
		
		// check to make sure the parent tile exists
		if(relTile != null)
		{
			if(myX < yourX)
			{
				if(myY < yourY) // north-west
				{
					return GameConstants.CONST_DIRECTION_NORTH_WEST;
				}
				else if(myY > yourY) // south-west
				{
					return GameConstants.CONST_DIRECTION_SOUTH_WEST;
				}
				else // west
				{
					return GameConstants.CONST_DIRECTION_WEST;
				}
			}
			else if(myX > yourX)
			{
				if(myY < yourY) // north-east
				{
					return GameConstants.CONST_DIRECTION_NORTH_EAST;
				}
				else if(myY > yourY) // south-east
				{
					return GameConstants.CONST_DIRECTION_SOUTH_EAST;
				}
				else // west
				{
					return GameConstants.CONST_DIRECTION_EAST;
				}
			}
			else
			{
				if(myY < yourY) // north
				{
					return GameConstants.CONST_DIRECTION_NORTH;
				}
				else // south
				{
					return GameConstants.CONST_DIRECTION_SOUTH;
				}
			}
		}
		
		return "";
	}

	public String toString()
	{
		return "@" + row + "," + col + "," + getTypeString() + "," + dest + "@";
	}
}
