// AStarShip.java by Matt Fritz
// November 4, 2010
// Simple class to handle a ship's movement on an A* path for Pirates of the Caribbean

package games.pirates;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import astar.*;

import tiles.Tile;
import util.AppletResourceLoader;

public class AStarShip extends AStarCharacter implements Serializable
{
	private String username = "";
	
	private int x = 0;
	private int y = 0;
	
	private int row = 0;
	private int col = 0;
	
	private int tileHeight = 24; // height of the tiles of the room
	
	private int xSpeed = 8;
	private int ySpeed = 4;
	
	private AStarPathfinder pathfinder = new AStarPathfinder(); // pathfinder
	private ArrayList<Tile> path = new ArrayList<Tile>(); // path to sail
	private int colDiff = 0; // 1 or greater if horizontal movement is necessary
	
	private Tile currentTile;
	
	// Define the images for this ship
	private AStarCharacterImage shipNorth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_n.png"));
	private AStarCharacterImage shipNorthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_nw.png"));
	private AStarCharacterImage shipWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_w.png"));
	private AStarCharacterImage shipSouthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_sw.png"));
	private AStarCharacterImage shipSouth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_s.png"));
	private AStarCharacterImage shipSouthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_se.png"));
	private AStarCharacterImage shipEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_e.png"));
	private AStarCharacterImage shipNorthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_blue_ne.png"));
	
	private AStarCharacterImage shipImage = shipSouthEast;
	private String currentDirection = "se"; // the direction the ship is currently facing

	private String shipColor = "blue"; // the color of the ship's sails (also the color of the team)

	private Rectangle boundingBox = new Rectangle(x, y, shipImage.getImage().getWidth(), shipImage.getImage().getHeight());

	public AStarShip() {}
	
	public AStarShip(String username)
	{
		this.username = username;
	}
	
	public AStarShip(String username, int row, int col)
	{
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
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
		boundingBox.y = y + tileHeight - shipImage.getImage().getHeight();
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

	public int getxSpeed() {
		return xSpeed;
	}

	public void setxSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
		
		changeShipImage(); // change the ship image based on the speeds
	}

	public int getySpeed() {
		return ySpeed;
	}

	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
		
		changeShipImage(); // change the ship image based on the speeds
	}
	
	// change the avatar image based upon the speeds
	private void changeShipImage()
	{
		if(xSpeed == 0 && ySpeed < 0) // north
		{
			shipImage = shipNorth;
			currentDirection = "n";
		}
		else if(xSpeed < 0 && ySpeed < 0) // north-west
		{
			shipImage = shipNorthWest;
			currentDirection = "nw";
		}
		else if(xSpeed < 0 && ySpeed == 0) // west
		{
			shipImage = shipWest;
			currentDirection = "w";
		}
		else if(xSpeed < 0 && ySpeed > 0) // south-west
		{
			shipImage = shipSouthWest;
			currentDirection = "sw";
		}
		else if(xSpeed == 0 && ySpeed > 0) // south
		{
			shipImage = shipSouth;
			currentDirection = "s";
		}
		else if(xSpeed > 0 && ySpeed > 0) // south-east
		{
			shipImage = shipSouthEast;
			currentDirection = "se";
		}
		else if(xSpeed > 0 && ySpeed == 0) // east
		{
			shipImage = shipEast;
			currentDirection = "e";
		}
		else if(xSpeed > 0 && ySpeed < 0) // north-east
		{
			shipImage = shipNorthEast;
			currentDirection = "ne";
		}
		
		// change the bounding box width and height
		boundingBox.width = shipImage.getImage().getWidth();
		boundingBox.height = shipImage.getImage().getHeight();
	}

	public void setColDiff(int colDiff) {
		this.colDiff = colDiff;
	}
	
	public int getColDiff() {
		return colDiff;
	}

	public ArrayList<Tile> getPath() {
		return path;
	}

	public void setPath(ArrayList<Tile> path) {
		this.path = path;
	}
	
	public BufferedImage getImage() {
		return shipImage.getImage();
	}
	
	/*public void setImage(ImageIcon characterImage) {
		this.characterImage = characterImage;
	}*/
	
	public void setCurrentTile(Tile currentTile)
	{
		if(currentTile != null)
		{
			this.row = currentTile.getRow();
			this.col = currentTile.getColumn();
			
			//this.x = currentTile.getX();
			//this.y = currentTile.getY();
		}
		this.currentTile = currentTile;
	}
	
	// snap the character's position to that of the current tile
	public void snapToCurrentTile()
	{
		if(currentTile != null)
		{
			this.x = currentTile.getX();
			this.y = currentTile.getY() - shipImage.getImage().getHeight() + tileHeight;
		}
	}
	
	public Tile getCurrentTile() {
		return currentTile;
	}
	
	public void removeTopmostPathStep()
	{
		if(path != null)
		{
			path.remove(0);
		}
	}
	
	public void clearPath()
	{
		if(path != null)
		{
			path.clear();
		}
	}
	
	// set the tiles used by the A* pathfinder
	public void setPathfinderTiles(HashMap<String,Tile> tiles) {pathfinder.setTiles(tiles);}
	
	// get the A* pathfinder
	public AStarPathfinder getPathfinder() {return pathfinder;}
	
	public Rectangle getBoundingBox() {return boundingBox;}

	// change the size of the avatar given the width and height of the room tiles
	public void changeAvatarSizeForTile(int tileWidth, int tileHeight)
	{
		// make sure the character is 87.5% the width of the tile
		// make sure the character height = (width) / 0.44094488188976377952755905511811 
		
		// set the tile height
		this.tileHeight = tileHeight;
		
		updateAvatarImages();
	}
	
	// figure out whether the character image is fully transparent at a given X and Y value
	public boolean isTransparentAt(int x, int y)
	{
		int pixel = shipImage.getImage().getRGB(x, y);
		int alpha = (pixel >> 24) & 0x000000FF; // bit shift by 24 and bitwise AND with 0x000000FF for the alpha value
		
		// check if the image is of a four-byte ABGR or INT_ARGB image
		if((shipImage.getImage().getType() == BufferedImage.TYPE_4BYTE_ABGR || shipImage.getImage().getType() == BufferedImage.TYPE_INT_ARGB) && alpha == 0)
		{
			// fully transparent, so return true
			return true;
		}
		
		return false;
	}
	
	// update the ship images
	public void updateShipImages()
	{
		shipNorth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_n.png"));
		shipNorthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_nw.png"));
		shipWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_w.png"));
		shipSouthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_sw.png"));
		shipSouth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_s.png"));
		shipSouthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_se.png"));
		shipEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_e.png"));
		shipNorthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_ne.png"));
		
		// figure out what the current avatar image should be
		shipImage.setImage(AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/ship_" + shipColor + "_" + currentDirection + ".png"));
	}
}
