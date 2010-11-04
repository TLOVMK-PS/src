// GamePirates.java by Matt Fritz
// October 31, 2010 (Happy Halloween!)
// Handles the running of the Pirates of the Caribbean game

package games.pirates;

import games.InternalGame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import astar.AStarCharacter;

import tiles.Tile;
import util.AppletResourceLoader;
import util.FileOperations;

public class GamePirates extends InternalGame implements Runnable
{	
	private Thread gameThread = null; // thread to handle the running of the game
	
	private int mouseX = 0;
	private int mouseY = 0;
	private int gridX = 0;
	private int gridY = 0;
	
	// small-ass tiles
	private int tileWidth = 48;
	private int tileHeight = 24;
	private Tile currentTile = null;
	private HashMap<String,Tile> tilesMap = new HashMap<String,Tile>();
	
	// map of the HashMap structures for the levels
	private HashMap<String, HashMap<String,Tile>> levelsMap = new HashMap<String,HashMap<String,Tile>>();
	
	// HashMap and simple array of ships on the screen
	private HashMap<String,AStarShip> ships = new HashMap<String,AStarShip>();
	private AStarShip shipsArray[] = new AStarShip[8];
	
	private Image offscreen = null; // offscreen buffer
	private Graphics bufferGraphics = null; // graphics object for the offscreen buffer
	
	// images used by the Pirates game
	private BufferedImage move_reticle = AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/move_reticle.png");
	private HashMap<String,BufferedImage> backgroundImages = new HashMap<String,BufferedImage>();
	private BufferedImage backgroundImage = null;
	
