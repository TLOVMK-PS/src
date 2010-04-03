// Animation.java by Matt Fritz
// April 1, 2010
// Super-class for all Animation classes

package animations;

import java.util.HashMap;

public class Animation
{
	protected String name; // name of the animation
	protected int currentFrame = 0; // current frame of the animation
	protected int totalFrames = 0; // total number of frames in the animation
	protected HashMap<String,AnimationFrame> frames = new HashMap<String,AnimationFrame>(); // map of animation frames
	
	protected Thread animationThread; // thread to run the animation
	
	protected String path = "";
	
	protected int x = 0;
	protected int y = 0;
	
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
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
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
	
	public void start() {}
	public void stop() {}
}
