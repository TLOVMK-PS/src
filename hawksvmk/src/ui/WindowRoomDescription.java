// WindowRoomDescription.java by Matt Fritz
// November 12, 2009
// Handles the "Room Description" window that comes up when you press the "I" button on the toolbar

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import util.AppletResourceLoader;

public class WindowRoomDescription implements ImageObserver
{
	private String roomName;
	private String roomDescription;
	private Font textFont;
	private Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int roomTitleX = 125; // x-position of the roomName text
	
	private Rectangle exitButtonRect = new Rectangle(301, 5, 16, 18); // bounds of the "X" button relative to the image
	
	private Graphics windowGraphics;
	private BufferedImage drawingSurface;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/room_description_window.png");
	
	boolean visible = false;
	
	public WindowRoomDescription(Font textFont, Font textFontBold, String roomName, String roomDescription, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		
		this.roomName = roomName;
		this.roomDescription = roomDescription;
		
		this.x = x;
		this.y = y;
	}
	
	// set the drawing surface with a blank image and turn it into a BufferedImage
	// that can handle alpha transparency
	public void setDrawingSurface(Image drawingSurface)
	{
		this.drawingSurface = new BufferedImage(drawingSurface.getWidth(this), drawingSurface.getHeight(this), BufferedImage.TYPE_INT_ARGB);
	}
	
	public Image getImage()
	{
		if(drawingSurface != null)
		{
			windowGraphics = drawingSurface.getGraphics();
			
			// draw the base window image
			windowGraphics.drawImage(windowImage.getImage(), 0, 0, this);
			
			// draw the room name on the image
			windowGraphics.setFont(textFontBold);
			windowGraphics.setColor(Color.WHITE);
			windowGraphics.drawString(roomName, roomTitleX, 18);
			
			// draw the guide oval to find the x and y of the "X" button
			//windowGraphics.drawOval(299, 5, 18, 18);
			
			// draw the room description on the image
			windowGraphics.setFont(textFont);
			// check to see if there are newline characters
			if(roomDescription.contains("\n"))
			{
				String[] lines = roomDescription.split("\\n");
				for(int i = 0; i < lines.length; i++)
				{
					if(i == 0)
					{
						// draw the first line
						windowGraphics.drawString(lines[i], 6, 60);
					}
					else
					{
						// draw the n-th line with a (5 * i) space after the previous line
						windowGraphics.drawString(lines[i], 6, 60 + (10 * i) + (5 * i));
					}
				}
			}
			else
			{
				// no newline characters, so just draw the string
				windowGraphics.drawString(roomDescription, 6, 60);
			}
		}
		
		return drawingSurface;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public boolean isVisible() {return visible;}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}
	
	// get the "X" button rectangle relative to the image
	public Rectangle getExitButtonRectRelative()
	{
		return exitButtonRect;
	}
	
	// get the "X" button rectangle as absolute coordinates
	public Rectangle getExitButtonRectAbsolute()
	{
		Rectangle exitButtonAbsolute = new Rectangle(x + (int)exitButtonRect.getX(), y + (int)exitButtonRect.getY(), (int)exitButtonRect.getWidth(), (int)exitButtonRect.getHeight());

		return exitButtonAbsolute;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomDescription() {
		return roomDescription;
	}

	public void setRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setRoomTitleX(int roomTitleX) {
		this.roomTitleX = roomTitleX;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
	{
		//System.out.println("Image updated");
		//theWindow.repaint();
	    return true;
	}
}
