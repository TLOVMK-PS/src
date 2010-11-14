package roomviewer;

//RoomViewerGrid.java by Matt Fritz
//March 26, 2009
//Class that implements the grid portion of the Room Viewer

import gridobject.GridObject;
import interfaces.GridSortable;
import interfaces.GridViewable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import comparators.DepthSortComparator;

import clickable.ClickableArea;
import clickable.ClickableAreaHandler;

import chat.ChatBubble;
import chat.ChatBubbles;

import animations.Animation;
import animations.StationaryAnimation;
import astar.AStarCharacter;

import roomobject.RoomItem;
import sockets.messages.MessageAddFriendConfirmation;
import sockets.messages.MessageAddFriendRequest;
import sockets.messages.MessageAddInventory;
import sockets.messages.MessageAddUserToRoom;
import sockets.messages.MessageCreateGuestRoom;
import sockets.messages.MessageMoveCharacter;
import sockets.messages.MessageRemoveFriend;
import sockets.messages.MessageRemoveUserFromRoom;
import sockets.messages.MessageSaveGuestRoom;
import sockets.messages.MessageSaveMailMessages;
import sockets.messages.MessageSendMailToUser;
import sockets.messages.MessageUpdateCharacterClothing;
import sockets.messages.MessageUpdateCharacterInRoom;
import sockets.messages.MessageUpdateInventory;
import sockets.messages.MessageUpdateItemInRoom;
import sounds.RepeatingSound;
import sounds.SingleSound;
import sounds.SoundPlayable;
import tiles.Tile;
import ui.WindowAvatarInformation;
import ui.WindowClothing;
import ui.WindowDesignModeItem;
import ui.WindowEditRoomDescription;
import ui.WindowGuestRooms;
import ui.WindowHelp;
import ui.WindowInventory;
import ui.WindowMap;
import ui.WindowMessages;
import ui.WindowRoomDescription;
import ui.WindowSettings;
import ui.WindowShop;
import util.AppletResourceLoader;
import util.Dictionary;
import util.FileOperations;
import util.FriendsList;
import util.InventoryInfo;
import util.InventoryItem;
import util.MailMessage;
import util.RatingSystem;
import util.StaticAppletData;

public class RoomViewerGrid extends JPanel implements GridViewable, Runnable
{
	private Thread gridThread;
	private int graphicsDelay = 20; // milliseconds between each frame
	
	final String LOADING_BACKGROUND_PATH = "img/ui/loading_room_vmk.png";
	String backgroundImagePath = LOADING_BACKGROUND_PATH;
	
	//String backgroundImagePath = "tiles_img/test_room_image.png";
	ImageIcon backgroundImage = AppletResourceLoader.getImageFromJar(backgroundImagePath);
	
	ImageIcon nogoTileImage = null; //new Tile().getNogoTileImage();
	ImageIcon walkTileImage = null; //new Tile().getWalkTileImage();
	ImageIcon exitTileImage = null; //new Tile().getExitTileImage();
	
	Tile currentTile = new Tile(0,0,Tile.TILE_WALK, ""); // currently selected tile type
	
	ImageIcon reticleExit = AppletResourceLoader.getImageFromJar("tiles_img/exit_reticle_64.png"); // "walk" reticle
	ImageIcon reticleNogo = AppletResourceLoader.getImageFromJar("tiles_img/nogo_reticle_64.png"); // "nogo" reticle
	ImageIcon reticleWalk = AppletResourceLoader.getImageFromJar("tiles_img/walk_reticle_64.png"); // "exit" reticle
	
	ImageIcon reticleTile = reticleWalk; // currently displayed reticle
	
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
	
	RoomViewerUI myRoomEditorWindow;
	
	HashMap<String,Tile> tilesMap = new HashMap<String,Tile>();
	
	private String tileTypeString = "walk";
	
	ArrayList<Animation> animations = new ArrayList<Animation>(); // ArrayList of animations
	//WindmillAnimation windmillAnimation = new WindmillAnimation(); // test animation
	
	boolean startSounds = true; // true to start the sounds
	ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>(); // ArrayList of sounds
	//RepeatingSound theSound = new RepeatingSound(0,"sound/sub_ping.wav"); // test repeating sound
	
	boolean designMode = true; // false to turn off "Design Mode"
	boolean designMoveMode = false; // true to turn on movement while in "Design Mode"
	ArrayList<RoomItem> items = new ArrayList<RoomItem>(); // items in the room
	RoomItem currentRoomItem = null; // currently selected room item
	
	ChatBubbles theChatBubbles; // data structure for the chat bubbles
	
	Font textFont; // font used by the text bubbles
	Font textFontBold; // bold font used by the text bubbles (for username display)
	Rectangle2D maxFontBounds; // maximum bounds of the font
	FontRenderContext frc; // render context for the font
	
	RoomViewerUI uiObject;
	
	// room description window
	WindowRoomDescription roomDescriptionWindow;
	
	// edit room description window
	WindowEditRoomDescription editRoomDescriptionWindow;
	
	// messages window
	WindowMessages messagesWindow;
	
	// inventory window
	WindowInventory inventoryWindow;
	
	// shop window
	WindowShop shopWindow;
	
	// clothing window
	WindowClothing clothingWindow;
	
	// settings window
	WindowSettings settingsWindow;
	
	// help window
	WindowHelp helpWindow;
	
	// avatar information window
	WindowAvatarInformation avatarInfoWindow;
	
	// "Design Mode" room item window
	WindowDesignModeItem designModeItemWindow;
	
	// map window
	WindowMap mapWindow;
	
	// "Guest Rooms" window
	WindowGuestRooms guestRoomsWindow;
	
	// Pathfinding stuff
	HashMap<String,AStarCharacter> characters = new HashMap<String,AStarCharacter>(); // all characters in this rom
	AStarCharacter[] charactersArray = new AStarCharacter[0];
	AStarCharacter myCharacter = new AStarCharacter(); // the single specific character for this client
	
	HashMap<String,String> roomInfo = new HashMap<String,String>();
	String roomID = ""; // ID of the current room
	String roomName = ""; // name of the current room
	boolean roomLoading = false;
	boolean suspendMessages = false; // true to prevent the sending of messages
	
	RepeatingSound hvmkIntroMusic = null; // the music for the Map screen when it loads for the first time
	
	ArrayList<ClickableArea> clickableAreas = new ArrayList<ClickableArea>(); // clickable areas for the grid
	ClickableAreaHandler clickableAreaHandler = new ClickableAreaHandler(); // handler for the ClickableArea actions
	
	boolean depthSort = false; // TRUE to enable depth-sorting on the grid objects
	ArrayList<GridSortable> gridObjects = new ArrayList<GridSortable>(); // grid objects for depth sorting
	ArrayList<GridSortable> allGridObjects = new ArrayList<GridSortable>(); // grid objects AND characters
	GridSortable[] gridObjectsArray = new GridSortable[0]; // array for characters and objects to be converted to
	
	public RoomViewerGrid()
	{
		
	}
	
	public void start()
	{
		gridThread = new Thread(this, "Internal Grid View");
		gridThread.start();
	}
	
	public void stop()
	{
		gridThread.interrupt();
		gridThread = null;
	}
	
	public void run()
	{
		while(this != null)
		{
			// only paint if this grid is visible
			if(isVisible())
			{
				paintComponent(this.getGraphics());
			}
			
			try
			{
				Thread.sleep(graphicsDelay);
			}
			catch(Exception e) {}
		}
	}
	
	public AStarCharacter getMyCharacter() {return myCharacter;}
	
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
	
