// Animation.java by Matt Fritz
// November 10, 2009
// Controls the animation of various objects on the grid

package animations;

import java.util.HashMap;

public class Animation implements Runnable
{
	private String name; // name of the animation
	private int currentFrame = 0; // current frame of the animation
	private int totalFrames = 0; // total number of frames in the animation
	private HashMap<String,AnimationFrame> frames = new HashMap<String,AnimationFrame>(); // map of animation frames
	
	private int x = 0; // x-coordinate of the animation
	private int y = 0; // y-coordinate of the animation
	
	private Thread animationThread; // thread to run the animation
	
	public Animation()
	{
		
	}
	
	public Animation(String name)
	{
		super();
		this.name = name;
	}
	
	public Animation(String name, int totalFrames)
	{
		this(name);
		this.totalFrames = totalFrames;
	}
	
	public Animation(String name, int totalFrames, int x, int y)
	{
		this(name, totalFrames);
		this.x = x;
		this.y = y;
	}
	
	// start the thread
	public void start()
	{
		System.out.println("Animation.java - start()");
		animationThread = new Thread(this, name + " Animation Thread");
		animationThread.start();
	}
	
	// add a frame to the HashMap
	public void addFrame(int frameNum, AnimationFrame theFrame)
	{
		frames.put(name + "-" + frameNum, theFrame);
	}
	
	// return a frame
	public AnimationFrame getFrame(int frameNum)
	{
		return frames.get(name + "-" + frameNum);
	}
	
	// return the next frame
	public AnimationFrame getNextFrame()
	{
		return frames.get(name + "-" + currentFrame); // return the next frame
	}
	
	// remove a frame
	public void removeFrame(int frameNum)
	{
		frames.remove(name + "-" + frameNum);
	}
	
	// stop the animation
	public void stop()
	{
		animationThread.interrupt();
		animationThread = null;
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
			catch(Exception e) {System.out.println("Exception in Animation - " + e.getMessage());}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getTotalFrames() {
		return totalFrames;
	}

	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}

	public HashMap<String, AnimationFrame> getFrames() {
		return frames;
	}

	public void setFrames(HashMap<String, AnimationFrame> frames) {
		this.frames = frames;
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
}
