// Cannonball.java by Matt Fritz
// November 4, 2010
// Handles a cannonball fired by a ship in Pirates of the Caribbean
// MOAR PIRATES!

package games.pirates;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Cannonball
{
	// image for the cannonball
	private BufferedImage cannonballImage = null;
	
	// cannonball types
	public static final int TYPE_CANNONBALL = 0;
	public static final int TYPE_GHOST_CANNONBALL = 1;
	private int type = TYPE_CANNONBALL;
	
	// position for the cannonball
	private int x = 0;
	private int y = 0;
	
	// speeds for the cannonball
	private int speedX = 0;
	private int speedY = 0;
	
	// measurements of the cannonball image
	private int width = 0;
	private int height = 0;
	
	// bounding box for the cannonball
	private Rectangle boundingBox = new Rectangle(0,0,0,0);
	
	// descriptive data for the cannonball
	private String firedBy = "";
	private String firedByColor = "";
	
	public Cannonball(String firedBy, String firedByColor)
	{
		this.firedBy = firedBy;
		this.firedByColor = firedByColor;
	}
	
	public BufferedImage getCannonballImage() {return cannonballImage;}
	public void setCannonballImage(BufferedImage cannonballImage)
	{
		this.cannonballImage = cannonballImage;
		
		width = cannonballImage.getWidth();
		height = cannonballImage.getHeight();
	}
	
	public int getType() {return type;}
	public void setType(int type)
	{
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}
	
	// move the cannonball
	public void moveCannonball()
	{
		x += speedX;
		y += speedY;
		
		// set the bounding box
		boundingBox.setBounds(x, y, width, height);
	}
	
	// return the bounding box for the cannonball
	public Rectangle getBoundingBox()
	{
		return boundingBox;
	}
}
