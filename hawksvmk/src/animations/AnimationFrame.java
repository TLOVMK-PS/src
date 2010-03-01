// AnimationFrame.java by Matt Fritz
// November 10, 2009
// Controls a single frame of an animation

package animations;

import java.awt.Image;

import javax.swing.ImageIcon;

public class AnimationFrame
{
	private int frameNum = 0; // frame number
	private ImageIcon image; // frame image
	private int delay = 20; // milliseconds until next frame
	
	public AnimationFrame(int frameNum, ImageIcon image, int delay)
	{
		this.frameNum = frameNum;
		this.image = image;
		this.delay = delay;
	}

	public int getFrameNum() {
		return frameNum;
	}

	public void setFrameNum(int frameNum) {
		this.frameNum = frameNum;
	}

	public Image getImage() {
		return image.getImage();
	}
	
	public ImageIcon getImageIcon() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}