	public void loadGridView()
	{	
		// turn off the double-buffering for the grid
		//this.setDoubleBuffered(false);
		
		this.addMouseListener(new MouseAdapter()
     {
     	public void mouseReleased(MouseEvent e)
     	{	
     		if(suspendMessages == true) {return;}
     		
     		// make sure the map is not visible
     		if(!mapWindow.isVisible())
     		{
	     		// make sure we only process these events when the mouse
	     		// is within the editing grid
	     		mouseX = e.getX();
	     		mouseY = e.getY();
	     		
	     		if(mouseX < 0 || mouseX > 800) {return;}
	     		if(mouseY < 0 || mouseY > 600) {return;}
	     		
	     		Point mousePoint = new Point(mouseX, mouseY);
	     		
	     		// check to see if we clicked inside a bounding box (room item)
	     		if(designMode == true)
	     		{
	     			if(currentRoomItem != null)
	     			{
	     				// current room item is already selected
	     				//System.out.println("Clicked inside currently selected item's bounding box");

	     				// place the selected item
	     				items.add(currentRoomItem); 

	     				// send the update item message to the server
	     				uiObject.sendMessageToServer(new MessageUpdateItemInRoom(roomID, currentRoomItem));

	     				// save the room items
	     				saveRoomItems();

	     				// turn off "Move Mode" for "Design Mode"
	     				designMoveMode = false;
	     				designModeItemWindow.setVisible(false);

	     				currentRoomItem = null;
	     				convertMouseToGridCoords();

	     				return;
	     			}
	     			else
	     			{
		     			for(RoomItem r : items)
		     			{
		     				// make sure this content is allowed based upon the content rating indices
		     				if(RatingSystem.isContentAllowed(r.getContentRatingIndex(), myCharacter.getContentRatingIndex()))
		     				{
		     					// clicked inside an item's bounding box where it's non-transparent?
			     				if(r.getBoundingBox().contains(mousePoint) && !r.isTransparentAt(mousePoint.x - r.getBoundingBox().x, mousePoint.y - r.getBoundingBox().y))
			     				{
			     					System.out.println("Clicked inside the non-transparent area of an item");
			     					
			     					// make this the current room item if there is none, otherwise release
			     					// the room item and keep it where it is
			     					//convertMouseToGridCoords();
			     					if(currentRoomItem == null)
			     					{
			     						currentRoomItem = r;
			     						items.remove(r);
			     						
			     						// show the "Design Mode" room item window
			     						designModeItemWindow.setItemName(currentRoomItem.getName());
			     						designModeItemWindow.setLocation(currentRoomItem.getX() - (designModeItemWindow.getWidth() / 3), currentRoomItem.getY() - currentRoomItem.getImage().getHeight() + currentRoomItem.getTileHeight());
			     						designModeItemWindow.setVisible(true);
			     						
			     						convertMouseToGridCoords();
			     						return;
			     					}
			     				}
		     				}
		     			}
	     			}
	     		}
	     		
	     		// check to see if we clicked inside a bounding box (character)
	     		for(AStarCharacter c : characters.values())
	     		{
	     			if(c.getBoundingBox().contains(mousePoint) && !c.isTransparentAt(mousePoint.x - c.getBoundingBox().x, mousePoint.y - c.getBoundingBox().y))
	     			{
	     				// check if it should be inactive
	     				if(c.getUsername().equals(uiObject.getUsername()))
	     				{
	     					avatarInfoWindow.setInactive(true);
	     				}
	     				else
	     				{
	     					avatarInfoWindow.setInactive(false);
	     				}
	     				
	     				// set the permissions for the Avatar Info window
	     				avatarInfoWindow.setPermissions(true, true, true, true, false);
	     				
	     				avatarInfoWindow.setUsername(c.getUsername());
	     				
	     				// make sure the avatar's signature is cleaned so it doesn't contain inappropriate text
	     				avatarInfoWindow.setSignature(Dictionary.cleanInappropriateText(myCharacter, c.getSignature()));
	     				
	     				avatarInfoWindow.setBadges(c.getDisplayedBadges());
	     				
	     				if(c.getUsername().equals(uiObject.getUsername()))
	     				{
	     					avatarInfoWindow.setPins(myCharacter.getDisplayedPins());
	     				}
	     				else
	     				{
	     					avatarInfoWindow.setPins(c.getDisplayedPins());
	     				}
	     				avatarInfoWindow.setVisible(true);
	     				
	     				System.out.println("Clicked non-transparent area for character: " + c.getUsername());
	     				
	     				convertMouseToGridCoords(); // convert the mouse coords back to grid coords
	     				
	     				return;
	     			}
	     		}
	     		
	     		// check to see if we clicked inside a clickable area
	     		for(ClickableArea ca : clickableAreas)
	     		{
	     			if(ca.contains(mousePoint))
	     			{
	     				// handle the action specified by the ClickableArea
	     				clickableAreaHandler.execute(ca, uiObject.theGridView);
	     				
	     				return;
	     			}
	     		}
	     		
	     		convertMouseToGridCoords(); // convert the mouse coords back to grid coords
	     		
	     		// move the character locally
	     		if(currentRoomItem == null)
	     		{
	     			moveCharacterInRoom(myCharacter, (gridX / 2), gridY);
	     		}
	     		//System.out.println("CLICK AT Mouse X: " + mouseX + " - Mouse Y: " + mouseY + "Grid X: " + gridX + " - Grid Y: " + gridY);
     		}
     	}
     });
     
     this.addMouseMotionListener(new MouseMotionAdapter()
     {
     	public void mouseMoved(MouseEvent e)
     	{
     		// make sure the map isn't visible
     		if(!mapWindow.isVisible())
     		{
	     		mouseX = e.getX();
	     		mouseY = e.getY();
	     		
	     		// make sure we only process these events when the mouse
	     		// is within the editing grid
	     		if(mouseX < 0 || mouseX > 800) {return;}
	     		if(mouseY < 0 || mouseY > 600) {return;}
	     		
	     		// check the UI elements
	     		
	     		// convert back to the grid coords
	     		convertMouseToGridCoords();
	     		
	     		// figure out the correct reticle to display
	     		Tile currentTile = tilesMap.get(gridY + "-" + (gridX / 2));
	     		if(currentTile != null)
	     		{
	     			if(currentTile.getType() == Tile.TILE_EXIT) {reticleTile = reticleExit;}
	     			if(currentTile.getType() == Tile.TILE_WALK) {reticleTile = reticleWalk;}
	     			if(currentTile.getType() == Tile.TILE_NOGO) {reticleTile = reticleNogo;}
	     		}
	     		
	     		setCurrentTileType(tileTypeString); // set the current tile type and coords
	     		
	     		// move the currently selected room item if it exists
	     		if(currentTile != null && designMoveMode == true)
	     		{
	     			if(currentRoomItem != null)
	     			{
	     				// Only two possible conditions for the current tile can facilitate the movement of the current room item:
	     				// 1) WALK tile and either a furniture item or a poster item
	     				// 2) NOGO tile and a poster item
			     		if((currentTile.getType() == Tile.TILE_WALK && (currentRoomItem.getType() == RoomItem.FURNITURE || currentRoomItem.getType() == RoomItem.POSTER)) || (currentTile.getType() == Tile.TILE_NOGO && currentRoomItem.getType() == RoomItem.POSTER))
			     		{
			     			currentRoomItem.setRow(currentTile.getRow());
			     			currentRoomItem.setCol(currentTile.getColumn());
			     			currentRoomItem.setX(currentTile.getX());
			     			currentRoomItem.setY(currentTile.getY() + tileHeight - currentRoomItem.getImage().getHeight());
			     		}
	     			}
	     		}
     		}
     	}
     });
     
     // intialize the tilesMap
     initTilesMap();
     
     // start the graphics loop
     graphicsLoop();
     
     //add(roomDescriptionWindow);
	}
	
