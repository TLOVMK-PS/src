// Explosion.java by Matt Fritz
// October 15, 2010
// Handles the display of an explosion after a firework is burst

package games.fireworks;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Explosion
{	
	private int x = 0;
	private int y = 0;
	
	private int targetWidth = 0;
	private int targetHeight = 0;
	
	private int currentWidth = 0;
	private int currentHeight = 0;
	
	private int scaleUnitWidth = 2;
	private int scaleUnitHeight = 2;
	
	private boolean active = true;
	
	private BufferedImage explosionImage = null;
	private Image scaledImage = null;
	
	public Explosion(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setExplosionImage(BufferedImage explosionImage)
	{
		this.explosionImage = explosionImage;
		this.targetWidth = explosionImage.getWidth();
		this.targetHeight = explosionImage.getHeight();
		this.currentWidth = (int)(this.targetWidth * 0.1);
		this.currentHeight = (int)(this.targetHeight * 0.1);
	}
	
	// expand the explosion image
	public void expand()
	{
		if(currentWidth >= targetWidth && currentHeight >= targetHeight)
		{
			// it's at its largest, so stop the explosion
			active = false;
			explosionImage = null;
			scaledImage = null;
		}
		else
		{
			// expand the explosion
			currentWidth += scaleUnitWidth;
			currentHeight += scaleUnitHeight;
			
			// attempt to center the explosion as it expands
			x -= scaleUnitWidth / 2;
			y -= scaleUnitHeight / 2;
			
			// get a scaled version of the original image
			// TODO: Causes frames to be lost on painting.  Figure out another way to show expansion.
			scaledImage = explosionImage.getScaledInstance(currentWidth, currentHeight, BufferedImage.SCALE_FAST);
		}
	}
	
	public BufferedImage getExplosionImage() {
		return explosionImage;
	}
	
	public Image getScaledImage()
	{
		return scaledImage;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
