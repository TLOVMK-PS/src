// GridView.java by Matt Fritz
// March 26, 2009
// Class that implements the grid portion of the Room Editor

package roomeditor;

import interfaces.GridViewable;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import animations.Animation;

import sounds.SoundPlayable;
import tiles.Tile;
import util.AppletResourceLoader;

public class GridView extends JLabel implements GridViewable
{
	private int graphicsDelay = 20; // milliseconds between each frame
	
	private AppletResourceLoader resourceLoader = new AppletResourceLoader(); // JAR resource loader
	
	String backgroundImagePath = "tiles_img/test_room_image.png";
	ImageIcon backgroundImage = resourceLoader.getImageFromJar(backgroundImagePath);
	
	ImageIcon nogoTileImage = new Tile().getNogoTileImage();
	ImageIcon walkTileImage = new Tile().getWalkTileImage();
	ImageIcon exitTileImage = new Tile().getExitTileImage();
	
	Tile currentTile = new Tile(0,0,Tile.TILE_WALK); // currently selected tile type
	
	ImageIcon reticleTile = resourceLoader.getImageFromJar("tiles_img/tile_selector.png");
	//ImageIcon reticleTile = new ImageIcon("img/furniture/beta/furni_0/furni_0_A.png");
	
	int tileWidth = 64;
	int tileHeight = 32;
	
	int tileRows = 38; // 19
	int tileColumns = 13; // 13
	
	int mouseX = 0;
	int mouseY = 0;
	int gridX = 0;
	int gridY = 0;
	
	boolean showGrid = true; // FALSE to hide the tile images
	
	boolean showExitTiles = true;
	boolean showNogoTiles = true;
	boolean showWalkTiles = true;
	
	Graphics bufferGraphics;
	Image offscreen;
	
	RoomEditorUI myRoomEditorWindow;
	
	HashMap<String,Tile> tilesMap = new HashMap<String,Tile>();
	
	private String tileTypeString = "walk";
	
	ArrayList<Animation> animations = new ArrayList<Animation>();
	ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>();
	
	public void loadGridView()
	{
		this.addMouseListener(new MouseAdapter()
        {
        	public void mouseReleased(MouseEvent e)
        	{	
        		// make sure we only process these events when the mouse
        		// is within the editing grid
        		if(mouseX < 0 || mouseX > 800) {return;}
        		if(mouseY < 0 || mouseY > 600) {return;}
        		
        		//System.out.println("CLICK AT Mouse X: " + mouseX + " - Mouse Y: " + mouseY + "Grid X: " + gridX + " - Grid Y: " + gridY);
        		
        		// put a different tile in the HashMap
        		if(showGrid == true)
        		{
        			// put a current tile in the HashMap
            		tilesMap.put(gridY + "-" + (gridX / 2), currentTile);
        		}
        	}
        });
        
        this.addMouseMotionListener(new MouseMotionAdapter()
        {
        	public void mouseMoved(MouseEvent e)
        	{
        		mouseX = e.getX();
        		mouseY = e.getY();
        		
        		// make sure we only process these events when the mouse
        		// is within the editing grid
        		if(mouseX < 0 || mouseX > 800) {return;}
        		if(mouseY < 0 || mouseY > 600) {return;}
        		
        		// get the grid row & column equivalents
        		gridX = (mouseX / (tileWidth / 2));
        		gridY = (mouseY / (tileHeight / 2));
        		
        		//System.out.println("Mouse X: " + mouseX + " - Mouse Y: " + mouseY + "Grid X: " + gridX + " - Grid Y: " + gridY);
        		
        		// snap the X and Y coords to the grid
        		if(gridX % 2 != 0 && gridY % 2 != 0)
        		{
        			// move it off the intersection
        			gridX -= 1; gridY -= 1;
        		}
        		else if(gridX % 2 != 0 && gridY % 2 == 0)
        		{
        			// move it off the intersection
        			gridY -= 1;
        		}
        		else if(gridX % 2 == 0 && gridY % 2 != 0)
        		{
        			// move it off the intersection
        			gridY -= 1;
        		}
        		
        		mouseX = gridX * (tileWidth / 2) + (tileWidth / 2);
        		mouseY = gridY * (tileHeight / 2) + (tileHeight / 2);
        		
        		if(e.getModifiers() == MouseEvent.CTRL_MASK && showGrid == true && showWalkTiles == true)
        		{
        			// put a "walk" tile in the HashMap
            		tilesMap.put(gridY + "-" + (gridX / 2), new Tile(gridY, (gridX / 2), Tile.TILE_WALK));
        		}
        		else if(e.getModifiers() == MouseEvent.SHIFT_MASK && showGrid == true && showNogoTiles == true)
        		{
        			// put a "nogo" tile on the HashMap
        			tilesMap.put(gridY + "-" + (gridX / 2), new Tile(gridY, (gridX / 2), Tile.TILE_NOGO));
        		}
        		
        		setCurrentTileType(tileTypeString); // set the current tile type and coords
        	}
        });
        
        // intialize the tilesMap
        initTilesMap();
        
        // start the graphics loop
        graphicsLoop();
	}
	
