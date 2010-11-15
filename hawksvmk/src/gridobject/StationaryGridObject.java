// StationaryGridObject.java by Matt Fritz
// November 11, 2009
// Handles stationary objects on the room grid

package gridobject;

import java.awt.image.BufferedImage;

import util.AppletResourceLoader;

public class StationaryGridObject extends GridObject
{
	public StationaryGridObject()
	{
		super();
	}
	
	public StationaryGridObject(String name, int x, int y, BufferedImage image)
	{
		super(name, x, y, image);
	}
	
	// return a new StationaryGridObject given a String input
	public static StationaryGridObject fromString(String line)
	{
		String name = "";
		int x = 0;
		int y = 0;
		BufferedImage image = null;
		
		// split the line at the comma characters
		String[] theArray = line.split(",");
		
		// assign all the fields given the elements of the array
		name = theArray[0];
		x = Integer.parseInt(theArray[1]);
		y = Integer.parseInt(theArray[2]);
		image = AppletResourceLoader.getBufferedImageFromJar(theArray[3]);
		
		// return the object
		StationaryGridObject obj = new StationaryGridObject(name,x,y,image);
		obj.setImagePath(theArray[3]);
		return obj;
	}
}
