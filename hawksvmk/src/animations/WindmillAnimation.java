// WindmillAnimation.java by Matt Fritz
// November 10, 2009
// Test class for the windmill animation in the "Boot Hill" guest room

package animations;

import javax.swing.ImageIcon;

public class WindmillAnimation extends StationaryAnimation
{
	public WindmillAnimation()
	{
		super("windmill",8);
		
		// add the frames
		for(int i = 0; i < 8; i++)
		{
			addFrame(i, new AnimationFrame(i, new ImageIcon("img/gr4/windmill_" + i + ".jpg"), 65));
		}
		
		// set the X and Y position
		setX(100);
		setY(100);
	}
	
	public void startAnimation()
	{
		System.out.println("WindmillAnimation.java - startAnimation()");
		start();
	}
}
