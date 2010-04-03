// MovingAnimation.java by Matt Fritz
// April 1, 2010
// Controls the x-y coordinate animation of various objects on the grid

package animations;

import java.util.HashMap;

public class MovingAnimation extends Animation implements Runnable
{
	// start the thread
	public void start()
	{
		System.out.println("MovingAnimation.java - start()");
		animationThread = new Thread(this, name + " Animation Thread");
		animationThread.start();
	}
	
	// run the animation
	public void run()
	{
		while(animationThread != null)
		{
			if(currentFrame >= (totalFrames - 1))
			{
				currentFrame = 0; // set the current frame to 0 so it loops back
			}
			else
			{
				currentFrame++; // increment the frame count
			}
			
			// change the coordinate positions and sleep
			try
			{
				this.x = getNextFrame().getX();
				this.y = getNextFrame().getY();
				Thread.sleep(getNextFrame().getDelay());
			}
			catch(Exception e) {System.out.println("Exception in MovingAnimation - " + e.getMessage());}
		}
	}
	
	// stop the animation
	public void stop()
	{
		animationThread.interrupt();
		animationThread = null;
	}
}
