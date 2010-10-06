// AStarCharacter.java by Matt Fritz
// November 16, 2009
// Simple class to handle a character's movement on an A* path

package astar;

import interfaces.ContentRateable;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import tiles.Tile;
import util.AppletResourceLoader;
import util.InventoryInfo;
import util.RatingSystem;
import util.StaticAppletData;

public class AStarCharacter implements Serializable, ContentRateable
{
	private String username = "";
	private String email = "";
	
	private int x = 0;
	private int y = 0;
	
	private int row = 0;
	private int col = 0;
	
	private int tileHeight = 32; // height of the tiles of the room
	
	private int xSpeed = 8;
	private int ySpeed = 4;
	
	private AStarPathfinder pathfinder = new AStarPathfinder(); // pathfinder
	private ArrayList<Tile> path = new ArrayList<Tile>(); // path to walk
	private int colDiff = 0; // 1 or greater if horizontal movement is necessary
	
	private Tile currentTile;
	
	// TODO: Change these objects to use the current avatar's own images instead of the template images
	// avatar images for the eight directions
	private AStarCharacterImage avatarNorth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_n_64.png"));
	private AStarCharacterImage avatarNorthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_nw_64.png"));
	private AStarCharacterImage avatarWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_w_64.png"));
	private AStarCharacterImage avatarSouthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_sw_64.png"));
	private AStarCharacterImage avatarSouth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_s_64.png"));
	private AStarCharacterImage avatarSouthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_se_64.png"));
	private AStarCharacterImage avatarEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_e_64.png"));
	private AStarCharacterImage avatarNorthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/male/male_avatar_ne_64.png"));
	
	private AStarCharacterImage characterImage = avatarSouthEast;
	private String currentDirection = "se"; // the direction the avatar is currently facing
	
	// Strings for clothing IDs
	private String baseAvatarID = "base_0";
	private String shirtID = "shirt_0";
	private String shoesID = "shoes_0";
	private String pantsID = "pants_0";
	private String hatID = "hat_0";
	
	private Rectangle boundingBox = new Rectangle(x, y, characterImage.getImage().getWidth(), characterImage.getImage().getHeight());
	
	private long credits = 1000;
	private String signature = "";
	private int contentRatingIndex = 0; // 0: G, 1: PG, 2: PG-13, 3: M
	
	// displayed badges/pins
	private InventoryInfo displayedBadges[] = new InventoryInfo[StaticAppletData.MAX_DISPLAYABLE_BADGES];
	private InventoryInfo displayedPins[] = new InventoryInfo[StaticAppletData.MAX_DISPLAYABLE_PINS];
	
	public AStarCharacter() {}
	
	public AStarCharacter(String username)
	{
		this.username = username;
	}
	
	public AStarCharacter(String username, int row, int col)
	{
		this.username = username;
		this.row = row;
		this.col = col;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
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
		boundingBox.y = y + tileHeight - characterImage.getImage().getHeight();
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
		
		changeAvatarImage(); // change the avatar image based on the speeds
	}

	public int getySpeed() {
		return ySpeed;
	}

	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
		
