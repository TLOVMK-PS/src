// AStarCharacter.java by Matt Fritz
// November 16, 2009
// Simple class to handle a character's movement on an A* path

package astar;

import interfaces.ContentRateable;
import interfaces.GridSortable;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import tiles.Tile;
import util.AppletResourceLoader;
import util.GameConstants;
import util.InventoryInfo;
import util.RatingSystem;
import util.StaticAppletData;

public class AStarCharacter implements Serializable, ContentRateable, GridSortable
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
	
	// avatar image arrays for the animations
	// Index 0 : stand pose for avatar direction
	// Indices 1 - 4 : walk frames for that avatar direction
	private AStarCharacterImage[] avatarAnimsNorth = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsNorthWest = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsWest = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsSouthWest = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsSouth = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsSouthEast = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsEast = new AStarCharacterImage[5];
	private AStarCharacterImage[] avatarAnimsNorthEast = new AStarCharacterImage[5];
	
	private final int ANIMATION_DELAY = 200; // delay (in milliseconds) between animation frames
	private String currentAnimationName = "stand"; // the current animation's name
	private int currentAnimationFrame = 0; // the current frame in the specified animation array
	private AvatarMovementAnimationThread movementThread = null; // the thread to handle avatar animations
	private boolean animating = false; // true if the avatar is currently running an animation
	
	private AStarCharacterImage[] animationImages = avatarAnimsSouthEast;
	private AStarCharacterImage characterImage = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_AVATAR_IMAGES + "male/male_avatar_se_64.png"));
	private String currentDirection = "se"; // the direction the avatar is currently facing
	
	// Strings for clothing IDs
	private String baseAvatarID = "base_0_0";
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
	
	public int getBaseY()
	{
		return y + tileHeight;
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
		// check to see which animation needs to be applied
		if(currentAnimationName.equals(GameConstants.CONST_WALK_ANIMATION))
		{
			if(xSpeed == 0 && ySpeed < 0) // north
			{
				animationImages = avatarAnimsNorth;
				currentDirection = "n";
			}
			else if(xSpeed < 0 && ySpeed < 0) // north-west
			{
				animationImages = avatarAnimsNorthWest;
				currentDirection = "nw";
			}
			else if(xSpeed < 0 && ySpeed == 0) // west
			{
				animationImages = avatarAnimsWest;
				currentDirection = "w";
			}
			else if(xSpeed < 0 && ySpeed > 0) // south-west
			{
				animationImages = avatarAnimsSouthWest;
				currentDirection = "sw";
			}
			else if(xSpeed == 0 && ySpeed > 0) // south
			{
				animationImages = avatarAnimsSouth;
				currentDirection = "s";
			}
			else if(xSpeed > 0 && ySpeed > 0) // south-east
			{
				animationImages = avatarAnimsSouthEast;
				currentDirection = "se";
			}
			else if(xSpeed > 0 && ySpeed == 0) // east
			{
				animationImages = avatarAnimsEast;
				currentDirection = "e";
			}
			else if(xSpeed > 0 && ySpeed < 0) // north-east
			{
				animationImages = avatarAnimsNorthEast;
				currentDirection = "ne";
			}
		}
		else if(currentAnimationName.equals(GameConstants.CONST_STAND_ANIMATION))
		{
			// set the character's image to be the same direction, but standing
			//characterImage.setImage(animationImages[0].getImage());
		}
		
		// check to make sure the character image exists
		if(characterImage != null)
		{
			// change the bounding box width and height
			boundingBox.width = characterImage.getImage().getWidth();
			boundingBox.height = characterImage.getImage().getHeight();
		}
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
	
	public BufferedImage getImage() {
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
	
	// snap the character's position to that of the current tile
	public void snapToCurrentTile()
	{
		if(currentTile != null)
		{
			this.x = currentTile.getX();
			this.y = currentTile.getY() - characterImage.getImage().getHeight() + tileHeight;
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
	
	public void addCredits(long credits) {this.credits += credits;}
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
		
		// check to see if the email exists and assign the default address if it doesn't
		if(email.equals("")) {email = GameConstants.CONST_DEFAULT_EMAIL;}
		
		// re-build the animation images for the specified directions
		avatarAnimsNorth = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[0], tileWidth);
		avatarAnimsNorthEast = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[1], tileWidth);
		avatarAnimsEast = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[2], tileWidth);
		avatarAnimsSouthEast = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[3], tileWidth);
		avatarAnimsSouth = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[4], tileWidth);
		avatarAnimsSouthWest = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[5], tileWidth);
		avatarAnimsWest = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[6], tileWidth);
		avatarAnimsNorthWest = updateAnimationImagesGroup(GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY[7], tileWidth);
		
		// figure out what the current avatar image should be
		characterImage.setImage(AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_AVATAR_IMAGES + email + "/" + GameConstants.PATH_AVATAR_IMAGES_PREFIX + "_" + currentDirection + "_" + tileWidth + ".png"));
	}
	
	// re-build a set of avatar animation images given the specified direction
	private AStarCharacterImage[] updateAnimationImagesGroup(String direction, int tileWidth)
	{
		int arraySize = GameConstants.CONST_CHARACTER_ANIMS_ARRAY.length; // figure out the array size
		AStarCharacterImage[] animationImages = new AStarCharacterImage[arraySize]; // create the initial array
		
		// iterate through the character anims constants array
		String animation = "_";
		for(int i = 0; i < arraySize; i++)
		{
			// get the next animation from the constants array
			animation = GameConstants.CONST_CHARACTER_ANIMS_ARRAY[i];
			
			// add the image to the animation images array
			animationImages[i] = new AStarCharacterImage(AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_AVATAR_IMAGES + email + "/" + GameConstants.PATH_AVATAR_IMAGES_PREFIX + "_" + direction + animation + tileWidth + ".png"));
		}
		
		// return the images
		return animationImages;
	}
	
	// start the avatar's animation thread with the specified animation set
	public void startAnimation(String animationName)
	{	
		// check to make sure an animation is not currently running
		if(!animating)
		{
			// set the animation name
			currentAnimationName = animationName;
			
			// set the current frame depending on the animation
			if(animationName.equals(GameConstants.CONST_STAND_ANIMATION))
			{
				// start right at the beginning of the animation frames
				currentAnimationFrame = 0;
			}
			else if(animationName.equals(GameConstants.CONST_WALK_ANIMATION))
			{
				// start right after the stand animation
				currentAnimationFrame = GameConstants.CONST_STAND_ANIMATION_FRAMES;
			}

			// an animation is now running
			animating = true;

			// start the thread
			movementThread = new AvatarMovementAnimationThread(animationName);
			movementThread.start();
		}
	}
	
	// stop the avatar's animation thread
	public void stopAnimation()
	{
		// check to make sure an animation is running
		if(animating)
		{
			// the animation is no longer running
			animating = false;
			
			// interrupt the thread
			movementThread.interrupt();
			movementThread = null;
			
			// set the proper image for the stand frame
			currentAnimationName = GameConstants.CONST_STAND_ANIMATION;
			//characterImage.setImage(animationImages[0].getImage());
		}
	}
	
	// define a class to handle the avatar's animation sequences
	class AvatarMovementAnimationThread extends Thread implements Serializable
	{
		private String animationName = ""; // the name of the animation
		
		public AvatarMovementAnimationThread(String animationName)
		{
			this.animationName = animationName;
		}
		
		public void run()
		{
			while(animating)
			{
				// check the animation name first
				if(animationName.equals(GameConstants.CONST_WALK_ANIMATION))
				{
					// check to make sure the animation hasn't gone over the frame index boundary
					if(currentAnimationFrame == (GameConstants.CONST_STAND_ANIMATION_FRAMES + GameConstants.CONST_WALK_ANIMATION_FRAMES - 1))
					{
						// reset the animation frame to the beginning of the animation (right after the stand animation)
						currentAnimationFrame = GameConstants.CONST_STAND_ANIMATION_FRAMES;
					}
					else
					{
						// increment the current animation frame
						currentAnimationFrame++;
					}
					
					System.out.println("WALK FRAME: " + currentAnimationFrame);
					
					// set the current character image for the current animation
					characterImage.setImage(animationImages[currentAnimationFrame].getImage());
				}
				
				// sleep the thread for the specified amount of time
				try
				{
					Thread.sleep(ANIMATION_DELAY);
				}
				catch(InterruptedException e) {}
			}
		}
	}
}
