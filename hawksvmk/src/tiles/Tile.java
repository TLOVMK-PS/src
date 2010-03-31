// Tile.java by Matt Fritz
// October 28, 2009
// Represents a tile on the room grid

package tiles;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

import util.AppletResourceLoader;

public class Tile implements Serializable
{
	// tile types
	public static final int TILE_EXIT = 0;
	public static final int TILE_NOGO = 1;
	public static final int TILE_WALK = 2;
	
	//private ImageIcon nogoTileImage = AppletResourceLoader.getImageFromJar("tiles_img/tile_nogo.png");
	//private ImageIcon walkTileImage = AppletResourceLoader.getImageFromJar("tiles_img/tile_walk.png");
	//private ImageIcon exitTileImage = AppletResourceLoader.getImageFromJar("tiles_img/tile_exit.png");
	
	private int width = 64; // tile width
	private int height = 32; // tile height
	
	private int type = TILE_WALK; // tile type
	
	private String dest = ""; // destination that the tile leads to
	
	private int row = 0; // grid row
	private int col = 0; // grid column
	
	private int x = 0; // absolute x-coordinate on the screen
	private int y = 0; // absolute y-coordinate on the screen
	
	private Tile parent;
	private int g = 0; // 10 for an orthogonal move, 14 for a diagonal move
	private int h = 0; // distance to the target
	
	private ImageIcon image; // the image file for the tile
	
	public Tile() {}
	
	public Tile(int row, int col, int type, String dest)
	{
		this.row = row;
		this.col = col;
		this.type = type;
		this.dest = dest;
		
		// set the tile image
		setImageFromType();
		
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
	private void setAbsoluteCoordinates()
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
	
	public void setParent(Tile parent) {this.parent = parent;}
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
		
		// set the tile image
		setImageFromType();
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
		
		// set the tile image
		setImageFromType();
	}
	
	// set the tile image from the "type" integer variable
	private void setImageFromType()
	{
		//if(type == Tile.TILE_EXIT) {image = exitTileImage;}
		//if(type == Tile.TILE_NOGO) {image = nogoTileImage;}
		//if(type == Tile.TILE_WALK) {image = walkTileImage;}
	}
	
	/*public ImageIcon getNogoTileImage() {
		return nogoTileImage;
	}

	public ImageIcon getWalkTileImage() {
		return walkTileImage;
	}

	public ImageIcon getExitTileImage() {
		return exitTileImage;
	}*/

	public String toString()
	{
		return "@" + row + "," + col + "," + getTypeString() + "," + dest + "@";
	}
}
