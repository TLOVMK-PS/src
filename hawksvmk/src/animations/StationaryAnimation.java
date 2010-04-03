// StationaryAnimation.java by Matt Fritz
// November 10, 2009
// Controls the animation of various objects on the grid

package animations;

import java.util.HashMap;

public class StationaryAnimation extends Animation implements Runnable
{	
	// start the thread
	public void start()
	{
		System.out.println("StationaryAnimation.java - start()");
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
			
			// sleep
			try
			{
				Thread.sleep(getNextFrame().getDelay());
			}
			catch(Exception e) {System.out.println("Exception in StationaryAnimation - " + e.getMessage());}
		}
	}
	
	// stop the animation
	public void stop()
	{
		animationThread.interrupt();
		animationThread = null;
	}
}
