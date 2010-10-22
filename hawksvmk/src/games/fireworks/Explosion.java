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
	
	private int imageX = 0;
	private int imageY = 0;
	private int textX = 0;
	private int textY = 0;
	
	private int targetWidth = 0;
	private int targetHeight = 0;
	
	private int currentWidth = 0;
	private int currentHeight = 0;
	
	private int scaleUnitWidth = 4;
	private int scaleUnitHeight = 4;
	
	private boolean active = true;
	
	private BufferedImage explosionImages[] = null;
	private int imageIndex = 0;
	private Image scaledImage = null;
	
	private BufferedImage scoreTextImage = null;
	
	public Explosion(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setScoreTextImage(BufferedImage scoreTextImage)
	{
		this.scoreTextImage = scoreTextImage;
	}
	
	public BufferedImage getScoreTextImage()
	{
		return scoreTextImage;
	}
	
	public void setExplosionImages(BufferedImage explosionImages[])
	{
		this.explosionImages = explosionImages;
		this.targetWidth = explosionImages[0].getWidth();
		this.targetHeight = explosionImages[0].getHeight();
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
			explosionImages = null;
			scaledImage = null;
		}
		else
		{
			// expand the explosion
			currentWidth += scaleUnitWidth;
			currentHeight += scaleUnitHeight;
			
			// get a scaled version of the original image corresponding to the image array index (10....0)
			imageIndex = (targetWidth / currentWidth) - 1;
			if(imageIndex < 0) {imageIndex = 0;}
			if(imageIndex >= explosionImages.length) {imageIndex = explosionImages.length - 1;}
			
			// attempt to center the explosion as it expands
			imageX = x - (explosionImages[imageIndex].getWidth() / 2);
			imageY = y - (explosionImages[imageIndex].getHeight() / 2);
			
			textX = x - (scoreTextImage.getWidth() / 2);
			textY = y - (scoreTextImage.getHeight() / 2);
		}
	}
	
	public BufferedImage getExplosionImage() {
		return explosionImages[imageIndex];
	}
	
	public Image getScaledImage()
	{
		return scaledImage;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public int getImageX() {
		return imageX;
	}
	
	public int getImageY() {
		return imageY;
	}
	
	public int getTextX() {
		return textX;
	}
	
	public int getTextY() {
		return textY;
	}
}
