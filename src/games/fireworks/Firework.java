// Firework.java by Matt Fritz
// October 14, 2010
// Handles a single firework within the Fireworks game

package games.fireworks;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Firework
{
	private final int MAX_SCORE = 500; // applied twice to get to a maximum of 1000
	private int score = 0;
	
	private int x = 0;
	private int y = 0;
	private int startX = 0;
	private int startY = 0;
	private Rectangle boundingBox = new Rectangle();
	
	private int xSpeed = 0; // 0 - 5
	private int ySpeed = 0; // 0 - 5
	
	private int targetX = 0;
	private int targetY = 0;
	private final int X_THRESHOLD = 50; // distance from the target X value that the X speed will decrease
	private final int Y_THRESHOLD = 50; // distance from the target Y value that the Y speed will decrease
	
	private boolean active = true;
	private boolean flawless = false;
	
	private int fireworkNumber = 3;
	private BufferedImage fireworkImage = null;
	
	public Firework() {}
	public Firework(int x, int y, int targetX, int targetY, int xSpeed, int ySpeed, int fireworkNumber)
	{
		setX(x);
		setY(y);
		setTargetX(targetX);
		setTargetY(targetY);
		setXSpeed(xSpeed);
		setYSpeed(ySpeed);
		this.fireworkNumber = fireworkNumber;
	}
	
	// slow the firework by applying variable speed thresholds
	// e.g. the closer a firework gets to its target position, the slower it moves
	private void applySpeedThresholds()
	{
		if(targetX - x <= X_THRESHOLD)
		{
			// start to slow the X speed -> 5...4...3...2...1...
			xSpeed = (int)(Math.abs(targetX - x) * 0.1) + 1;
		}
		
		if(y - targetY <= Y_THRESHOLD)
		{
			// start to slow the Y speed -> 5...4...3...2...1...
			ySpeed = (int)((targetY - y) * 0.1) - 1;
		}
		
		// check if we're within the "Flawless" range
		if((Math.abs(targetX - x) <= (X_THRESHOLD / 2)) && (y - targetY <= (Y_THRESHOLD / 2)))
		{
			// this would be a "Flawless" if clicked
			flawless = true;
		}
	}
	
	// move the firework
	public void moveFirework(int speedFactor)
	{
		int xSpeed = this.xSpeed;
		int ySpeed = this.ySpeed;
		
		// check to see if we need to apply a speed damping factor based upon the amount of
		// explosions that are currently visible
		if(speedFactor != -1)
		{
			xSpeed /= speedFactor;
			ySpeed /= speedFactor;
			
			//if(xSpeed == 0 && this.xSpeed > 0 && (y - targetY <= Y_THRESHOLD)) {xSpeed = 1;}
			//if(xSpeed == 0 && this.xSpeed < 0 && (y - targetY <= Y_THRESHOLD)) {xSpeed = -1;}
			if(ySpeed == 0 && this.ySpeed < 0) {ySpeed = -1;}
		}
		
		// check to make sure the firework is active
		if(active)
		{
			// apply the speed thresholds if necessary
			if((Math.abs(targetX - x) <= X_THRESHOLD) || (y - targetY <= Y_THRESHOLD)) {applySpeedThresholds();}
			
			// check to see if we reached or overshot the target
			if(((x >= targetX && startX <= targetX) || (x <= targetX && startX >= targetX)) && y <= targetY)
			{
				// stop the firework
				stopFirework();
			}
			else
			{
				// only move the firework horizontally once it passes a certain height
				if((y - targetY <= (Y_THRESHOLD)))
				{
					if(startX < targetX)
					{
						x += xSpeed; // move right
					}
					else if(startX > targetX)
					{
						x -= xSpeed; // move left
					}
				}
				
				// move the firework vertically
				y += ySpeed;
				
				// update the bounding box
				boundingBox.x = x;
				boundingBox.y = y;
			}
		}
	}
	
	// stop the firework
	private void stopFirework()
	{
		// the firework is no longer active
		active = false;
		
		// remove the image
		fireworkImage = null;
	}
	
	// burst the firework
	public void burstFirework()
	{
		// apply the score based on how close the firework is to its target for its X and Y coordinates
		
		// snap the X and Y back if they overshot the target for scoring purposes
		if((x > targetX && startX <= targetX) || (x < targetX && startX >= targetX)) {x = targetX;}
		if(y < targetY) {y = targetY;}
		
		// calculate the score
		calculateScore();
		
		// stop the firework
		stopFirework();
	}
	
	// calculate the score for the firework burst given how close it was to its target
	private void calculateScore()
	{
		score = (int)((MAX_SCORE * (Math.min(x, targetX) / (double)Math.max(x, targetX))) + (MAX_SCORE * (targetY / (double)y)));
	}

	public BufferedImage getFireworkImage() {
		return fireworkImage;
	}

	public void setFireworkImage(BufferedImage fireworkImage) {
		this.fireworkImage = fireworkImage;
		boundingBox = new Rectangle(x,y,fireworkImage.getWidth(),fireworkImage.getHeight());
	}

	public int getScore() {
		return score;
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	public boolean isActive() {
		return active;
	}
	
	public boolean isFlawless() {
		return flawless;
	}

	public void setX(int x) {
		this.x = x;
		this.startX = x;
	}

	public void setY(int y) {
		this.y = y;
		this.startY = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public void setXSpeed(int speed) {
		xSpeed = speed;
	}

	public void setYSpeed(int speed) {
		ySpeed = speed;
	}
	
	public int getFireworkNumber() {
		return fireworkNumber;
	}
}