	// set-up the double buffering objects
	// called from RoomEditorUI
	public void setOffscreenImage(Image offscreen)
	{
		this.offscreen = offscreen;
		bufferGraphics = this.offscreen.getGraphics();
	}
	
	public void paintComponent(Graphics g)
	{	
		// make sure the buffer exists
		if(bufferGraphics != null)
		{
			// clear the screen
			bufferGraphics.clearRect(0, 0, 800, 572);
			
			// only draw if the map is not visible
			if(!mapWindow.isVisible())
			{
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
							
							if(tileIcon.getType() == Tile.TILE_NOGO && showNogoTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), tileIcon.getX(), tileIcon.getY(), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_EXIT && showExitTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), tileIcon.getX(), tileIcon.getY(), new GridViewMovementImageObserver(this));
							}
							else if(tileIcon.getType() == Tile.TILE_WALK && showWalkTiles == true)
							{
								bufferGraphics.drawImage(tileIcon.getImage(), tileIcon.getX(), tileIcon.getY(), new GridViewMovementImageObserver(this));
							}
						}
					}
				}
			
				// draw the animations
				for(Animation anim : animations)
				{
					bufferGraphics.drawImage(anim.getNextFrame().getImage(), anim.getX(), anim.getY(), this);
				}
				
				// draw the tile reticle for the current tile type
				bufferGraphics.drawImage(reticleTile.getImage(), mouseX - (tileWidth / 2), mouseY - (tileHeight / 2), new GridViewMovementImageObserver(this));
				
				// draw any room items if they exist
				if(items.size() > 0)
				{
					for(int i = 0; i < items.size(); i++)
					{
						RoomItem nextItem = items.get(i);
						if(nextItem != null)
						{
							// make sure the content rating for this item is fine before we draw it to the grid
							if(RatingSystem.isContentAllowed(nextItem.getContentRatingIndex(), myCharacter.getContentRatingIndex()))
							{
								bufferGraphics.drawImage(nextItem.getImage(), nextItem.getX(), nextItem.getY(), this);
							}
						}
					}
				}
				
				// draw the currently selected room item
				if(currentRoomItem != null)
				{
					// make sure the content rating for this item is fine before we draw it to the grid
					if(RatingSystem.isContentAllowed(currentRoomItem.getContentRatingIndex(), myCharacter.getContentRatingIndex()))
					{
						bufferGraphics.drawImage(currentRoomItem.getImage(), currentRoomItem.getX(), currentRoomItem.getY(), this);
					}
				}
				
				// check whether all the grid objects need to be depth sorted again
				if(depthSort)
				{
					// depth-sort ALL the grid objects and convert them into an array
					Collections.sort(allGridObjects, new DepthSortComparator());
					gridObjectsArray = allGridObjects.toArray(gridObjectsArray);
					
					// switch off the depthSort flag
					depthSort = false;
				}
				
				// draw all the objects and characters on the grid
				for(int gridObjectCount = 0; gridObjectCount < gridObjectsArray.length; gridObjectCount++)
				{
					if(gridObjectsArray[gridObjectCount] instanceof GridObject)
					{
						// get a grid object from the array
						GridObject obj = (GridObject)gridObjectsArray[gridObjectCount];
						
						// draw the image for the object
						bufferGraphics.drawImage(obj.getImage(), obj.getX(), obj.getY(), this);
					}
					else if(gridObjectsArray[gridObjectCount] instanceof AStarCharacter)
					{
						// get a character from the array instead of the HashMap to save on memory consumption
						AStarCharacter character = (AStarCharacter)gridObjectsArray[gridObjectCount];

						if(character.getPath() != null)
						{
							if(character.getPath().size() > 0)
							{
								// this shit will need to be depth-sorted again
								depthSort = true;
								
								Tile nextTile = character.getPath().get(0); // get the next step in the path
								
								// check if movement is necessary
								character.setColDiff(Math.abs(character.getCol() - nextTile.getColumn()));
								//System.out.println("COLDIFF: " + character.getColDiff());
								if(character.getColDiff() > 0) // prevent the back-and-forth movement across the same column
								{
									if(character.getX() == nextTile.getX())
									{
										//System.out.println("Character X speed at 0");
										character.setxSpeed(0);
									}
									else
									{	
										// move along the X-axis
										if(character.getX() == nextTile.getX())
										{
											// stay along the same line of movement to prevent
											// the "back-and-forth" vertical movement
										}
										else
										{
											if(character.getX() < nextTile.getX())
											{
												character.setxSpeed(4);
												character.setX(character.getX() + character.getxSpeed());
											}
											if(character.getX() > nextTile.getX())
											{
												character.setxSpeed(-4);
												character.setX(character.getX() + character.getxSpeed());
											}
										}
									}
								}
								else
								{
									if(character.getPath().size() == 1)
									{
										// move along the X-axis
										if(character.getX() == nextTile.getX())
										{
											// stay along the same line of movement to prevent
											// the "back-and-forth" vertical movement
										}
										else
										{
											if(character.getX() < nextTile.getX())
											{
												character.setxSpeed(4);
												character.setX(character.getX() + character.getxSpeed());
											}
											if(character.getX() > nextTile.getX())
											{
												character.setxSpeed(-4);
												character.setX(character.getX() + character.getxSpeed());
											}
										}
									}
									else
									{
										if(character.getySpeed() > 0 || character.getySpeed() < 0)
										{
											character.setxSpeed(0);
										}
									}
								}
								
								if(character.getY() == nextTile.getY())
								{
									//System.out.println("Character Y speed at 0");
									character.setySpeed(0);
								}
								else
								{
									// move along the Y-axis
									if(character.getY() < nextTile.getY())
									{
										character.setySpeed(2);
									}
									if(character.getY() > nextTile.getY())
									{
										character.setySpeed(-2);
									}
									character.setY(character.getY() + character.getySpeed());
								}
								
								if(character.getColDiff() == 0)
								{
									if(character.getPath() != null)
									{
										if(character.getPath().size() > 1)
										{
											if(character.getCol() == nextTile.getColumn() && character.getY() == nextTile.getY())
											{
												// remove the first step in the path so we can proceed to the next
												character.setCurrentTile(character.getPath().get(0));
												//System.out.println("TILE MOVED: " + character.getCurrentTile().toString());
												character.removeTopmostPathStep();
											}
										}
										else
										{
											if(character.getX() == nextTile.getX() && character.getY() == nextTile.getY())
											{
												// remove the first step in the path so we can proceed to the next
												character.setCurrentTile(character.getPath().get(0));
												//System.out.println("TILE MOVED: " + character.getCurrentTile().toString());
												character.removeTopmostPathStep();
												
												if(character.getPath().size() == 0)
												{
													// tell the server to update the final position of the character
													//System.out.println("Movement finished; updating character position");
													
													if(character.getCurrentTile().getType() == Tile.TILE_EXIT && character.getUsername().equals(myCharacter.getUsername()))
													{
														// exit tile, so change rooms
														String destination = character.getCurrentTile().getDest();
														if(!destination.equals("") && destination != null)
														{
															// this EXIT tile actually goes somewhere
															System.out.println("Changing room: " + destination);
															changeRoom(destination);
														}
													}
													else
													{
														// no exit tile, so update the character if it's the myCharacter object
														if(character.getUsername().equals(myCharacter.getUsername()))
														{
															sendUpdateCharacterMessage(character);
														}
													}
												}
											}
										}
									}
								}
								else
								{
									if(character.getX() == nextTile.getX() && character.getY() == nextTile.getY())
									{
										// remove the first step in the path so we can proceed to the next
										if(character.getPath() != null)
										{
											character.setCurrentTile(character.getPath().get(0));
											//System.out.println("TILE MOVED: " + character.getCurrentTile().toString());
											character.removeTopmostPathStep();
											
											if(character.getPath().size() == 0)
											{
												// tell the server to update the final position of the character
												//System.out.println("Movement finished; updating character position");
												if(character.getCurrentTile().getType() == Tile.TILE_EXIT && character.getUsername().equals(myCharacter.getUsername()))
												{
													// exit tile, so change rooms
													String destination = character.getCurrentTile().getDest();
													if(!destination.equals("") && destination != null)
													{
														// this EXIT tile actually goes somewhere
														System.out.println("Changing room: " + destination);
														changeRoom(destination);
													}
												}
												else
												{
													// no exit tile, so update the character if it's the myCharacter object
													if(character.getUsername().equals(myCharacter.getUsername()))
													{
														sendUpdateCharacterMessage(character);
													}
												}
											}
										}
									}
								}
							}
						}
						
						// draw the character
						if(character != null)
						{
							bufferGraphics.drawImage(character.getImage(), character.getX(), character.getY() - character.getImage().getHeight() + tileHeight, this);
						}
					}
				}
			}
			
			// paint the internal UI components
			this.paintComponents(bufferGraphics);
			
			// only draw if the map is not visible
			if(!mapWindow.isVisible())
			{
				// draw the chat bubbles
				if(theChatBubbles != null)
				{
					for(int bubbleIndex = 0; bubbleIndex < theChatBubbles.getChatBubbles().size(); bubbleIndex++)
					{
						ChatBubble bubble = theChatBubbles.getChatBubbles().get(bubbleIndex);
						
						// orient the chat bubble above the avatar
						//bubble.setX(character.getX());
						drawTextBubble(bubble.getUsername(), bubble.getText(), bubble.getX(), bubble.getY());
					}
				}
				// draw a test text bubble
				//drawTextBubble("HOST_Hawk", "Haha, I made this shit work.  Take THAT, bitches!", 100, 100);
			}
			
			// draw the offscreen image to the screen like a normal image.
	        // Since offscreen is the screen width we start at 0,0.
			if(g != null)
			{
				g.drawImage(offscreen,0,0,new GridViewMovementImageObserver(this));
				//roomDescriptionWindow.paintWindow();
			}
		}
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}
	
	// add a text bubble (called from the UI)
	public void addTextBubble(String username, String text, int x)
	{
		int textX = x;
		AStarCharacter character = characters.get(username); // get the specific character
		
		// check to see if this chat contains any inappropriate text
		text = Dictionary.cleanInappropriateText(myCharacter, text);
		
		if(character != null)
		{
			// orient the text bubble above the avatar
			frc = ((Graphics2D)bufferGraphics).getFontRenderContext(); // get the font render context
			int boldTextWidth = (int)textFontBold.getStringBounds(username + ": ",frc).getWidth(); // get the width of the bold username text
			int bubbleTextWidth = (int)textFont.getStringBounds(text, frc).getWidth(); // get the width of the content text string
			
			int fullTextWidth = boldTextWidth + bubbleTextWidth; // full width of the bubble text
			
			textX = character.getX() - (fullTextWidth / 2); // center the text above the avatar
			
			if(textX < 0) {textX = 5;} // make sure it doesn't go offscreen on the left
			if((textX + fullTextWidth) > 800) // make sure it doesn't go offscreen on the right
			{
				textX = 800 - fullTextWidth - 25;
			}
		}
		
		theChatBubbles.addChatBubble(username, text, textX);
		theChatBubbles.moveUpAll();
	}
	
	// draw a text bubble
	private void drawTextBubble(String username, String text, int x, int y)
	{
		frc = ((Graphics2D)bufferGraphics).getFontRenderContext(); // get the font render context
		int boldTextWidth = (int)textFontBold.getStringBounds(username + ": ",frc).getWidth(); // get the width of the bold username text
		int bubbleTextWidth = (int)textFont.getStringBounds(text, frc).getWidth(); // get the width of the content text string
		
		// white text bubble
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.fillRoundRect(x, y, boldTextWidth + bubbleTextWidth + 20, 16, 10, 10);
		
		// bubble outline with a specific color
		if(isStaffMember(username))
		{
			bufferGraphics.setColor(Color.RED); // red outline for staff
		}
		else
		{
			bufferGraphics.setColor(Color.BLACK); // black outline for regular player
		}
		bufferGraphics.drawRoundRect(x, y, boldTextWidth + bubbleTextWidth + 20, 16, 10, 10);
		
		// black bubble text
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.setFont(textFontBold);
		bufferGraphics.drawString(username + ": ", x + 10, y + 12);
		bufferGraphics.setFont(textFont);
		bufferGraphics.drawString(text, x + boldTextWidth + 10, y + 12);
	}
	
	// set up the animations
	private void setupAnimations()
	{
		System.out.println("In setupAnimations()");
		// start the animations
		for(Animation a : animations)
		{
			System.out.println("Starting animation: " + a.getName());
			a.start();
		}
		// start the test windmill animation
	    //System.out.println("Calling windmillAnimation.startAnimation()...");
	    //windmillAnimation.startAnimation();
	}
	
	// set up the sound
	private void setupSounds()
	{
		System.out.println("In setupSounds()");
		
		// start the sounds
		for(SoundPlayable s : sounds)
		{
			System.out.println("Starting sound: " + s.getName());
			s.playSound();
		}
		
		// start the test repeating sound
		//theSound.playSound();
	}
	
	// set up the chat bubbles
	public void setupChatBubbles()
	{
		System.out.println("In setupChatBubbles()");
		
		theChatBubbles = new ChatBubbles();
		
		theChatBubbles.start(); // start the chat bubbles thread
		
		//addTextBubble("HOST_Hawk", "This is index 0.", 100);
		//addTextBubble("HOST_Hawk", "This is index 1.  Almost there!", 100);
		//addTextBubble("HOST_Hawk", "This is index 2.  This is the longest text bubble.", 100);
	}
	
	// stop all the Threads
	public void stopAll()
	{
		for(Animation a : animations)
		{
			a.stop();
		}
		for(SoundPlayable s : sounds)
		{
			s.stopSound();
		}
		theChatBubbles.stop();
		theChatBubbles.clearAll();
	}
	
	private void setupInternalUI()
	{	
		// set up the room description window
		roomDescriptionWindow = new WindowRoomDescription(textFont, textFontBold, "Walk Test", "This is a walk test room. You can try out the features of the game, including chat and walking. Please feel free to wander around.<br><br>- Hawk's VMK: Development Team", 0, 424);
		roomDescriptionWindow.setVisible(false);
		add(roomDescriptionWindow);

		// set up the edit room description window
		editRoomDescriptionWindow = new WindowEditRoomDescription(textFont, textFontBold, "Walk Text", "This is a walk test room.  You can try out the features of the game, including chat and walking.  Please feel free to wander around.<br><br>-Hawk's VMK: Development Team", 0, 272);
		editRoomDescriptionWindow.setGridObject(this);
		editRoomDescriptionWindow.setVisible(false);
		add(editRoomDescriptionWindow);

		// set up the messages window
		messagesWindow = new WindowMessages(textFont, textFontBold, 100, 100);
		messagesWindow.setGridObject(this);
		messagesWindow.setVisible(false);
		add(messagesWindow);

		// set up the inventory window
		inventoryWindow = new WindowInventory(textFont, textFontBold, 100, 100);
		inventoryWindow.setGridObject(this);
		inventoryWindow.setVisible(false);
		add(inventoryWindow);

		// set up the shop window
		shopWindow = new WindowShop(textFont, textFontBold, 100, 100);
		shopWindow.setGridObject(this);
		shopWindow.setVisible(false);
		add(shopWindow);

		// set up the clothing window
		clothingWindow = new WindowClothing(textFont, textFontBold, 200, 50);
		clothingWindow.setGridObject(this);
		clothingWindow.setUsername(uiObject.getUsername());
		clothingWindow.setVisible(false);
		add(clothingWindow);

		// set up the settings window
		settingsWindow = new WindowSettings(textFont, textFontBold, 200, 50);
		settingsWindow.setGridObject(this);
		settingsWindow.setVisible(false);
		add(settingsWindow);

		// set up the help window
		helpWindow = new WindowHelp(textFont, textFontBold, 250, 50);
		helpWindow.setGridObject(this);
		helpWindow.setVisible(false);
		add(helpWindow);

		// set up the avatar information window
		avatarInfoWindow = new WindowAvatarInformation(textFont, textFontBold, 616, 306);
		avatarInfoWindow.setGridObject(this);
		avatarInfoWindow.setVisible(false);
		add(avatarInfoWindow);

		// set up the "Design Mode" room item window
		designModeItemWindow = new WindowDesignModeItem(textFont, 100, 100);
		designModeItemWindow.setGridObject(this);
		designModeItemWindow.setVisible(false);
		add(designModeItemWindow);

		// set up the "Guest Rooms" window
		guestRoomsWindow = new WindowGuestRooms(textFont, textFontBold, 200, 50);
		guestRoomsWindow.setGridObject(this);
		guestRoomsWindow.setVisible(false);
		add(guestRoomsWindow);

		// set up the map
		mapWindow = new WindowMap(textFont, textFontBold, 0, 0);
		mapWindow.setGridObject(this);
		mapWindow.setVisible(true);
		add(mapWindow);
	}
	
	public void graphicsLoop()
	{    
		// set up the animations
		setupAnimations();
		
		// set up the sounds
		if(startSounds)
		{
			setupSounds();
		}
		
		// set up the chat bubbles
		setupChatBubbles();
		
		// set up the internal UI
		setupInternalUI();
		
		// set up the character
		/*myCharacter.setUsername(uiObject.getUsername());
		myCharacter.setCurrentTile(tilesMap.get("15-7"));
		myCharacter.setX(myCharacter.getCurrentTile().getX());
		myCharacter.setY(myCharacter.getCurrentTile().getY());
		characters.put(uiObject.getUsername(), myCharacter);*/
	}
	
	private void initTilesMap()
	{
		tilesMap = new HashMap<String,Tile>(); // clear the tiles
		
		// draw the grid
		for(int i = 0; i < tileColumns; i++) // columns (3)
		{
			for(int j = 0; j < tileRows; j++) // rows (4)
			{	
				// put the tile into the HashMap
				Tile newTile = new Tile(j,i,Tile.TILE_NOGO, "");
				newTile.setWidth(tileWidth);
				newTile.setHeight(tileHeight);
				newTile.setAbsoluteCoordinates();
				tilesMap.put(j + "-" + i, newTile);
			}
		}
		
		//pathfinder.setTiles(tilesMap);
	}
	
	// set a new background image
	public void setBackgroundImage(String imagePath)
	{
		this.backgroundImagePath = imagePath;
		
		backgroundImage = AppletResourceLoader.getImageFromJar(imagePath);
	}
	
	// set the currently selected tile type
	public void setCurrentTileType(String type)
	{
		tileTypeString = type;
		
		if(type.equals("walk")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_WALK, "");}
		if(type.equals("nogo")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_NOGO, "");}
		if(type.equals("exit")) {currentTile = new Tile(gridY, (gridX / 2), Tile.TILE_EXIT, tilesMap.get(gridY + "-" + (gridX / 2)).getDest());}
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
	public void setTilesMap(HashMap<String,Tile> tilesMap)
	{
		//pathfinder.setTiles(tilesMap);
		
		// set up the character
		myCharacter.setUsername(uiObject.getUsername());
		myCharacter.setCurrentTile(tilesMap.get("15-7"));
		myCharacter.setX(myCharacter.getCurrentTile().getX());
		myCharacter.setY(myCharacter.getCurrentTile().getY());
		
		characters.put(uiObject.getUsername(), myCharacter);
		
		// create the charactersArray array of characters
		generateAllGridObjects();
		
		this.tilesMap = tilesMap;
	}
	
	// return the background image
	public String getBackgroundImagePath() {return backgroundImagePath;}
	
	// set the animations
	public void setAnimations(ArrayList<Animation> animations)
	{
		this.animations = animations;
		setupAnimations();
	}
	
	// play the intro music for the Map screen
	public void playIntroMusic()
	{
		// play the intro music for the Map screen
		hvmkIntroMusic = (RepeatingSound)FileOperations.loadSound("sounds/hvmk_intro_music.sound");
		sounds.add(hvmkIntroMusic);
		sounds.get(0).playSound();
	}
	
	// set the sounds
	public void setSounds(ArrayList<SoundPlayable> sounds)
	{
		this.sounds = sounds;
	}
	
	// start the sounds
	public void startSounds()
	{
		// make sure we can start the sounds
		if(startSounds)
		{
			setupSounds();
		}
	}
	
	// stop the sounds
	public void stopSounds()
	{
		// stop the sounds
		for(int i = 0; i < sounds.size(); i++)
		{
			sounds.get(i).stopSound();
		}
	}
	
	// set the text font
	public void setTextFont(Font textFont)
	{
		this.textFont = textFont;
	}
	
	// set the bold text font
	public void setTextFontBold(Font textFontBold)
	{
		this.textFontBold = textFontBold;
	}
	
	public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}
	
	public void toggleRoomDescriptionWindow() {roomDescriptionWindow.toggleVisibility();}
	
	public void toggleEditRoomDescriptionWindow()
	{
		editRoomDescriptionWindow.toggleVisibility();
		
		if(editRoomDescriptionWindow.isVisible())
		{
			// set the values in the boxes
			if(roomInfo != null)
			{
				editRoomDescriptionWindow.setRoomOwner(myCharacter.getUsername());
				editRoomDescriptionWindow.setRoomName(roomInfo.get("NAME"));
				editRoomDescriptionWindow.setRoomDescription(roomInfo.get("DESCRIPTION"));
				editRoomDescriptionWindow.setMusicURL(roomInfo.get("MUSIC"));
			}
		}
	}
	
	public void toggleMessagesWindow() {messagesWindow.toggleVisibility();}
	
	public void toggleInventoryWindow() {inventoryWindow.toggleVisibility();}
	
	public void toggleShopWindow() {shopWindow.toggleVisibility();}
	
	public void toggleClothingWindow()
	{
		clothingWindow.setSignatureText(myCharacter.getSignature());
		clothingWindow.toggleVisibility();
	}
	
	public void toggleSettingsWindow() {settingsWindow.toggleVisibility();} 
	
	public void toggleHelpWindow() {helpWindow.toggleVisibility();}
	
	public void toggleAvatarInformationWindow()
	{	
		avatarInfoWindow.toggleVisibility();
	}
	
	// generate the allGridObjects ArrayList containing the characters and the grid objects
	private void generateAllGridObjects()
	{
		allGridObjects.clear();
		allGridObjects.addAll(gridObjects);
		allGridObjects.addAll(characters.values());
		
		System.out.println("Shit in characters.values(): " + characters.values().size());
		System.out.println("Shit in gridObjects: " + gridObjects.size());
		System.out.println("Shit in allGridObjects: " + allGridObjects.size());
		
		// depth-sort the objects again since the collection was modified
		depthSort = true;
	}
	
	// add a character to the current room
	public void addCharacterToRoom(AStarCharacter character)
	{
		// check to see if this is the first time the user has been created, and if so, play the music
		if(myCharacter.getEmail().equals("") && !uiObject.getEmail().equals(""))
		{
			// make sure sounds are enabled before we play the sound
			if(startSounds)
			{
				playIntroMusic();
			}
		}
		
		// add the character to the HashMap
		// make sure it's not the character already referenced for this user
		if(character != null)
		{
			if(character.getEmail().equals(uiObject.getEmail()))
			{
				System.out.println("Character (" + uiObject.getUsername() + ") already exists in room");
				
				myCharacter = character;
				myCharacter.setUsername(uiObject.getUsername());
				
				// set the content rating and the currently-selected clothing
				settingsWindow.setSelectedRating(myCharacter.getContentRatingAsString());
				clothingWindow.setCurrentlySelectedClothing();

				// if this is the first time the room is loaded...
				if(!uiObject.theGridView.isVisible() || roomLoading == true)
				{
					// make sure the correct size is set
					myCharacter.changeAvatarSizeForTile(tileWidth, tileHeight);
					
					// find an exit tile to start on
					//if(character.getCurrentTile().getType() != Tile.TILE_EXIT)
					//{
					if(character.getCurrentTile() == null)
					{
						Iterator<Tile> it = tilesMap.values().iterator();
						while(it.hasNext())
						{
							Tile t = it.next();
							
							if(t.getType() == Tile.TILE_EXIT)
							{
								// set the position to this exit tile
								myCharacter.setRow(t.getRow());
								myCharacter.setCol(t.getColumn());
								myCharacter.setCurrentTile(tilesMap.get(t.getRow() + "-" + t.getColumn()));
								myCharacter.setX(t.getX());
								myCharacter.setY(t.getY());
								
								character.setRow(t.getRow());
								character.setCol(t.getColumn());
								character.setCurrentTile(tilesMap.get(t.getRow() + "-" + t.getColumn()));
								character.setX(t.getX());
								character.setY(t.getY());
								break;
							}
						}
					}
				}
				else
				{
					// set the existing position
					myCharacter.setRow(character.getRow());
					myCharacter.setCol(character.getCol());
					myCharacter.setCurrentTile(tilesMap.get(character.getRow() + "-" + character.getCol()));
					myCharacter.setX(character.getCurrentTile().getX());
					myCharacter.setY(character.getCurrentTile().getY());
				}
				
				characters.put(uiObject.getUsername(), myCharacter);
				
				//uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(myCharacter, roomID));
				
				// hide the loading window
				roomLoading = false;
				uiObject.showLoadingWindow(false, false);
				setVisible(true);
				
				return;
			}
			else
			{
				// set the character's current tile
				character.setCurrentTile(tilesMap.get(character.getRow() + "-" + character.getCol()));
				character.setX(character.getCurrentTile().getX());
				character.setY(character.getCurrentTile().getY());
				
				// make sure the correct avatar size is set
				character.changeAvatarSizeForTile(tileWidth, tileHeight);
				
				// add the character to the HashMap
				characters.put(character.getUsername(), character);
				
				System.out.println("Character (" + character.getUsername() + ") added to room at " + character.getRow() + "-" + character.getCol());
			}
			
			// create the charactersArray array of characters
			generateAllGridObjects();
		}
	}
	
	// remove a character from the current room
	public void removeCharacterFromRoom(String username)
	{
		characters.remove(username);
		
		// create the charactersArray array of characters
		generateAllGridObjects();
		
		System.out.println("Character (" + username + ") removed from room");
	}
	
	// move a character in the current room
	public void moveCharacterInRoom(AStarCharacter character, int destGridX, int destGridY)
	{
		// make sure it's not a nogo tile
		if(tilesMap.get(destGridY + "-" + destGridX).getType() == Tile.TILE_NOGO) {return;}
		
 		// make sure the character is still in the room
 		if(character != null)
 		{
 			// send a "move character" message to the server to update all clients
 			uiObject.sendMessageToServer(new MessageMoveCharacter(myCharacter, roomID, (gridX / 2), gridY));
 			
 			// get the current room configuration
     		character.setPathfinderTiles(tilesMap);
     		
	 		// process a pathfinding operation for the character
	 		//System.out.println("Setting current tile: " + character.getRow() + "-" + character.getCol());
	 		//System.out.println("Tile type: " + tilesMap.get(character.getRow() + "-" + character.getCol()).getTypeString());
	 		character.setCurrentTile(tilesMap.get(character.getRow() + "-" + character.getCol()));
	 		character.clearPath();
	 		character.setPath(character.getPathfinder().getPath(character.getCurrentTile(), tilesMap.get(destGridY + "-" + destGridX)));
	 		
	 		characters.put(character.getUsername(), character); // put the character back in the HashMap
	 		
	 		// create the charactersArray array of characters
			generateAllGridObjects();
	 		
	 		// check if it's the same character as the current user's
	 		if(character.getUsername().equals(myCharacter.getUsername()))
	 		{
	 			myCharacter = character;
	 		}
 		}
	}
	
	// get a character in the current room
	public synchronized AStarCharacter getCharacterInRoom(String username)
	{
		return characters.get(username);
	}
	
	// update a character in the current room
	public void updateCharacterInRoom(AStarCharacter character)
	{
		if(character != null)
		{
			// make sure we're not updating the current character for this user
			if(!character.getUsername().equals(myCharacter.getUsername()))
			{
				characters.put(character.getUsername(), character); // put the character in the HashMap
				
				// create the charactersArray array of characters
				generateAllGridObjects();
			}
		}
	}
	
	// update the signature and clothing items of myCharacter
	public void updateUserSignatureAndClothing(String signature, String shirtID, String shoesID, String pantsID, String hatID)
	{	
		// check to see if the signature needs to be updated
		if(!myCharacter.getSignature().equals(signature))
		{
			// update the signature
			myCharacter.setSignature(signature);
			
			// send the update message to the server
			uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(myCharacter, roomID));
		}
		
		// check to see if at least one clothing item has changed in order to send the Update Clothing message
		if(!shirtID.equals(myCharacter.getShirtID()) ||
		   !shoesID.equals(myCharacter.getShoesID()) ||
		   !pantsID.equals(myCharacter.getPantsID()) ||
		   !hatID.equals(myCharacter.getHatID()))
		{
			
			// update the clothing information first
			if(!shirtID.equals("")) {myCharacter.setShirtID(shirtID);}
			if(!shoesID.equals("")) {myCharacter.setShoesID(shoesID);}
			if(!pantsID.equals("")) {myCharacter.setPantsID(pantsID);}
			myCharacter.setHatID(hatID);
			
			// send the update clothing message to the server
			uiObject.sendMessageToServer(new MessageUpdateCharacterClothing(myCharacter, roomID));
		}
	}
	
	// add a friend request to the Messages window
	public void addFriendRequest(String from)
	{
		messagesWindow.addFriendRequest(from);
	}
	
	// add a friend to the Messages window
	public void addFriendToList(String friend)
	{
		messagesWindow.addFriendToList(friend);
	}
	
	// check if myCharacter is friends with another character
	public boolean isFriendsWith(String username)
	{
		return messagesWindow.friendsListContains(username);
	}
	
	// remove a friend from the Messages window
	public void removeFriendFromList(String friend)
	{
		messagesWindow.removeFriendFromList(friend);
	}
	
	// send a friend request
	public void sendFriendRequest(String recipient)
	{
		uiObject.sendMessageToServer(new MessageAddFriendRequest(myCharacter.getUsername(), recipient));
	}
	
	// send a friend request confirmation
	public void sendFriendRequestConfirmation(String recipient, boolean approved)
	{
		uiObject.sendMessageToServer(new MessageAddFriendConfirmation(myCharacter.getUsername(), recipient, approved));
	}
	
	// send a friend deletion message
	public void sendDeleteFriendMessage(String recipient)
	{
		uiObject.sendMessageToServer(new MessageRemoveFriend(myCharacter.getUsername(), recipient));
	}
	
	// set the player's friends list
	public void setFriendsList(FriendsList friendsList)
	{
		messagesWindow.setFriendsList(friendsList);
	}
	
	// add a mail message to the Messages window
	public void addMailMessage(MailMessage m)
	{
		messagesWindow.addMailMessage(m);
	}
	
	// send a mail message to another user
	public void sendMailMessage(String recipient, String message)
	{
		uiObject.sendMessageToServer(new MessageSendMailToUser(myCharacter.getUsername(), recipient, message, new Date()));
	}
	
	// send a "Save Mail" message to the server
	public void sendSaveMailMessage(ArrayList<MailMessage> messages)
	{
		uiObject.sendMessageToServer(new MessageSaveMailMessages(myCharacter.getUsername(), messages));
	}
	
	// set a user's mail messages
	public void setMailMessages(ArrayList<MailMessage> messages)
	{
		messagesWindow.setMailMessages(messages);
	}
	
	// set whether a given friend is online
	public void setFriendOnline(String friend, boolean online)
	{
		messagesWindow.setFriendOnline(friend, online);
	}
	
	// set the player's inventory
	public void setInventory(ArrayList<InventoryItem> inventory)
	{
		inventoryWindow.setInventory(inventory);
	}
	
	// show the map
	public void showMap()
	{
		mapWindow.setVisible(true);
	}
	
	// hide the map
	public void hideMap()
	{
		mapWindow.setVisible(false);
	}
	
	// toggle the "Guest Rooms" window
	public void toggleGuestRoomsWindow()
	{
		guestRoomsWindow.toggleVisibility();
	}
	
	// change to another room
	public void changeRoom(String newRoomID)
	{
		new RoomLoaderThread(newRoomID, this).start();
	}
	
	// send an "Update Character" message to the server
	public void sendUpdateCharacterMessage(AStarCharacter character)
	{
		new CharacterUpdaterThread(character, roomID).start();
	}
	
	// set the current room name
	public void setRoomInformation(String roomID, String roomName)
	{
		this.roomID = roomID;
		this.roomName = roomName;
	}
	
	// check whether a given username has a staff prefix
	private boolean isStaffMember(String username)
	{
		if(username.toLowerCase().startsWith("host_")) {return true;}
		if(username.toLowerCase().startsWith("vmk_")) {return true;}
		if(username.toLowerCase().startsWith("qa_")) {return true;}
		
		return false;
	}
	
	// set the size of the tiles and also the number of rows/columns
	public void changeTileSize(int width, int height)
	{
		// prevent a stack overflow error
		if(width == tileWidth && height == tileHeight) {return;}
		
		// set the loading flag
		//loading = true;
		
		// set the columns and tile width
		if(width == 64) {tileColumns = 13;}
		if(width == 48) {tileColumns = 17;}
		if(width == 32) {tileColumns = 25;}
		tileWidth = width;
		
		// set the rows and tile height
		if(height == 32) {tileRows = 38;}
		if(height == 24) {tileRows = 47;}
		if(height == 16) {tileRows = 71;}
		tileHeight = height;
		
		// set the paths and image icons
		reticleExit = AppletResourceLoader.getImageFromJar("tiles_img/exit_reticle_" + width + ".png"); // "walk" reticle
		reticleNogo = AppletResourceLoader.getImageFromJar("tiles_img/nogo_reticle_" + width + ".png"); // "nogo" reticle
		reticleWalk = AppletResourceLoader.getImageFromJar("tiles_img/walk_reticle_" + width + ".png"); // "exit" reticle
		
		// initialize the tiles
		initTilesMap();
		
		// clear the loading flag
		//loading = false;
	}
	
	public String getTileSize()
	{
		return tileWidth + "x" + tileHeight;
	}
	
	public ArrayList<RoomItem> getRoomItems() {
		return items;
	}
	
	// add an item to the room
	public boolean addRoomItem(RoomItem item)
	{
		if(roomInfo.get("OWNER").equals(myCharacter.getUsername()))
		{
			item.setRow(myCharacter.getRow());
			item.setCol(myCharacter.getCol());
			item.setOwner(myCharacter.getUsername());
			items.add(item);
			
			// save the room items
			saveRoomItems();
			
			// update the creation of this new item for everyone in the room
			uiObject.sendMessageToServer(new MessageUpdateItemInRoom(roomID, item));
			
			return true;
		}
		
		return false;
	}
	
	public void setRoomItems(ArrayList<RoomItem> _items)
	{
		String roomOwner = roomInfo.get("OWNER");
		items = _items;
		for(int i = 0; i < items.size(); i++)
		{
			// set the owner of the item
			items.get(i).setOwner(roomOwner);
		}
	}
	
	// update a given item in the room
	public void updateRoomItem(RoomItem item)
	{
		items.remove(item);
		items.add(item);
	}
	
	public RoomItem getSelectedRoomItem() {
		return currentRoomItem;
	}
	
	public void clearSelectedRoomItem(boolean keepItem) {
		if(keepItem)
		{
			// keep the item in the room
			items.add(currentRoomItem);
			
			// send the update item message to the server
			uiObject.sendMessageToServer(new MessageUpdateItemInRoom(roomID, currentRoomItem));
			
			// save the guest room items
			saveRoomItems();
		}
		else
		{
			// move the item back to the player's inventory
			inventoryWindow.addInventory(new InventoryItem(currentRoomItem.getName(), currentRoomItem.getId(), currentRoomItem.getType()));
			
			// save the guest room items
			saveRoomItems();
		}
		currentRoomItem = null;
	}
	
	
	
	// set whether a selected item can be moved
	public void setDesignMoveMode(boolean designMoveMode) {
		this.designMoveMode = designMoveMode;
	}
	
	// save the room items to the Guest Room file
	public void saveRoomItems()
	{
		// send the save message to the server
		uiObject.sendMessageToServer(new MessageSaveGuestRoom(items, roomInfo));
	}
	
	// send a "Create Guest Room" message to the server
	public void sendCreateGuestRoomMessage(HashMap<String,String> info)
	{
		uiObject.sendMessageToServer(new MessageCreateGuestRoom(info));
	}
	
	public void setRoomInfo(HashMap<String,String> roomInfo)
	{
		this.roomInfo = roomInfo;
	}
	
	public HashMap<String,String> getRoomInfo() {
		return roomInfo;
	}
	
	// add information to the Room Info map
	public void addRoomInfo(String key, String value)
	{
		roomInfo.put(key, value);
	}
	
	// get the credits for my character
	public long getMyCredits()
	{
		return myCharacter.getCredits();
	}
	
	// set the credits for my character
	public void setMyCredits(long credits)
	{
		myCharacter.setCredits(credits);
	}
	
	public void setInventoryPinsWorn(InventoryInfo wornPins[])
	{
		inventoryWindow.setPinsWorn(wornPins);
	}
	
	// send an Update Inventory message to the server
	public void sendUpdateInventoryMessage(ArrayList<InventoryItem> inventory)
	{
		uiObject.sendMessageToServer(new MessageUpdateInventory(myCharacter.getUsername(), inventory));
	}
	
	// send an Add Inventory message to the server
	public void sendAddInventoryMessage(InventoryItem item)
	{
		// add the newly purchased item to the Inventory window
		inventoryWindow.addPurchasedItem(item);
		
		// send the message to the server
		uiObject.sendMessageToServer(new MessageAddInventory(myCharacter.getUsername(), item));
	}
	
	// add a clothing item to the Clothing window from another window or thread
	public void addClothingItemToClothingWindow(InventoryItem item, boolean sortCollections)
	{
		clothingWindow.addClothingItem(item, sortCollections);
	}
	
	// re-add the character to the characters HashMap
	public void updateCharacterClothing(AStarCharacter character)
	{
		// update the character in the structure
		characters.put(character.getUsername(), character);
		
		if(character.getUsername().equals(myCharacter.getUsername()))
		{
			// update the myCharacter object if necessary
			myCharacter = character;
		}
	}
	
	public void showGameArea(String gameArea)
	{
		uiObject.showGameArea(gameArea);
	}
	
	public void hideGameArea(String gameArea)
	{
		uiObject.hideGameArea(gameArea);
	}
	
	// set the description on the loading window
	public void setLoadingDescription(String description)
	{
		uiObject.setLoadingDescription(description);
	}
	
	// set the clickable areas for the grid
	public void setClickableAreas(ArrayList<ClickableArea> clickableAreas)
	{
		this.clickableAreas = clickableAreas;
	}
	
	// return the clickable areas for the grid
	public ArrayList<ClickableArea> getClickableAreas() {return clickableAreas;}
	
	// set the objects for the grid
	public void setGridObjects(ArrayList<GridSortable> gridObjects)
	{
		this.gridObjects = gridObjects;
	}
	
	// return the objects for the grid
	public ArrayList<GridSortable> getGridObjects() {return gridObjects;}
	
	// thread that serves as the character updater thread so the character can be updated properly
	class CharacterUpdaterThread extends Thread
	{
		private AStarCharacter character = null;
		private String roomID = "";
		
		public CharacterUpdaterThread(AStarCharacter character, String roomID)
		{
			this.character = character;
			this.roomID = roomID;
		}
		
		public void run()
		{
			// send the update message to the server
			uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(character, roomID));
		}
	}
	
	// thread that serves as the room loader so painting and updates can still take place while a room loads
	class RoomLoaderThread extends Thread
	{
		String newRoomID = "";
		RoomViewerGrid grid = null;
		
		public RoomLoaderThread(String newRoomID, RoomViewerGrid grid)
		{
			this.newRoomID = newRoomID;
			this.grid = grid;
		}
		
		public void run()
		{
			String newRoomName = StaticAppletData.getRoomMapping(newRoomID).getRoomName();
			
			// hide the map and the grid so nothing obscures the loading window
			mapWindow.setVisible(false);
			grid.setVisible(false);
			
			// show the loading window
			uiObject.showLoadingWindow(newRoomName, "Room loading... please wait", true, true);
			
			// stop any sounds currently playing
			stopSounds();
			
			// set the current room item in its current position if it exists
			if(currentRoomItem != null)
			{
				items.add(currentRoomItem);
				
				// clear the current room item
				currentRoomItem = null;
				
				// save the room items
				saveRoomItems();
			}
			
			roomLoading = true;
			
			// remove this player from the current room
			uiObject.sendMessageToServer(new MessageRemoveUserFromRoom(myCharacter.getUsername(), roomID));
			
			// remove all users from the room
			characters.clear();
			
			// remove all chat bubbles from the room
			theChatBubbles.clearAll();
			
			// remove the clickable areas from the room
			clickableAreas.clear();
			
			// remove the grid objects from the room
			gridObjects.clear();
			
			// load the room file
			if(newRoomID.startsWith("gr"))
			{
				// load a guest room
				FileOperations.loadGuestRoom(StaticAppletData.getRoomMapping(newRoomID).getRoomPath(), grid);
			}
			else
			{
				// load a public room
				FileOperations.loadFile(AppletResourceLoader.getFileFromJar(StaticAppletData.getRoomMapping(newRoomID).getRoomPath()), grid);
			}
		  	
		  	// find an exit tile to start on
		  	myCharacter.setCurrentTile(null);
			Iterator<Tile> it = tilesMap.values().iterator();
			while(it.hasNext())
			{
				Tile t = it.next();
				
				// check and make sure the tile's destination is the same ID as the room
				// this character just exited from
				if(t.getType() == Tile.TILE_EXIT)
				{
					if(t.getDest().equals(roomID) || roomID.equals(""))
					{
						// set the position to this exit tile
						myCharacter.setRow(t.getRow());
						myCharacter.setCol(t.getColumn());
						myCharacter.setCurrentTile(tilesMap.get(t.getRow() + "-" + t.getColumn()));
						myCharacter.setX(t.getX());
						myCharacter.setY(t.getY());
						break;
					}
				}
			}
			
			// no suitable EXIT tile found above, so default to any exit tile
			if(myCharacter.getCurrentTile() == null)
			{
				Iterator<Tile> it2 = tilesMap.values().iterator();
				while(it2.hasNext())
				{
					Tile t = it2.next();
					
					// check and make sure the tile's destination is the same ID as the room
					// this character just exited from
					if(t.getType() == Tile.TILE_EXIT)
					{
						// set the position to this exit tile
						myCharacter.setRow(t.getRow());
						myCharacter.setCol(t.getColumn());
						myCharacter.setCurrentTile(tilesMap.get(t.getRow() + "-" + t.getColumn()));
						myCharacter.setX(t.getX());
						myCharacter.setY(t.getY());
						break;
					}
				}
			}
		  	
			// make sure the correct size is set
			myCharacter.changeAvatarSizeForTile(tileWidth, tileHeight);
			
		  	// add this player to the new room
		  	uiObject.sendMessageToServer(new MessageAddUserToRoom(myCharacter, newRoomID, newRoomName));
		  	
		  	// set the room name and ID
		  	roomID = newRoomID;
		  	roomName = newRoomName;
		  	
		  	// hide the loading window
		  	uiObject.showLoadingWindow(false, false);
		  	
		  	// set the room information window data
		  	roomDescriptionWindow.setRoomID(roomInfo.get("ID"));
		  	roomDescriptionWindow.setRoomOwner(roomInfo.get("OWNER"));
		  	roomDescriptionWindow.setRoomName(roomInfo.get("NAME"));
		  	roomDescriptionWindow.setRoomDescription(roomInfo.get("DESCRIPTION"));
		  	
		  	uiObject.theGridView.setVisible(true);
		}
	}
}

class GridViewMovementImageObserver implements ImageObserver
{
	RoomViewerUI theWindow;
	public GridViewMovementImageObserver(RoomViewerUI theWindow) {this.theWindow = theWindow;}
	public GridViewMovementImageObserver() {}
	public GridViewMovementImageObserver(RoomViewerGrid gridView) {}
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h)
	{
		//System.out.println("Image updated");
		//theWindow.repaint();
	    return true;
	}
}