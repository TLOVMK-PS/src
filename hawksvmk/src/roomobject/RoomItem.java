// RoomIte.java by Matt Fritz
// April 4, 2010
// Generic class that describes an item in a public room

package roomobject;

import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.ImageIcon;

import util.AppletResourceLoader;

public class RoomItem implements Serializable
{
	public final static int FURNITURE = 0;
	public final static int POSTER = 1;
	
	private int x = 0; // x-coordinate
	private int y = 0; // y-coordinate
	private int layer = 0; // layer that the item should be drawn on (can be same as y-coord)
	private int type = FURNITURE; // type of the room item
	
	private int row = 0;
	private int col = 0;
	
	private int tileHeight = 32; // height of the tiles in the room
	
	private String id = ""; // id of the item
	private String name = ""; // name of the item
	
	private String directory = ""; // directory where the images for the item are stored
	private String path = ""; // path to the image of the item
	private ImageIcon image; // image of the item
	
	private String rotation = "A"; // A, B, C, or D to describe one of four possible rotations
	
	private Rectangle boundingBox = new Rectangle(x, y, 0, 0);
	private String owner = ""; // the owner of the item
	
	public RoomItem() {}
	
	public RoomItem(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public RoomItem(int x, int y, int layer)
	{
		this(x,y);
		this.layer = layer;
	}
	
	public RoomItem(int x, int y, String id, String name, String directory, String rotation, int type)
	{
		this(x,y);
		this.id = id;
		this.name = name;
		this.directory = directory;
		this.rotation = rotation;
		this.type = type;
		
		// set the path to the image and the image itself
		this.path = directory + id + "_" + rotation + ".png";
		System.out.println(this.path);
		image = AppletResourceLoader.getImageFromJar(this.path);
		
		boundingBox.x = x;
		boundingBox.y = y;// + tileHeight - image.getIconHeight();
		boundingBox.width = image.getIconWidth();
		boundingBox.height = image.getIconHeight();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		boundingBox.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		boundingBox.y = y; //+ tileHeight - image.getIconHeight();
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public String getRotation() {
		return rotation;
	}

	public void setRotation(String rotation)
	{
		// set the rotation
		this.rotation = rotation;
		
		// set the path to the image and the image itself
		this.path = directory + id + "_" + rotation + ".png";
		image = AppletResourceLoader.getImageFromJar(this.path);
		
		boundingBox.width = image.getIconWidth();
		boundingBox.height = image.getIconHeight();
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getTileHeight() {
		return tileHeight;
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	// rotate the item
	public void rotate()
	{
		// figure out the necessary rotation
		if(rotation.equals("A"))
		{
			rotation = "B";
		}
		else if(rotation.equals("B"))
		{
			rotation = "C";
		}
		else if(rotation.equals("C"))
		{
			rotation = "D";
		}
		else if(rotation.equals("D"))
		{
			rotation = "A";
		}
		
		// resolve the image
		this.path = directory + id + "_" + rotation + ".png";
		image = AppletResourceLoader.getImageFromJar(this.path);
		
		// check to make sure the rotation image exists
		if(image == null)
		{
			// default to the "A" rotation
			rotation = "A";
			this.path = directory + id + "_" + rotation + ".png";
			image = AppletResourceLoader.getImageFromJar(this.path);
		}
	}
	
	public String toString()
	{
		return getId() + "," + getRow() + "," + getCol() + "," + getRotation();
	}
}
