// AStarCharacter.java by Matt Fritz
// November 16, 2009
// Simple class to handle a character's movement on an A* path

package astar;

import interfaces.ContentRateable;

import java.awt.Image;
import java.awt.Rectangle;
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
	
	// avatar images for the eight directions
	private ImageIcon avatarNorth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_n_64.png");
	private ImageIcon avatarNorthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_nw_64.png");
	private ImageIcon avatarWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_w_64.png");
	private ImageIcon avatarSouthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_sw_64.png");
	private ImageIcon avatarSouth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_s_64.png");
	private ImageIcon avatarSouthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_se_64.png");
	private ImageIcon avatarEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_e_64.png");
	private ImageIcon avatarNorthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_ne_64.png");
	
	private ImageIcon characterImage = avatarSouthEast;
	
	private Rectangle boundingBox = new Rectangle(x, y, characterImage.getIconWidth(), characterImage.getIconHeight());
	
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
		boundingBox.y = y + tileHeight - characterImage.getIconHeight();
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
		}
		else if(xSpeed < 0 && ySpeed < 0) // north-west
		{
			characterImage = avatarNorthWest;
		}
		else if(xSpeed < 0 && ySpeed == 0) // west
		{
			characterImage = avatarWest;
		}
		else if(xSpeed < 0 && ySpeed > 0) // south-west
		{
			characterImage = avatarSouthWest;
		}
		else if(xSpeed == 0 && ySpeed > 0) // south
		{
			characterImage = avatarSouth;
		}
		else if(xSpeed > 0 && ySpeed > 0) // south-east
		{
			characterImage = avatarSouthEast;
		}
		else if(xSpeed > 0 && ySpeed == 0) // east
		{
			characterImage = avatarEast;
		}
		else if(xSpeed > 0 && ySpeed < 0) // north-east
		{
			characterImage = avatarNorthEast;
		}
		
		// change the bounding box width and height
		boundingBox.width = characterImage.getIconWidth();
		boundingBox.height = characterImage.getIconHeight();
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
	
	public void setImage(ImageIcon characterImage) {
		this.characterImage = characterImage;
	}
	
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
	
	// change the size of the avatar given the width and height of the room tiles
	public void changeAvatarSizeForTile(int tileWidth, int tileHeight)
	{
		// make sure the character is 87.5% the width of the tile
		// make sure the character height = (width) / 0.44094488188976377952755905511811 
		if(tileWidth == 64 && tileHeight == 32)
		{
			// 64x32 tile
			avatarNorth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_n_64.png");
			avatarNorthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_nw_64.png");
			avatarWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_w_64.png");
			avatarSouthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_sw_64.png");
			avatarSouth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_s_64.png");
			avatarSouthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_se_64.png");
			avatarEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_e_64.png");
			avatarNorthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_ne_64.png");
			
			// scale the current image for consistency
			characterImage = new ImageIcon(characterImage.getImage().getScaledInstance(56, 127, Image.SCALE_DEFAULT));
		}
		else if(tileWidth == 48 && tileHeight == 24)
		{
			// 48x24 tile
			avatarNorth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_n_48.png");
			avatarNorthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_nw_48.png");
			avatarWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_w_48.png");
			avatarSouthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_sw_48.png");
			avatarSouth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_s_48.png");
			avatarSouthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_se_48.png");
			avatarEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_e_48.png");
			avatarNorthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_ne_48.png");
			
			// scale the current image for consistency
			characterImage = new ImageIcon(characterImage.getImage().getScaledInstance(42, 95, Image.SCALE_DEFAULT));
		}
		else if(tileWidth == 32 && tileHeight == 16)
		{
			// 32x16 tile
			avatarNorth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_n_32.png");
			avatarNorthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_nw_32.png");
			avatarWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_w_32.png");
			avatarSouthWest = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_sw_32.png");
			avatarSouth = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_s_32.png");
			avatarSouthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_se_32.png");
			avatarEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_e_32.png");
			avatarNorthEast = AppletResourceLoader.getImageFromJar("img/avatars/male/male_avatar_ne_32.png");
			
			// scale the current image for consistency
			characterImage = new ImageIcon(characterImage.getImage().getScaledInstance(28, 63, Image.SCALE_DEFAULT));
		}
		
		// set the tile height
		this.tileHeight = tileHeight;
	}
}
