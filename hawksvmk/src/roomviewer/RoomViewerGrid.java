package roomviewer;

//RoomViewerGrid.java by Matt Fritz
//March 26, 2009
//Class that implements the grid portion of the Room Viewer

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
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import chat.ChatBubble;
import chat.ChatBubbles;

import animations.Animation;
import astar.AStarCharacter;

import sockets.messages.MessageAddFriendConfirmation;
import sockets.messages.MessageAddFriendRequest;
import sockets.messages.MessageAddUserToRoom;
import sockets.messages.MessageMoveCharacter;
import sockets.messages.MessageRemoveFriend;
import sockets.messages.MessageRemoveUserFromRoom;
import sockets.messages.MessageSaveMailMessages;
import sockets.messages.MessageSendMailToUser;
import sockets.messages.MessageUpdateCharacterInRoom;
import sounds.SoundPlayable;
import tiles.Tile;
import ui.WindowAvatarInformation;
import ui.WindowClothing;
import ui.WindowHelp;
import ui.WindowInventory;
import ui.WindowMap;
import ui.WindowMessages;
import ui.WindowRoomDescription;
import ui.WindowSettings;
import ui.WindowShop;
import util.AppletResourceLoader;
import util.FileOperations;
import util.FriendsList;
import util.InventoryItem;
import util.MailMessage;
import util.StaticAppletData;

public class RoomViewerGrid extends JPanel implements GridViewable, Runnable
{
	private Thread gridThread;
	private int graphicsDelay = 20; // milliseconds between each frame
	
	String backgroundImagePath = "img/ui/loading_room_vmk.png";
	//String backgroundImagePath = "tiles_img/test_room_image.png";
	ImageIcon backgroundImage = AppletResourceLoader.getImageFromJar(backgroundImagePath);
	
	ImageIcon nogoTileImage = null; //new Tile().getNogoTileImage();
	ImageIcon walkTileImage = null; //new Tile().getWalkTileImage();
	ImageIcon exitTileImage = null; //new Tile().getExitTileImage();
	
	Tile currentTile = new Tile(0,0,Tile.TILE_WALK); // currently selected tile type
	
	ImageIcon reticleExit = AppletResourceLoader.getImageFromJar("tiles_img/exit_reticle.png"); // "walk" reticle
	ImageIcon reticleNogo = AppletResourceLoader.getImageFromJar("tiles_img/nogo_reticle.png"); // "nogo" reticle
	ImageIcon reticleWalk = AppletResourceLoader.getImageFromJar("tiles_img/walk_reticle.png"); // "exit" reticle
	
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
	
	boolean startSounds = false; // true to start the sounds
	ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>(); // ArrayList of sounds
	//RepeatingSound theSound = new RepeatingSound(0,"sound/sub_ping.wav"); // test repeating sound
	
	ChatBubbles theChatBubbles; // data structure for the chat bubbles
	
	Font textFont; // font used by the text bubbles
	Font textFontBold; // bold font used by the text bubbles (for username display)
	Rectangle2D maxFontBounds; // maximum bounds of the font
	FontRenderContext frc; // render context for the font
	
	RoomViewerUI uiObject;
	
	// room description window
	WindowRoomDescription roomDescriptionWindow;
	
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
	
	// map window
	WindowMap mapWindow;
	
	// Pathfinding stuff
	HashMap<String,AStarCharacter> characters = new HashMap<String,AStarCharacter>(); // all characters in this rom
	AStarCharacter myCharacter = new AStarCharacter(); // the single specific character for this client
	