	public GamePirates()
	{
		// assign the properties of the game
		super("pirates", "Pirates of the Caribbean", 3, 3);
		
		// set the background images
		backgroundImages.put("1_1", AppletResourceLoader.getBufferedImageFromJar("img/games/pirates/levels/level_1_1.jpg"));
		
		// create the levels
		levelsMap.put("1_1",FileOperations.loadPiratesLevelTiles(1,1));
		
		// add the mouse handlers
		addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent e) {}
			public void mouseMoved(MouseEvent e)
			{
				mouseX = e.getPoint().x;
				mouseY = e.getPoint().y;
				
				if(mouseX < 0 || mouseX > getWidth()) {return;}
				if(mouseY < 0 || mouseY > getHeight()) {return;}
				
				// convert the mouse coordinates to conform to the isometric grid
				convertMouseToGridCoords();
				
				// figure out whether to display the reticle
	     		currentTile = tilesMap.get(gridY + "-" + (gridX / 2));
			}
		});
		addMouseListener(new MouseListener()
		{
			public void mousePressed(MouseEvent e)
			{
				requestFocusInWindow();
			}
			public void mouseEntered(MouseEvent e)
			{
				requestFocusInWindow();
			}
			public void mouseReleased(MouseEvent e)
			{
				// move the ship to this tile
				moveShip(ships.get("Default"), (gridX / 2), gridY);
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		// add the key listener
		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e)
			{
				// fire cannons if it's one of the arrow keys
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT)
				{
					// FIRE CANNONS
					//fireCannons("left");
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT)
				{
					// FIRE CANNONS 
					//fireCannons("right");
				}
			}
		});
	}
	
	// convert the ships HashMap to an array
	private void convertShipsToArray()
	{
		shipsArray = ships.values().toArray(shipsArray);
	}
	
	private void convertMouseToGridCoords()
	{
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
	}
	
	// start the game
	public void start()
	{
		// create the offscreen buffer
		offscreen = createImage(getWidth(), getHeight());
		
		// create the buffer graphics object
		bufferGraphics = offscreen.getGraphics();
		
		// reset the credits won
		setCreditsWon(0);
		
		// reset the scores
		setPlayerScore(0);
		
		// reset the level and round number
		setRoundNum(1);
		setLevelNum(1);
		
		// reset the tiles for the level
		tilesMap = levelsMap.get(getLevelNum() + "_" + getRoundNum());
		
		// add a default ship
		AStarShip defaultShip = new AStarShip("Default",12,2);
		defaultShip.setCurrentTile(tilesMap.get("12-2"));
		defaultShip.snapToCurrentTile();
		defaultShip.updateShipImages();
		ships.put("Default",defaultShip);
		
		// convert the ships ArrayList to an array
		convertShipsToArray();
		
		// reset the background image
		resetLevelBackgroundImage();
		
		gameThread = new Thread(this, getGameTitle());
		gameThread.start();
	}
	
	// stop the game externally
	public void stop()
	{
		gameThread.interrupt();
		gameThread = null;
	}
	
	// end the game
	public void endGame()
	{
		// hide the game area
		getUIObject().hideGameArea(getGameID());
	}
	
	public void run()
	{
		while(gameThread != null)
		{
			// only paint if this grid is visible
			if(isVisible())
			{
				paintComponent(this.getGraphics());
			}
			
			try
			{
				Thread.sleep(getGraphicsDelay());
			}
			catch(Exception e) {}
		}
	}
	
	public void update(Graphics g)
	{
		paintComponent(g);
	}
	
	public void paintComponent(Graphics g)
	{
		// make sure the buffer exists
		if(bufferGraphics != null)
		{
			// clear the buffered image
			bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
			
			// draw the background image onto the buffer
			bufferGraphics.drawImage(backgroundImage, 0, 0, this);
			
			// check to see if we should draw the reticle
			if(currentTile != null && currentTile.getType() == Tile.TILE_WALK)
			{
				// draw the reticle
				bufferGraphics.drawImage(move_reticle, currentTile.getX(), currentTile.getY(), this);
			}
			
			// draw all the ships
			for(int i = 0; i < shipsArray.length; i++)
			{
				// get the next ship
				AStarShip ship = shipsArray[i];
				
				// make sure the damn ship actually exists
				if(ship != null)
				{
					if(ship.getPath() != null)
					{
						if(ship.getPath().size() > 0)
						{
							Tile nextTile = ship.getPath().get(0); // get the next step in the path
							
							// check if movement is necessary
							ship.setColDiff(Math.abs(ship.getCol() - nextTile.getColumn()));
							//System.out.println("COLDIFF: " + ship.getColDiff());
							if(ship.getColDiff() > 0) // prevent the back-and-forth movement across the same column
							{
								if(ship.getX() == nextTile.getX())
								{
									//System.out.println("Character X speed at 0");
									ship.setxSpeed(0);
								}
								else
								{	
									// move along the X-axis
									if(ship.getX() == nextTile.getX())
									{
										// stay along the same line of movement to prevent
										// the "back-and-forth" vertical movement
									}
									else
									{
										if(ship.getX() < nextTile.getX())
										{
											ship.setxSpeed(4);
											ship.setX(ship.getX() + ship.getxSpeed());
										}
										if(ship.getX() > nextTile.getX())
										{
											ship.setxSpeed(-4);
											ship.setX(ship.getX() + ship.getxSpeed());
										}
									}
								}
							}
							else
							{
								if(ship.getPath().size() == 1)
								{
									// move along the X-axis
									if(ship.getX() == nextTile.getX())
									{
										// stay along the same line of movement to prevent
										// the "back-and-forth" vertical movement
									}
									else
									{
										if(ship.getX() < nextTile.getX())
										{
											ship.setxSpeed(4);
											ship.setX(ship.getX() + ship.getxSpeed());
										}
										if(ship.getX() > nextTile.getX())
										{
											ship.setxSpeed(-4);
											ship.setX(ship.getX() + ship.getxSpeed());
										}
									}
								}
								else
								{
									if(ship.getySpeed() > 0 || ship.getySpeed() < 0)
									{
										ship.setxSpeed(0);
									}
								}
							}
							
							if(ship.getY() == nextTile.getY())
							{
								//System.out.println("Character Y speed at 0");
								ship.setySpeed(0);
							}
							else
							{
								// move along the Y-axis
								if(ship.getY() < nextTile.getY())
								{
									ship.setySpeed(2);
								}
								if(ship.getY() > nextTile.getY())
								{
									ship.setySpeed(-2);
								}
								ship.setY(ship.getY() + ship.getySpeed());
							}
							
							if(ship.getColDiff() == 0)
							{
								if(ship.getPath() != null)
								{
									if(ship.getPath().size() > 1)
									{
										if(ship.getCol() == nextTile.getColumn() && ship.getY() == nextTile.getY())
										{
											// remove the first step in the path so we can proceed to the next
											ship.setCurrentTile(ship.getPath().get(0));
											//System.out.println("TILE MOVED: " + ship.getCurrentTile().toString());
											ship.removeTopmostPathStep();
										}
									}
									else
									{
										if(ship.getX() == nextTile.getX() && ship.getY() == nextTile.getY())
										{
											// remove the first step in the path so we can proceed to the next
											ship.setCurrentTile(ship.getPath().get(0));
											//System.out.println("TILE MOVED: " + ship.getCurrentTile().toString());
											ship.removeTopmostPathStep();
											
											if(ship.getPath().size() == 0)
											{
												// ship's done moving
											}
										}
									}
								}
							}
							else
							{
								if(ship.getX() == nextTile.getX() && ship.getY() == nextTile.getY())
								{
									// remove the first step in the path so we can proceed to the next
									if(ship.getPath() != null)
									{
										ship.setCurrentTile(ship.getPath().get(0));
										//System.out.println("TILE MOVED: " + ship.getCurrentTile().toString());
										ship.removeTopmostPathStep();
										
										if(ship.getPath().size() == 0)
										{
											// ship's done moving
										}
									}
								}
							}
						}
					}

					// draw the ship
					bufferGraphics.drawImage(ship.getImage(), ship.getX(), ship.getY() - ship.getImage().getHeight() + tileHeight, this);
				}
			}
			
			// check to make sure the internal graphics object exists
			if(g != null)
			{
				// swap the buffer to the screen
				g.drawImage(offscreen, 0, 0, this);
			}
		}
	}
	
	// reset the background image for the level
	public void resetLevelBackgroundImage()
	{
		backgroundImage = backgroundImages.get(getLevelNum() + "_" + getRoundNum());
	}
	
	// move a ship in the current room
	public void moveShip(AStarShip ship, int destGridX, int destGridY)
	{
		// make sure it's not a nogo tile
		if(tilesMap.get(destGridY + "-" + destGridX).getType() == Tile.TILE_NOGO) {return;}
		
 		// make sure the ship is still in the room
 		if(ship != null)
 		{
 			// get the current room configuration
     		ship.setPathfinderTiles(tilesMap);
     		
	 		// process a pathfinding operation for the ship
	 		//System.out.println("Setting current tile: " + ship.getRow() + "-" + ship.getCol());
	 		//System.out.println("Tile type: " + tilesMap.get(ship.getRow() + "-" + ship.getCol()).getTypeString());
	 		ship.setCurrentTile(tilesMap.get(ship.getRow() + "-" + ship.getCol()));
	 		ship.clearPath();
	 		ship.setPath(ship.getPathfinder().getPath(ship.getCurrentTile(), tilesMap.get(destGridY + "-" + destGridX)));
	 		
	 		ships.put(ship.getUsername(), ship); // put the ship back in the HashMap
	 		
	 		// create the shipsArray array of ships
			convertShipsToArray();
 		}
	}
}
