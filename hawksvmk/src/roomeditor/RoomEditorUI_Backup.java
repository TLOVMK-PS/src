// RoomEditorUI.java by Matt Fritz
// March 25, 2009
// Class for the Room Editor window

package roomeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class RoomEditorUI_Backup extends JFrame
{
	private int graphicsDelay = 20; // milliseconds between each frame
	
	ImageIcon backgroundImage = new ImageIcon("tiles_img/test_room_image.png");
	ImageIcon nogoTile = new ImageIcon("tiles_img/tile_nogo.png");
	ImageIcon reticleTile = new ImageIcon("tiles_img/tile_selector.png");
	
	int tileWidth = 64;
	int tileHeight = 32;
	
	int tileRows = 19; // 19
	int tileColumns = 13; // 13
	
	int mouseX = 0;
	int mouseY = 0;
	
	Graphics bufferGraphics;
	Image offscreen;
	
	RoomEditorUI_Backup myRoomEditorWindow;
	
	public RoomEditorUI_Backup() {}
	
	public void loadRoomEditorUI()
	{
		// set general properties of the main window frame
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(null);
        
        this.addMouseListener(new MouseAdapter()
        {
        	//public void mouseReleased(MouseEvent e)
        	//{
        		
        	//}
        });
        
        this.addMouseMotionListener(new MouseMotionAdapter()
        {
        	public void mouseMoved(MouseEvent e)
        	{
        		mouseX = e.getX();
        		mouseY = e.getY();
        		
        		// get the grid row & column equivalents
        		int gridX = (mouseX / tileWidth);
        		int gridY = (mouseY / tileHeight);
        		
        		System.out.println("Mouse X: " + mouseX + " - Mouse Y: " + mouseY + "Grid X: " + gridX + " - Grid Y: " + gridY);
        		
        		// snap the X and Y coords to the grid
        		mouseX = gridX * (tileWidth) + (tileWidth / 2);
        		mouseY = gridY * (tileHeight) + (tileHeight / 2);
        	}
        	public void mouseDragged(MouseEvent e)
        	{
        		
        	}
        });
        
        // pack the window and display it
        this.setName("Room Editor");
        this.setTitle("Room Editor");
        this.pack();
        this.setVisible(true);
        
        myRoomEditorWindow = this;
        
        // set-up the double-buffering objects
        offscreen = createImage(800, 600);
        bufferGraphics = offscreen.getGraphics();
        
        // start the graphics loop
        graphicsLoop();
	}
	
	public void paint(Graphics g)
	{
		// make sure the buffer exists
		if(bufferGraphics != null)
		{
			// clear the screen
			bufferGraphics.clearRect(0, 0, 800, 600);
		
			bufferGraphics.drawImage(backgroundImage.getImage(), 0, 0, new MovementImageObserver(this));
			
			// draw the grid
			for(int i = 0; i < tileRows; i++)
			{
				for(int j = 0; j < tileColumns; j++)
				{
					bufferGraphics.drawImage(nogoTile.getImage(), j * tileWidth, i * tileHeight, new MovementImageObserver(this));
					bufferGraphics.drawImage(nogoTile.getImage(), j * tileWidth - (tileWidth / 2), i * tileHeight - (tileHeight / 2), new MovementImageObserver(this));
					
					bufferGraphics.setColor(Color.BLACK);
			        bufferGraphics.drawString(j + " - " + i,j * tileWidth, i * tileHeight);
				}
			}
			
			bufferGraphics.drawImage(reticleTile.getImage(), mouseX - (tileWidth / 2), mouseY - (tileHeight / 2), new MovementImageObserver(this));
		
			// draw the offscreen image to the screen like a normal image.
	        // Since offscreen is the screen width we start at 0,0.
	        g.drawImage(offscreen,0,0,new MovementImageObserver(this));
		}
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}
	
	public void graphicsLoop()
	{
		while(this != null)
		{
			paint(this.getGraphics());
			try
			{
				Thread.sleep(graphicsDelay);
			}
			catch(Exception e) {}
		}
	}
}

class MovementImageObserver implements ImageObserver
{
	RoomEditorUI_Backup theWindow;
	public MovementImageObserver(RoomEditorUI_Backup theWindow) {this.theWindow = theWindow;}
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
	{
		//System.out.println("Image updated");
		theWindow.repaint();
	    return true;
	}
}