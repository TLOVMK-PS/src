// ClickableArea.java by Matt Fritz
// November 11, 2010
// Define a clickable rectangle for the Room Viewer grid

package clickable;

import java.awt.Rectangle;

public class ClickableArea extends Rectangle
{
	private String action = ""; // the action associated with a click in this rectangle
	
	public ClickableArea()
	{
		super();
	}
	
	public ClickableArea(int x, int y, int width, int height, String action)
	{
		super(x,y,width,height);
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String toString()
	{
		return x + "," + y + "," + width + "," + height + "," + action;
	}
	
	// create a new ClickableArea given a String
	// used as a helper function when loading from a file
	public static ClickableArea fromString(String string)
	{
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		String action = "";
		String items[] = string.split(",");
		
		x = Integer.parseInt(items[0]);
		y = Integer.parseInt(items[1]);
		width = Integer.parseInt(items[2]);
		height = Integer.parseInt(items[3]);
		action = items[4];
		
		return new ClickableArea(x,y,width,height,action);
	}
}
