// Explosion.java by Matt Fritz
// November 5, 2010
// Generic class to describe explosions in Pirates of the Caribbean

package games.pirates;

import java.awt.image.BufferedImage;

public class Explosion
{
	private boolean active = true; // boolean describing whether this explosion is active
	
	// image arrays for fire and smoke
	private BufferedImage fireImages[];
	private BufferedImage smokeImages[];
	
	// total frames for fire and smoke images
	private int fireFrames = 0;
	private int smokeFrames = 0;
	
	// current frame for the fire and smoke images
	private int currentFireFrame = 0;
	private int currentSmokeFrame = 0;
	
	private int frameThreshold = 2; // set to a higher number to make the animation slower (i.e. every X frames, the animation will change to the next image)
	
	private int x = 0;
	private int y = 0;
	
	public Explosion(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public BufferedImage[] getFireImages() {
		return fireImages;
	}
	
	public void setFireImages(BufferedImage[] fireImages) {
		this.fireImages = fireImages;
		this.fireFrames = fireImages.length;
	}
	
	public BufferedImage[] getSmokeImages() {
		return smokeImages;
	}
	
	public void setSmokeImages(BufferedImage[] smokeImages) {
		this.smokeImages = smokeImages;
		this.smokeFrames = smokeImages.length;
	}
	
	public int getFireFrames() {
		return fireFrames;
	}
	
	public int getSmokeFrames() {
		return smokeFrames;
	}
	
	public void nextFireFrame()
	{
		currentFireFrame++;
	}
	
	public void nextSmokeFrame()
	{
		currentSmokeFrame++;
	}
	
	// get the current frame image for the fire animation
	public BufferedImage getFireFrame()
	{
		// make sure the animation is active
		if(active)
		{
			// check to make sure there are actually fire frames
			if(fireFrames > 0)
			{
				// increment the frame counter
				nextFireFrame();
				
				// check if we have passed the maximum amount of images
				if((currentFireFrame / frameThreshold) > fireFrames - 1)
				{
					// check to see if there is a smoke animation
					if(smokeFrames == 0)
					{
						// there are no smoke frames to display, so end this animation
						active = false;
					}
					return null;
				}
				else
				{	
					// get the current fire frame image
					return fireImages[currentFireFrame / frameThreshold];
				}
			}
		}
		
		return null;
	}
	
	// get the current frame image for the smoke animation
	public BufferedImage getSmokeFrame()
	{
		// check to make sure the animation is active
		if(active)
		{
			// make sure there are smoke frames
			if(smokeFrames > 0)
			{
				// increment the smoke frame counter
				nextSmokeFrame();
				
				// check to see if we have exceeded the maximum amount of images
				if((currentSmokeFrame / frameThreshold) > smokeFrames - 1)
				{
					// the animation is now inactive since smoke lasts longer than fire
					active = false;
					return null;
				}
				else
				{
					// get the current smoke frame image
					return smokeImages[currentSmokeFrame / frameThreshold];
				}
			}
		}
		
		return null;
	}
}