		changeAvatarImage(); // change the avatar image based on the speeds
	}
	
	// change the avatar image based upon the speeds
	private void changeAvatarImage()
	{
		if(xSpeed == 0 && ySpeed < 0) // north
		{
			characterImage = avatarNorth;
			currentDirection = "n";
		}
		else if(xSpeed < 0 && ySpeed < 0) // north-west
		{
			characterImage = avatarNorthWest;
			currentDirection = "nw";
		}
		else if(xSpeed < 0 && ySpeed == 0) // west
		{
			characterImage = avatarWest;
			currentDirection = "w";
		}
		else if(xSpeed < 0 && ySpeed > 0) // south-west
		{
			characterImage = avatarSouthWest;
			currentDirection = "sw";
		}
		else if(xSpeed == 0 && ySpeed > 0) // south
		{
			characterImage = avatarSouth;
			currentDirection = "s";
		}
		else if(xSpeed > 0 && ySpeed > 0) // south-east
		{
			characterImage = avatarSouthEast;
			currentDirection = "se";
		}
		else if(xSpeed > 0 && ySpeed == 0) // east
		{
			characterImage = avatarEast;
			currentDirection = "e";
		}
		else if(xSpeed > 0 && ySpeed < 0) // north-east
		{
			characterImage = avatarNorthEast;
			currentDirection = "ne";
		}
		
		// change the bounding box width and height
		boundingBox.width = characterImage.getImage().getWidth();
		boundingBox.height = characterImage.getImage().getHeight();
	}
	
	public String getBaseAvatarID() {
		return baseAvatarID;
	}

	public void setBaseAvatarID(String baseAvatarID) {
		this.baseAvatarID = baseAvatarID;
	}

	public String getShirtID() {
		return shirtID;
	}

	public void setShirtID(String shirtID) {
		this.shirtID = shirtID;
	}

	public String getShoesID() {
		return shoesID;
	}

	public void setShoesID(String shoesID) {
		this.shoesID = shoesID;
	}

	public String getPantsID() {
		return pantsID;
	}

	public void setPantsID(String pantsID) {
		this.pantsID = pantsID;
	}

	public String getHatID() {
		return hatID;
	}

	public void setHatID(String hatID) {
		this.hatID = hatID;
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
	
	public Image getImage() {
		return characterImage.getImage();
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
	
	public void setCredits(long credits) {this.credits = credits;}
	public long getCredits() {return credits;}
	
	public void setSignature(String signature) {this.signature = signature;}
	public String getSignature() {return signature;}
	
	public void setContentRating(String contentRating)
	{
		contentRatingIndex = RatingSystem.getContentRatingIndex(contentRating);
	}
	public int getContentRatingIndex()
	{
		return contentRatingIndex;
	}
	public String getContentRatingAsString()
	{
		return RatingSystem.getContentRating(contentRatingIndex);
	}
	
	public void setDisplayedBadges(InventoryInfo[] displayedBadges) {this.displayedBadges = displayedBadges;}
	public InventoryInfo[] getDisplayedBadges() {return displayedBadges;}
	
	public void setDisplayedPins(InventoryInfo[] displayedPins) {this.displayedPins = displayedPins;}
	public InventoryInfo[] getDisplayedPins() {return displayedPins;}
	
	// TODO: Change this method to use the current avatar's own images instead of the template images
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
		int pixel = characterImage.getImage().getRGB(x, y);
		int alpha = (pixel >> 24) & 0x000000FF; // bit shift by 24 and bitwise AND with 0x000000FF for the alpha value
		
		// check if the image is of a four-byte ABGR or INT_ARGB image
		if((characterImage.getImage().getType() == BufferedImage.TYPE_4BYTE_ABGR || characterImage.getImage().getType() == BufferedImage.TYPE_INT_ARGB) && alpha == 0)
		{
			// fully transparent, so return true
			return true;
		}
		
		return false;
	}
	
	// update the avatar images in a hacked way
	// ONLY TO BE CALLED ON THE SERVER SIDE AFTER CLOTHING IS UPDATED
	public void updateAvatarImages()
	{
		int tileWidth = tileHeight * 2;
		
		avatarNorth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_n_" + tileWidth + ".png"));
		avatarNorthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_nw_" + tileWidth + ".png"));
		avatarWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_w_" + tileWidth + ".png"));
		avatarSouthWest = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_sw_" + tileWidth + ".png"));
		avatarSouth = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_s_" + tileWidth + ".png"));
		avatarSouthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_se_" + tileWidth + ".png"));
		avatarEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_e_" + tileWidth + ".png"));
		avatarNorthEast = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_ne_" + tileWidth + ".png"));
		
		// figure out what the current avatar image should be
		characterImage.setImage(AppletResourceLoader.getBufferedImageFromJar("img/avatars/" + email + "/avatar_" + currentDirection + "_" + tileWidth + ".png"));
	}
	
	// ==============================================================
	// GETTERS/SETTERS FOR THE AVATAR IMAGES
	// ==============================================================
	
	
}
