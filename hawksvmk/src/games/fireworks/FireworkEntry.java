// FireworkEntry.java by Matt Fritz
// October 14, 2010
// Handle an entry representation for a Firework object

package games.fireworks;

public class FireworkEntry
{
	private int x = 0;
	private int y = 0;
	private int targetX = 0;
	private int targetY = 0;
	private int xSpeed = 0;
	private int ySpeed = 0;
	private int fireworkNumber = 0;
	private int delay = 0;
	
	public FireworkEntry(int x, int y, int targetX, int targetY, int xSpeed, int ySpeed, int fireworkNumber, int delay)
	{
		this.x = x;
		this.y = y;
		this.targetX = targetX;
		this.targetY = targetY;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.fireworkNumber = fireworkNumber;
		this.delay = delay;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public int getXSpeed() {
		return xSpeed;
	}

	public int getYSpeed() {
		return ySpeed;
	}

	public int getFireworkNumber() {
		return fireworkNumber;
	}
	
	public int getDelay() {
		return delay;
	}
}