	String roomName = ""; // name of the current room
	boolean roomLoading = false;
	
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
				paint(this.getGraphics());
			}
			
			try
			{
				Thread.sleep(graphicsDelay);
			}
			catch(Exception e) {}
		}
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
	
	public void loadGridView()
	{	 
		this.addMouseListener(new MouseAdapter()
     {
     	public void mouseReleased(MouseEvent e)
     	{	
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
	     		
	     		if(roomDescriptionWindow != null)
	     		{
	     			if(roomDescriptionWindow.isVisible())
	     			{	
	     				// check to see if the user clicked on the "X"
	     				if(roomDescriptionWindow.getExitButtonRectAbsolute().contains(mousePoint))
	     				{
	     					// hide the Room Description window
	     					roomDescriptionWindow.toggleVisibility();
	     					
	     					return;
	     				}
	     			}
	     		}
	     		
	     		// check to see if we clicked inside a bounding box
	     		for(AStarCharacter c : characters.values())
	     		{
	     			if(c.getBoundingBox().contains(mousePoint))
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
	     				
	     				avatarInfoWindow.setUsername(c.getUsername());
	     				avatarInfoWindow.setSignature(c.getSignature());
	     				avatarInfoWindow.setBadges(c.getDisplayedBadges());
	     				avatarInfoWindow.setPins(c.getDisplayedPins());
	     				avatarInfoWindow.setVisible(true);
	     				
	     				System.out.println("Clicked bounding box for character: " + c.getUsername());
	     				
	     				convertMouseToGridCoords(); // convert the mouse coords back to grid coords
	     				
	     				return;
	     			}
	     		}
	     		
	     		convertMouseToGridCoords(); // convert the mouse coords back to grid coords
	     		
	     		// move the character locally
	     		moveCharacterInRoom(myCharacter, (gridX / 2), gridY);
	     		
	     		// send a "move character" message to the server to update all clients
	     		uiObject.sendMessageToServer(new MessageMoveCharacter(myCharacter, roomName, (gridX / 2), gridY));
	     		
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
	
	public void paint(Graphics g)
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
				
				bufferGraphics.drawImage(reticleTile.getImage(), mouseX - (tileWidth / 2), mouseY - (tileHeight / 2), new GridViewMovementImageObserver(this));
			
				// draw the animations
				for(Animation anim : animations)
				{
					bufferGraphics.drawImage(anim.getNextFrame().getImage(), anim.getX(), anim.getY(), this);
				}
				
				// draw the path to the target tile for each character
				//for(int characterCount = 0; characterCount < characters.values().size(); characterCount++)
				//for(AStarCharacter character : characters.values())
				for(int characterCount = 0; characterCount < characters.values().size(); characterCount++)
				{
					// get a character
					AStarCharacter character = (AStarCharacter)characters.values().toArray()[characterCount];
					
					if(character.getPath() != null)
					{
						if(character.getPath().size() > 0)
						{
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
												uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(character, roomName));
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
											uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(character, roomName));
										}
									}
								}
							}
						}
					}
					
					// draw the character
					if(character != null)
					{
						bufferGraphics.drawImage(character.getImage(), character.getX(), character.getY() - character.getImage().getHeight(this) + 32, this);
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
				
				// draw the room description window
				if(roomDescriptionWindow != null)
				{
					if(roomDescriptionWindow.isVisible())
					{
						bufferGraphics.drawImage(roomDescriptionWindow.getImage(), roomDescriptionWindow.getX(), roomDescriptionWindow.getY(), this);
					}
				}
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
			s.stop();
		}
		theChatBubbles.stop();
		theChatBubbles.clearAll();
	}
	
	private void setupInternalUI()
	{		
		 // set up the room description window
	     roomDescriptionWindow = new WindowRoomDescription(textFont, textFontBold, "Walk Test", "This is a walk test room.  You can try out the\nfeatures of the game, including chat and walking.\nPlease feel free to wander around.\n\n- Hawk's VMK: Development Team", 0, 424);
		 roomDescriptionWindow.setRoomTitleX(125);
		 roomDescriptionWindow.setDrawingSurface(createImage(323,148));
		 roomDescriptionWindow.setVisible(false);
		 
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
		// draw the grid
		for(int i = 0; i < tileColumns; i++) // columns (3)
		{
			for(int j = 0; j < tileRows; j++) // rows (4)
			{	
				// put the tile into the HashMap
				tilesMap.put(j + "-" + i, new Tile(j,i,Tile.TILE_NOGO));
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
	public void setTilesMap(HashMap<String,Tile> tilesMap)
	{
		//pathfinder.setTiles(tilesMap);
		
		// set up the character
		myCharacter.setUsername(uiObject.getUsername());
		myCharacter.setCurrentTile(tilesMap.get("15-7"));
		myCharacter.setX(myCharacter.getCurrentTile().getX());
		myCharacter.setY(myCharacter.getCurrentTile().getY());
		characters.put(uiObject.getUsername(), myCharacter);
		
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
	
	// set the sounds
	public void setSounds(ArrayList<SoundPlayable> sounds)
	{
		this.sounds = sounds;
		
		// set up the sounds
		if(startSounds)
		{
			setupSounds();
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
	
	// add a character to the current room
	public void addCharacterToRoom(AStarCharacter character)
	{
		// add the character to the HashMap
		// make sure it's not the character already referenced for this user
		if(character != null)
		{
			if(character.getEmail().equals(uiObject.getEmail()))
			{
				System.out.println("Character (" + uiObject.getUsername() + ") already exists in room");
				myCharacter = character;
				myCharacter.setUsername(uiObject.getUsername());
				
				// if this is the first time the room is loaded...
				if(!uiObject.theGridView.isVisible() || roomLoading == true)
				{
					System.out.println("The room is LOADING, buttknocker");
					// find an exit tile to start on
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
				
				uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(myCharacter, roomName));
				
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
				
				// add the character to the HashMap
				characters.put(character.getUsername(), character);
				
				System.out.println("Character (" + character.getUsername() + ") added to room at " + character.getRow() + "-" + character.getCol());
			}
		}
	}
	
	// remove a character from the current room
	public void removeCharacterFromRoom(String username)
	{
		characters.remove(username);
		
		System.out.println("Character (" + username + ") removed from room");
	}
	
	// move a character in the current room
	public void moveCharacterInRoom(AStarCharacter character, int destGridX, int destGridY)
	{
 		// make sure the character is still in the room
 		if(character != null)
 		{
 			// get the current room configuration
     		character.setPathfinderTiles(tilesMap);
     		
	 		// process a pathfinding operation for the character
	 		System.out.println("Setting current tile: " + character.getRow() + "-" + character.getCol());
	 		System.out.println("Tile type: " + tilesMap.get(character.getRow() + "-" + character.getCol()).getTypeString());
	 		character.setCurrentTile(tilesMap.get(character.getRow() + "-" + character.getCol()));
	 		character.clearPath();
	 		character.setPath(character.getPathfinder().getPath(character.getCurrentTile(), tilesMap.get(destGridY + "-" + destGridX)));
	 		
	 		characters.put(character.getUsername(), character); // put the character back in the HashMap
	 		
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
			}
		}
	}
	
	public void updateUserSignature(String username, String signature)
	{
		AStarCharacter character = characters.get(username); // get the character
		character.setSignature(signature); // set the signature
		
		// send the update message to the server
		uiObject.sendMessageToServer(new MessageUpdateCharacterInRoom(character, roomName));
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
	
	// change to another room
	public void changeRoom(String newRoomName)
	{
		// show the loading window
		uiObject.showLoadingWindow(newRoomName, "Room loading... please wait", true, true);
		
		roomLoading = true;
		
		uiObject.theGridView.setVisible(false);
		
		// hide the map
		mapWindow.setVisible(false);
		
		// remove this player from the current room
		uiObject.sendMessageToServer(new MessageRemoveUserFromRoom(myCharacter.getUsername(), roomName));
		
		// remove all users from the room
		characters.clear();
		
		// remove all chat bubbles from the room
		theChatBubbles.clearAll();
		
		// load the room file
	  	FileOperations.loadFile(AppletResourceLoader.getFileFromJar(StaticAppletData.getRoomMapping(newRoomName).getRoomPath()), this);
	  	
	  	// find an exit tile to start on
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
				break;
			}
		}
	  	
	  	// add this player to the new room
	  	uiObject.sendMessageToServer(new MessageAddUserToRoom(myCharacter, newRoomName));
	  	
	  	// set the room name
	  	roomName = newRoomName;
	  	
	  	// hide the loading window
	  	uiObject.showLoadingWindow(false, false);
	  	
	  	uiObject.theGridView.setVisible(true);
	}
	
	// set the current room name
	public void setRoomName(String roomName)
	{
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