	// set-up the double buffering objects
	// called from RoomEditorUI
	public void setOffscreenImage(Image offscreen)
	{
		this.offscreen = offscreen;
		bufferGraphics = this.offscreen.getGraphics();
	}
	
	public void paint(Graphics g)
	{	
		// make sure the buffer exists
		if(bufferGraphics != null)
		{
			// clear the screen
			bufferGraphics.clearRect(0, 0, 800, 600);
		
			bufferGraphics.drawImage(backgroundImage.getImage(), 0, 0, new GridViewMovementImageObserver(this));
			
			// draw the grid
			if(showGrid == true)
			{
				for(int i = 0; i < tileColumns; i++) // columns (3)
				{
					for(int j = 0; j < tileRows; j++) // rows (4)
					{
						// get the tile
						Tile tileIcon = tilesMap.get(j + "-" + i);
						
						if(j % 2 == 0) // draw even rows
						{
							if(tileIcon.getType() == Tile.TILE_NOGO && showNogoTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth, j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_EXIT && showExitTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth, j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_WALK && showWalkTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth, j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
						}
						else // draw odd rows
						{
							if(tileIcon.getType() == Tile.TILE_NOGO && showNogoTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth + (tileWidth / 2), j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_EXIT && showExitTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth + (tileWidth / 2), j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_WALK && showWalkTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), i * tileWidth + (tileWidth / 2), j * (tileHeight / 2), new GridViewMovementImageObserver(this));
							}
								
						}
					}
				}
			}
			
			bufferGraphics.drawImage(reticleTile.getImage(), mouseX - (tileWidth / 2), mouseY - (tileHeight / 2), new GridViewMovementImageObserver(this));
		
			// draw the offscreen image to the screen like a normal image.
	        // Since offscreen is the screen width we start at 0,0.
			if(g != null)
			{
				g.drawImage(offscreen,0,0,new GridViewMovementImageObserver(this));
			}
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
	
	private void initTilesMap()
	{
		// draw the grid
		for(int i = 0; i < tileColumns; i++) // columns (3)
		{
			for(int j = 0; j < tileRows; j++) // rows (4)
			{	
				// put the tile into the HashMap
				tilesMap.put(j + "-" + i, new Tile(j,i,Tile.TILE_NOGO));
			}
		}
	}
	
	// set a new background image
	public void setBackgroundImage(String imagePath)
	{
		this.backgroundImagePath = imagePath;
		
		backgroundImage = resourceLoader.getImageFromJar(imagePath);
	}
	
	// set the currently selected tile type
	public void setCurrentTileType(String type)
	{
		tileTypeString = type;
		
		if(type.equals("walk")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_WALK);}
		if(type.equals("nogo")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_NOGO);}
		if(type.equals("exit")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_EXIT);}
	}
	
	// show/hide the grid
	public void showGrid(boolean showGrid)
	{
		this.showGrid = showGrid;
	}
	
	public void showWalkTiles(boolean showWalkTiles) {this.showWalkTiles = showWalkTiles;}
	public void showNogoTiles(boolean showNogoTiles) {this.showNogoTiles = showNogoTiles;}
	public void showExitTiles(boolean showExitTiles) {this.showExitTiles = showExitTiles;}
	
	public void toggleGrid()
	{
		showGrid = !showGrid;
	}
	
	// return the HashMap of the tiles
	public HashMap<String,Tile> getTilesMap() {return tilesMap;}
	
	// set the tiles HashMap
	public void setTilesMap(HashMap<String,Tile> tilesMap) {this.tilesMap = tilesMap;}
	
	// return the background image
	public String getBackgroundImagePath() {return backgroundImagePath;}
	
	// set the animations
	public void setAnimations(ArrayList<Animation> animations)
	{
		this.animations = animations;
	}
	
	// set the sounds
	public void setSounds(ArrayList<SoundPlayable> sounds)
	{
		this.sounds = sounds;
	}
	
	public void setupChatBubbles() {}
	
	// add a text bubble (called from the UI)
	public void addTextBubble(String username, String text, int x)
	{
	}
}

class GridViewMovementImageObserver implements ImageObserver
{
	RoomEditorUI theWindow;
	public GridViewMovementImageObserver(RoomEditorUI theWindow) {this.theWindow = theWindow;}
	public GridViewMovementImageObserver() {}
	public GridViewMovementImageObserver(GridView gridView) {}
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
	{
		//System.out.println("Image updated");
		//theWindow.repaint();
	    return true;
	}
}
