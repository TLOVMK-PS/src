// GamePirates.java by Matt Fritz
// October 31, 2010 (Happy Halloween!)
// Handles the running of the Pirates of the Caribbean game

package games.pirates;

import games.InternalGame;

import java.awt.Graphics;
import java.awt.Image;

public class GamePirates extends InternalGame implements Runnable
{	
	private Thread gameThread = null; // thread to handle the running of the game
	
	private int mouseX = -20;
	private int mouseY = -20;
	
	private Image offscreen = null; // offscreen buffer
	private Graphics bufferGraphics = null; // graphics object for the offscreen buffer
	
	public GamePirates()
	{
		// assign the properties of the game
		super("pirates", "Pirates of the Caribbean", 3, 3);
	}
	
	public void run()
	{
		
	}
}
