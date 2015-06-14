// RoomViewerUI.java by Matt Fritz
// November 10, 2009
// Class for the Room Viewer window

package roomviewer;

import games.GameScore;
import games.fireworks.GameFireworks;
import games.pirates.GamePirates;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import astar.AStarCharacter;
import astar.AStarCharacterBasicData;

import login.LoginModule;
import mainProgram.VMKClient;

import roomobject.RoomItem;
import sockets.messages.MessageSecure;
import sockets.messages.MessageAddChatToRoom;
import sockets.messages.MessageLogout;
import sockets.messages.MessageSendMailToUser;
import sockets.messages.games.MessageGameAddUserToRoom;
import sockets.messages.games.MessageGameMoveCharacter;
import sockets.messages.games.MessageGameRemoveUserFromRoom;
import sockets.messages.games.MessageGameScore;
import sockets.messages.games.pirates.MessageGamePiratesFireCannons;
import ui.WindowLoading;
import util.Dictionary;
import util.FileOperations;
import util.AppletResourceLoader;
import util.FriendsList;
import util.GameConstants;
import util.InventoryItem;
import util.MailMessage;
import util.StaticAppletData;

public class RoomViewerUI extends Applet
{
	private boolean release = true; // TRUE to change the window display
	private boolean windowClosing = false; // TRUE when the window is closing
	
	private VMKClient theVMKClient; // VMK client socket connection

	private String filename = GameConstants.PATH_ROOMS_TEMPLATES + "gr4.room";
	private AStarCharacterBasicData basicAvatarData; // structure to hold basic avatar information
	
	private JTextField chatTextBox;
	private Font textFont;
	private Font textFontBold;
	
	private int maximumChatCharacters = 90; // maximum number of chat characters
	
	private JLabel loadingBackground;
	private WindowLoading loadingWindow;
	RoomViewerGrid theGridView;
	private String roomID = "";
	private String roomName = "";
	
	private boolean roomDescriptionWindowVisible = false;
	private boolean messagesWindowVisible = false;
	private boolean inventoryWindowVisible = false;
	private boolean shopWindowVisible = false;
	private boolean clothingWindowVisible = false;
	private boolean settingsWindowVisible = false;
	private boolean helpWindowVisible = false;
	private boolean avatarInfoWindowVisible = false;
	private boolean mapWindowVisible = true;
	private boolean loadingWindowVisible = false;
	
	JLabel toolbar_left;
	JLabel toolbar_right;
	JLabel toolbar;
	
	// Toolbar (left) button bounds
	Rectangle infoButtonRect = new Rectangle(7,3,24,24); // bounds of the "I" button
	Rectangle globeButtonRect = new Rectangle(38,3,28,18); // bounds of the globe button
	Rectangle inventoryButtonRect = new Rectangle(70,3,28,18); // bounds of the inventory button
	Rectangle messagesButtonRect = new Rectangle(102,3,28,18); // bounds of the messages button
	Rectangle shopButtonRect = new Rectangle(134,3,28,18); // bounds of the "SHOP" button
	Rectangle questButtonRect = new Rectangle(166,3,18,18); // bounds of the "Q" button
	Rectangle emoticonsButtonRect = new Rectangle(191,1,32,24); // bounds of the heart button
	
	// Toolbar (right) button bounds
	Rectangle magicButtonRect = new Rectangle(2,1,35,21); // bounds of the magic button
	Rectangle cameraButtonRect = new Rectangle(44,3,29,18); // bounds of the camera button
	Rectangle clothingButtonRect = new Rectangle(76,3,28,18); // bounds of the clothing button
	Rectangle soundButtonRect = new Rectangle(108,3,28,20); // bounds of the sound button
	Rectangle helpButtonRect = new Rectangle(139,3,29,19); // bounds of the "HELP" button
	Rectangle exitButtonRect = new Rectangle(171,3,21,19); // bounds of the red "X" button
	
	// animation for a new message/friend request
	private JLabel messagesAnimationLabel;
	
	int loginOffsetX = 200;
	int loginOffsetY = 150;
	
	private RoomViewerUI roomViewerUI;
	
	// references to game area windows for use with showGameArea()
	private GameFireworks gameFireworks = null;
	private GamePirates gamePirates = null;
	
	public RoomViewerUI() {} // set the code base}
	
	public void init()
	{
		StaticAppletData.setCodeBase(getCodeBase().toString()); // set the code base
		StaticAppletData.setCodeBaseURL(getCodeBase()); // set the code base URL
		
		loadLoginUI(); // load the login UI first
		//loadRoomViewerUI();
	}
	
	public void start()
	{
		// show the UI elements again if they had been hidden
		/*if(theGridView != null)
		{
			theGridView.roomDescriptionWindow.setVisible(roomDescriptionWindowVisible);
			theGridView.messagesWindow.setVisible(messagesWindowVisible);
			theGridView.inventoryWindow.setVisible(inventoryWindowVisible);
			theGridView.shopWindow.setVisible(shopWindowVisible);
			theGridView.clothingWindow.setVisible(clothingWindowVisible);
			theGridView.settingsWindow.setVisible(settingsWindowVisible);
			theGridView.helpWindow.setVisible(helpWindowVisible);
			theGridView.avatarInfoWindow.setVisible(avatarInfoWindowVisible);
			theGridView.mapWindow.setVisible(mapWindowVisible);
			loadingWindow.setVisible(loadingWindowVisible);
		}*/
	}
	
	public void stop()
	{
		// hide the UI elements so they don't disappear forever
		if(theGridView != null)
		{
			// keep track of their visibility before they are all hidden
			roomDescriptionWindowVisible = theGridView.roomDescriptionWindow.isVisible();
			messagesWindowVisible = theGridView.messagesWindow.isVisible();
			inventoryWindowVisible = theGridView.inventoryWindow.isVisible();
			shopWindowVisible = theGridView.shopWindow.isVisible();
			clothingWindowVisible = theGridView.clothingWindow.isVisible();
			settingsWindowVisible = theGridView.settingsWindow.isVisible();
			helpWindowVisible = theGridView.helpWindow.isVisible();
			avatarInfoWindowVisible = theGridView.avatarInfoWindow.isVisible();
			mapWindowVisible = theGridView.mapWindow.isVisible();
			loadingWindowVisible = loadingWindow.isVisible();
			
			// hide the UI elements
			theGridView.roomDescriptionWindow.setVisible(false);
			theGridView.messagesWindow.setVisible(false);
			theGridView.inventoryWindow.setVisible(false);
			theGridView.shopWindow.setVisible(false);
			theGridView.clothingWindow.setVisible(false);
			theGridView.settingsWindow.setVisible(false);
			theGridView.helpWindow.setVisible(false);
			theGridView.avatarInfoWindow.setVisible(false);
			theGridView.mapWindow.setVisible(false);
			loadingWindow.setVisible(false);
		}
	}
	
	public void destroy()
	{
		// stop the client
		if(theVMKClient != null)
		{
			// check to see if the client socket is connected to the server
			if(theVMKClient.isClientConnected())
			{
				sendMessageToServer(new MessageLogout());
			}
			
			// manual logout by the client, either by closing the window or the client stopping execution
			windowClosing = true;
			
			// stop the client
			theVMKClient.stopClient();
		}
	}
	
	// remove the login portion and load the room viewer UI portion
	private void loginLoadRoomViewerUI()
	{
		// remove all login UI components
		this.removeAll();
		
		// load the Room Viewer UI
		loadRoomViewerUI();
	}
	
	// load the Login UI
	public void loadLoginUI()
	{
		String logoImagePath = GameConstants.PATH_UI_IMAGES + "hawkstersvmk_100px.png";
		String emailImagePath = GameConstants.PATH_UI_IMAGES + "email.png";
		String passwordImagePath = GameConstants.PATH_UI_IMAGES + "password.png";
		String loginImagePath = GameConstants.PATH_UI_IMAGES + "login.png";
		
		final LoginModule loginModule = new LoginModule();
		
		this.removeAll();
		
		// set general properties of the main window frame
		this.setPreferredSize(new Dimension(800, 600));
		this.setSize(800,600);
		this.setLayout(null);
		
		// logo label
		final JLabel logoLabel = new JLabel(AppletResourceLoader.getImageFromJar(logoImagePath));
		logoLabel.setBounds(new Rectangle(loginOffsetX + 10, loginOffsetY + 10, 100, 128));
		add(logoLabel);
		
		// Email label
		final JLabel emailLabel = new JLabel(AppletResourceLoader.getImageFromJar(emailImagePath));
		emailLabel.setBounds(new Rectangle(loginOffsetX + 125, loginOffsetY + 100, 75, 25));
		add(emailLabel);
		
		// Password label
		final JLabel passwordLabel = new JLabel(AppletResourceLoader.getImageFromJar(passwordImagePath));
		passwordLabel.setBounds(new Rectangle(loginOffsetX + 125, loginOffsetY + 130, 75, 25));
		add(passwordLabel);
		
		// Email text box
		final JTextField emailTextBox = new JTextField();
		emailTextBox.setBorder(null);
		emailTextBox.setBounds(new Rectangle(loginOffsetX + 205, loginOffsetY + 100, 150, 25));
		add(emailTextBox);
		
		// Password text box
		final JPasswordField passwordTextBox = new JPasswordField();
		passwordTextBox.setBorder(null);
		passwordTextBox.setBounds(new Rectangle(loginOffsetX + 205, loginOffsetY + 130, 150, 25));
		add(passwordTextBox);
		
		// "Error" label
		final JLabel errorLabel = new JLabel("* Invalid email/password combination *", JLabel.CENTER);
		errorLabel.setForeground(Color.ORANGE);
		errorLabel.setBackground(new Color(0, 128, 0));
		errorLabel.setBounds(new Rectangle(loginOffsetX + 0, loginOffsetY + 165, 400, 25));
		errorLabel.setVisible(false);
		add(errorLabel);
		
		// Login button
		final JButton loginButton = new JButton(AppletResourceLoader.getImageFromJar(loginImagePath));
		loginButton.setBackground(new Color(0, 128, 0));
		loginButton.setBounds(new Rectangle(loginOffsetX + 150, loginOffsetY + 200, 75, 25));
		loginButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// trim the email and password for whitespace upon a login attempt
				emailTextBox.setText(emailTextBox.getText().trim());
				passwordTextBox.setText(new String(passwordTextBox.getPassword()).trim());
				
				loginButton.setEnabled(false);
				emailTextBox.setEnabled(false);
				passwordTextBox.setEnabled(false);
				
				// check the login credentials
				if(loginModule.login(emailTextBox.getText(), new String(passwordTextBox.getPassword())))
				{
					// correct credentials and authentication
					errorLabel.setVisible(false);
					
					// load up the Room Viewer window
					if(!loginModule.getUsername().trim().equals(""))
					{
						// set basic properties based upon the login result
						roomViewerUI.setAvatarBasicData(new AStarCharacterBasicData(loginModule.getUsername(), emailTextBox.getText(), loginModule.getGender(), loginModule.getContentRating(), loginModule.getCredits()));
					}
					
					// close the login window
					loginLoadRoomViewerUI();
				}
				else
				{
					// incorrect credentials
					errorLabel.setText(loginModule.getErrorMessage());
					errorLabel.setVisible(true);
					
					loginButton.setEnabled(true);
					emailTextBox.setEnabled(true);
					passwordTextBox.setEnabled(true);
				}
			}
		});
		add(loginButton);
		
		// set a background color
		setBackground(Color.black); // black
		//setBackground(new Color(0,128,0)); // medium-green
		
	    // pack the window and display it
	    this.setName("Hawk's Virtual Magic Kingdom");
	    this.setVisible(true);
	    
	    repaint();
	    
	    roomViewerUI = this;
	}
	
	// load the Room Viewer UI after login
	public void loadRoomViewerUI()
	{
		// start the loader thread so the graphics can be drawn as the loading takes place
		new RoomViewerLoadingThread(roomViewerUI).start();
	}

	// set up the fonts
	private void setupFonts()
	{
		System.out.println("In setupFonts()");
		
		// set up the text fonts
		try
		{
			textFont = Font.createFont(Font.TRUETYPE_FONT, AppletResourceLoader.getFileFromJar(GameConstants.PATH_FONTS + "FOXLEY8_.ttf"));
			textFont = textFont.deriveFont(16.0f);
			textFontBold = textFont.deriveFont(Font.BOLD, 16.0f);
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN setupFonts(): " + e.getClass().getName() + " - " + e.getMessage());
		}
	}
	
	// get the basic avatar data structure
	public AStarCharacterBasicData getAvatarBasicData()
	{
		return basicAvatarData;
	}
	
	// set the basic avatar data structure
	public void setAvatarBasicData(AStarCharacterBasicData basicAvatarData)
	{
		this.basicAvatarData = basicAvatarData;
	}
	
	// add a character to the grid
	public void addCharacterToRoom(AStarCharacter character)
	{
		// make sure the current character hasn't been created on the grid yet
		if(character.getUsername().equals(basicAvatarData.getUsername()) && !theGridView.getMyCharacter().getEmail().equals(basicAvatarData.getEmail()))
		{
			// it's the current player's character, so set their displayed pins
			theGridView.setInventoryPinsWorn(character.getDisplayedPins());
		}
		
		theGridView.addCharacterToRoom(character);
	}
	
	// remove a user from the grid
	public void removeUserFromRoom(String username)
	{
		theGridView.removeCharacterFromRoom(username);
	}
	
	// add a chat bubble to the room
	public void addChatToRoom(String username, String text)
	{
		// make sure we're not sending the current user's recent chat again
		if(!basicAvatarData.getUsername().equals(username))
		{
			theGridView.addTextBubble(username, text, 100);
		}
	}
	
	// move a character on the grid
	public void moveCharacter(AStarCharacter character, int destGridX, int destGridY)
	{
		// make sure we're not moving the current user's character again
		if(!character.getUsername().equals(basicAvatarData.getUsername()))
		{
			theGridView.moveCharacterInRoom(character, destGridX, destGridY);
		}
	}
	
	// update a character in the room
	public void updateCharacterInRoom(AStarCharacter character)
	{
		theGridView.updateCharacterInRoom(character);
	}
	
	// add a friend request
	public void addFriendRequest(String from)
	{
		// TODO: Play a sound
		messagesAnimationLabel.setVisible(true);
		repaint();
		
		theGridView.addFriendRequest(from);
	}
	
	// add a user to this user's friends list
	public void addFriendToList(String friend)
	{
		theGridView.addFriendToList(friend);
	}
	
	// remove a friend from this user's friends list
	public void removeFriendFromList(String friend)
	{
		theGridView.removeFriendFromList(friend);
	}
	
	// set this user's friends list
	public void setFriendsList(FriendsList friendsList)
	{
		theGridView.setFriendsList(friendsList);
	}
	
	// add a mail message to the user's messages
	public void addMailMessage(MailMessage m)
	{
		// TODO: Play a sound and make the Messages button flash
		messagesAnimationLabel.setVisible(true);
		repaint();
		
		theGridView.addMailMessage(m);
	}
	
	// set the mail messages for this user
	public void setMailMessages(ArrayList<MailMessage> messages)
	{
		if(messages.size() > 0)
		{
			// TODO: Play a sound
			messagesAnimationLabel.setVisible(true);
			repaint();
			
			theGridView.setMailMessages(messages);
		}
	}
	
	// set whether a given friend is online
	public void setFriendOnline(String friend, boolean online)
	{
		theGridView.setFriendOnline(friend, online);
	}
	
	// get a character in the room
	public synchronized AStarCharacter getCharacterInRoom(String username)
	{
		return theGridView.getCharacterInRoom(username);
	}
	
	// set the player's inventory
	public void setInventory(ArrayList<InventoryItem> inventory)
	{
		theGridView.setInventory(inventory);
	}
	
	// send a message to the server (only used for messages from the grid and game rooms)
	protected void sendMessageToServer(MessageSecure m)
	{
		new MessageSenderThread(m).start();
	}
	
	// show/hide the toolbar elements based upon the value of the "visible" boolean
	public void showToolbar(boolean visible)
	{
		toolbar_left.setVisible(visible);
		toolbar_right.setVisible(visible);
		toolbar.setVisible(visible);
		chatTextBox.setVisible(visible);
	}
	
	// show/hide the loading window
	public void showLoadingWindow(boolean visible, boolean backgroundVisible)
	{
		loadingWindow.setVisible(visible);
		loadingBackground.setVisible(backgroundVisible);
	}
	
	// show/hide the loading window and change its text
	public void showLoadingWindow(String roomTitle, String description, boolean visible, boolean backgroundVisible)
	{
		loadingWindow.setRoomTitle(roomTitle);
		loadingWindow.setDescription(description);
		loadingWindow.setVisible(visible);
		loadingBackground.setVisible(backgroundVisible);
	}
	
	public boolean isLoadingVisible()
	{
		return loadingWindow.isVisible();
	}
	
	// set the current room name
	public void setRoomInformation(String newRoomID, String newRoomName)
	{
		roomID = newRoomID;
		roomName = newRoomName;
		theGridView.setRoomInformation(newRoomID, newRoomName);
	}
	
	// update an item in the room
	public void updateRoomItem(RoomItem item)
	{
		theGridView.updateRoomItem(item);
	}
	
	// set the ID of the newly-created Guest Room
	public void setNewlyCreatedRoomID(String roomID)
	{
		theGridView.shopWindow.addRoomInfo("ID", roomID);
	}
	
	public void updateCharacterClothing(AStarCharacter character)
	{
		theGridView.updateCharacterClothing(character);
	}
	
	// show an internal game playing area
	public void showGameArea(String gameArea)
	{
		// hide the regular grid view
		theGridView.setVisible(false);
		
		// disable the chat box so the internal game can accept keyboard commands
		chatTextBox.setEnabled(false);
		
		// disable the toolbars
		toolbar_left.setEnabled(false);
		toolbar_right.setEnabled(false);
		
		// send the "Game Add User" message to add the user to the game room and remove him from the original room
		sendMessageToServer(new MessageGameAddUserToRoom(gameArea, theGridView.getMyCharacter()));
		
		// check to see which game needs to be started
		if(gameArea.toLowerCase().equals("fireworks"))
		{	
			// show the Fireworks game
			gameFireworks.start();
			gameFireworks.setVisible(true);
		}
		else if(gameArea.toLowerCase().equals("pirates"))
		{
			// show the Pirates game
			gamePirates.start();
			gamePirates.setVisible(true);
		}
	}
	
	// hide an internal game playing area
	public void hideGameArea(String gameArea)
	{
		if(gameArea.toLowerCase().equals("fireworks"))
		{
			// give the player his credits
			theGridView.getMyCharacter().addCredits(gameFireworks.getCreditsWon());
			
			// send a message to the user informing him of his accomplishment
			sendMessageToServer(new MessageSendMailToUser("HVMK Staff",basicAvatarData.getUsername(),"Nice job with your fireworks display in Castle Fireworks!<br /><br />In recognition of your accomplishment, you have been awarded " + gameFireworks.getCreditsWon() + " credits.",new Date()));
			
			// send the "Game Remove User" message to remove the user from the game room and add him back to the original room
			sendMessageToServer(new MessageGameRemoveUserFromRoom(basicAvatarData.getUsername(), theGridView.getMyCharacter(), gameFireworks.getRoomID(), theGridView.getRoomInfo().get("ID")));
			
			// hide the game area
			gameFireworks.setVisible(false);
			gameFireworks.stop();
		}
		else if(gameArea.toLowerCase().equals("pirates"))
		{
			// give the player his credits
			theGridView.getMyCharacter().addCredits(gamePirates.getCreditsWon());
			
			// send the "Game Remove User" message to remove the user from the game room and add him back to the original room
			sendMessageToServer(new MessageGameRemoveUserFromRoom(basicAvatarData.getUsername(), theGridView.getMyCharacter(), gamePirates.getRoomID(), theGridView.getRoomInfo().get("ID")));
			
			// hide the game area
			gamePirates.setVisible(false);
			gamePirates.stop();
		}
		
		// enable the toolbars again
		toolbar_left.setEnabled(true);
		toolbar_right.setEnabled(true);
		
		// enable the chat box again so the player can type
		chatTextBox.setEnabled(true);
		
		// change the room back to the original room he was in
		theGridView.changeRoom(theGridView.getRoomInfo().get("ID"));
		
		// show the regular grid view
		theGridView.setVisible(true);
		
		// hide the map
		theGridView.hideMap();
	}
	
	// send an add score message to the server (to be used by the internal games)
	public void sendAddGameScoreMessage(GameScore g)
	{
		sendMessageToServer(new MessageGameScore(g));
	}
	
	// set the generated game room ID from the server for the game
	public void setGameRoomID(String gameArea, String gameRoomID)
	{
		if(gameArea.toLowerCase().equals("fireworks"))
		{
			gameFireworks.setRoomID(gameRoomID);
		}
		else if(gameArea.toLowerCase().equals("pirates"))
		{
			gamePirates.setRoomID(gameRoomID);
		}
	}
	
	// move a character in a game room
	public void gameMoveCharacter(String username, String gameID, int destGridX, int destGridY)
	{
		// make sure we're not moving the current user's character again
		if(!basicAvatarData.getUsername().equals(username))
		{
			// check the game ID
			if(gameID.toLowerCase().equals("pirates"))
			{
				// Pirates of the Caribbean
				gamePirates.moveShip(username, destGridX, destGridY);
			}
		}
	}
	
	// send a Game Move Character message to the server
	public void sendGameMoveCharacterMessage(String username, String gameID, String gameRoomID, int destGridX, int destGridY)
	{
		sendMessageToServer(new MessageGameMoveCharacter(username, gameID, gameRoomID, destGridX, destGridY));
	}
	
	// add a score to the desired game from the server
	public void addGameScore(String gameArea, GameScore score)
	{
		if(gameArea.toLowerCase().equals("fireworks"))
		{
			gameFireworks.addGameScore(score);
		}
		else if(gameArea.toLowerCase().equals("pirates"))
		{
			//gamePirates.addGameScore(score);
		}
	}
	
	// fire the cannons for a ship in Pirates of the Caribbean
	public void gamePiratesFireCannons(String username, String direction)
	{
		gamePirates.fireCannons(username, direction);
	}
	
	// send a Fire Cannons message for a ship in POTC
	public void sendGamePiratesFireCannonsMessage(String username, String direction, String gameRoomID)
	{
		sendMessageToServer(new MessageGamePiratesFireCannons(username, direction, gameRoomID));
	}
	
	public void showChatBox()
	{
		if(!chatTextBox.isVisible())
		{
			chatTextBox.setVisible(true);
		}
	}
	
	// manipulate the loading window to display a message about the server being down
	public void displayLoadingWindowServerDown()
	{
		loadingWindow.hideLoadingBar();
		loadingWindow.setDescription("The HVMK server is down.  Please try again later.");
	}
	
	// set the description on the loading window
	public void setLoadingDescription(String description)
	{
		loadingWindow.setDescription(description);
	}
	
	// return the character for the current user
	// ONLY USED BY VMKClientThread WHEN SENDING AN UPDATE TO THE SERVER AFTER A RE-CONNECT
	public AStarCharacter getMyCharacter()
	{
		return theGridView.getMyCharacter();
	}
	
	public boolean isWindowClosing()
	{
		return windowClosing;
	}
	
	// thread to handle sending messages
	class MessageSenderThread extends Thread
	{
		private MessageSecure m = null;
		
		public MessageSenderThread(MessageSecure m) {this.m = m;}
		
		public void run()
		{
			// send the message to the server
			theVMKClient.sendMessageToServer(m);
		}
	}
	
	// internal class to load the actual RoomViewerUI after login
	class RoomViewerLoadingThread extends Thread
	{
		private RoomViewerUI uiObject = null;
		
		public RoomViewerLoadingThread(RoomViewerUI uiObject) {this.uiObject = uiObject;}
		
		public void run()
		{
			if(release == false) // in-house development UI
			{
				uiObject.setPreferredSize(new Dimension(1000, 628));
			}
			else // public release UI
			{
				uiObject.setPreferredSize(new Dimension(800, 628));
			}

			uiObject.setSize(800,600);

			uiObject.setLayout(null);

			// set up the fonts
			setupFonts();

			if(release == false) // in-house development UI
			{
				JLabel titleLabel = new JLabel("<html><center>Room Viewer v.1<br>by Hawkster</center></html>");
				titleLabel.setBounds(new Rectangle(860, 10, 100, 40));
				add(titleLabel);

				// Show Grid button
				final JCheckBox showGridButton = new JCheckBox("Show Grid");
				showGridButton.setSelected(false);
				showGridButton.setBounds(new Rectangle(850, 150, 100, 32));
				showGridButton.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						// toggle the grid
						theGridView.showGrid(showGridButton.isSelected());
					}
				});
				add(showGridButton);

				// Show Exit Tiles button
				final JCheckBox showExitTilesButton = new JCheckBox("Show Exit Tiles");
				showExitTilesButton.setSelected(true);
				showExitTilesButton.setBounds(new Rectangle(850, 200, 132, 32));
				showExitTilesButton.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						// show/hide the exit tiles
						theGridView.showExitTiles(showExitTilesButton.isSelected());
					}
				});
				add(showExitTilesButton);

				// Show Nogo Tiles button
				final JCheckBox showNogoTilesButton = new JCheckBox("Show Nogo Tiles");
				showNogoTilesButton.setSelected(true);
				showNogoTilesButton.setBounds(new Rectangle(850, 226, 132, 32));
				showNogoTilesButton.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						// show/hide the nogo tiles
						theGridView.showNogoTiles(showNogoTilesButton.isSelected());
					}
				});
				add(showNogoTilesButton);

				// Show Walk Tiles button
				final JCheckBox showWalkTilesButton = new JCheckBox("Show Walk Tiles");
				showWalkTilesButton.setSelected(true);
				showWalkTilesButton.setBounds(new Rectangle(850, 252, 132, 32));
				showWalkTilesButton.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						// show/hide the walk tiles
						theGridView.showWalkTiles(showWalkTilesButton.isSelected());
					}
				});
				add(showWalkTilesButton);

				// "Load Room" button
				final JButton loadRoomButton = new JButton("Load Room");
				loadRoomButton.setBounds(new Rectangle(835, 348, 132, 32));
				loadRoomButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						// open the file
						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter(
								"Room Files (*.room)", "room");
						chooser.setFileFilter(filter);
						//chooser.setCurrentDirectory(new File(currentDirectory));
						int returnVal = chooser.showOpenDialog(roomViewerUI);
						if(returnVal == JFileChooser.APPROVE_OPTION)
						{
							filename = chooser.getSelectedFile().getPath();

							System.out.println("You chose to load this file: " + filename);

							// stop all the threads from the current room
							theGridView.stopAll();

							// load the room
							FileOperations.loadFile(AppletResourceLoader.getFileFromJar(filename), theGridView);
						}
					}
				});
				add(loadRoomButton);

				// "Add Chat" button
				final JButton addChatButton = new JButton("Add Chat");
				addChatButton.setBounds(new Rectangle(835, 396, 132, 32));
				addChatButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						// add a chat bubble to the grid view
						theGridView.addTextBubble(basicAvatarData.getUsername(), "This is another chat bubble!", 100);
					}
				});
				add(addChatButton);
			}

			// set up the "Loading" window
			loadingWindow = new WindowLoading(textFont, textFontBold, 250, 150);
			loadingWindow.setRoomTitle("Hawk's Virtual Magic Kingdom");
			loadingWindow.setDescription("Loading... please wait");
			loadingWindow.setVisible(true);
			add(loadingWindow);

			// set up the "Loading" window background
			loadingBackground = new JLabel(AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "loading_room_vmk.png"));
			loadingBackground.setBounds(0,0,800,600);
			loadingBackground.setVisible(true);
			add(loadingBackground);
			
			// repaint the window
			repaint();

			// Toolbar (left)
			toolbar_left = new JLabel("");
			toolbar_left.setBounds(new Rectangle(0, 572, 228, 28));
			toolbar_left.addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent e)
				{	
					// make sure we only process these events when the mouse
					// is within the editing grid
					int mouseX = e.getX();
					int mouseY = e.getY();
					Point mousePoint = new Point(mouseX, mouseY);

					if(infoButtonRect.contains(mousePoint)) // click inside the "I" button
					{
						System.out.println("Clicked toolbar info button");

						// check to see if the room owner information exists
						if(theGridView.getRoomInfo().get("OWNER") != null)
						{
							if(theGridView.getRoomInfo().get("OWNER").equals(theGridView.getMyCharacter().getUsername()))
							{
								theGridView.toggleEditRoomDescriptionWindow(); // show/hide the Edit Room Description window
							}
							else
							{
								theGridView.toggleRoomDescriptionWindow(); // show/hide the description
							}
						}
						else
						{
							theGridView.toggleRoomDescriptionWindow(); // show/hide the description
						}
					}
					else if(globeButtonRect.contains(mousePoint)) // click inside the globe button
					{
						System.out.println("Clicked toolbar globe button");
						theGridView.showMap(); // show the map
					}
					else if(inventoryButtonRect.contains(mousePoint)) // click inside the inventory button
					{
						System.out.println("Clicked toolbar inventory button");
						theGridView.toggleInventoryWindow(); // show/hide the inventory window
					}
					else if(messagesButtonRect.contains(mousePoint)) // click inside the messages button
					{
						System.out.println("Clicked toolbar messages button");

						// hide the "New Mail" animation
						messagesAnimationLabel.setVisible(false);

						theGridView.toggleMessagesWindow(); // show/hide the messages window
					}
					else if(shopButtonRect.contains(mousePoint)) // click inside the shop button
					{
						System.out.println("Clicked toolbar shop button");
						theGridView.toggleShopWindow(); // show/hide the shop window
					}
					else if(questButtonRect.contains(mousePoint)) // click inside the quest button
					{
						System.out.println("Clicked toolbar quest button");
						gamePirates.endGame();
					}
					else if(emoticonsButtonRect.contains(mousePoint)) // click inside the emoticons button
					{
						System.out.println("Clicked toolbar emoticons button");
					}
					else
					{
						System.out.println("Clicked Toolbar (left): " + mouseX + "-" + mouseY);
					}
				}
			});
			add(toolbar_left);

			// Toolbar (right)
			toolbar_right = new JLabel("");
			toolbar_right.setBounds(new Rectangle(603, 572, 199, 28));
			toolbar_right.addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent e)
				{	
					// make sure we only process these events when the mouse
					// is within the editing grid
					int mouseX = e.getX();
					int mouseY = e.getY();
					Point mousePoint = new Point(mouseX, mouseY);

					if(magicButtonRect.contains(mousePoint)) // click inside the magic pins button
					{
						System.out.println("Clicked toolbar magic pins button");
					}
					else if(cameraButtonRect.contains(mousePoint)) // click inside the camera button
					{
						System.out.println("Clicked toolbar camera button");
					}
					else if(clothingButtonRect.contains(mousePoint)) // click inside the clothing button
					{
						System.out.println("Clicked toolbar clothing button");
						theGridView.toggleClothingWindow(); // show/hide the clothing window
					}
					else if(soundButtonRect.contains(mousePoint)) // click inside the sound button
					{
						System.out.println("Clicked toolbar sound button");
						theGridView.toggleSettingsWindow(); // show/hide the settings window
					}
					else if(helpButtonRect.contains(mousePoint)) // click inside the help button
					{
						System.out.println("Clicked toolbar help button");
						theGridView.toggleHelpWindow(); // show/hide the help window
					}
					else if(exitButtonRect.contains(mousePoint)) // click inside the exit button
					{
						// logout
						sendMessageToServer(new MessageLogout());
						theVMKClient.stopClient();
						theVMKClient = null;

						// load the login UI
						loadLoginUI();
					}
					else
					{
						System.out.println("Clicked Toolbar (right): " + mouseX + "-" + mouseY);
					}
				}
			});
			add(toolbar_right);

			// add the "New Mail" animation
			messagesAnimationLabel = new JLabel(AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "mail_anim.gif"));
			messagesAnimationLabel.setBounds(100, 574, 32, 23);
			messagesAnimationLabel.setVisible(false);
			add(messagesAnimationLabel);

			// Toolbar image
			toolbar = new JLabel(AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "toolbar.png"));
			toolbar.setBounds(new Rectangle(0, 572, 800, 28));
			add(toolbar);

			// Text box for chat input
			chatTextBox = new JTextField();
			chatTextBox.setVisible(false);
			chatTextBox.setBorder(null);
			chatTextBox.setCaretColor(Color.WHITE);
			chatTextBox.setBackground(new Color(23, 34, 49));
			chatTextBox.setForeground(Color.WHITE);
			chatTextBox.setFont(textFont);
			chatTextBox.setBounds(new Rectangle(235, 576, 360, 17));
			chatTextBox.addKeyListener(new KeyListener()
			{
				public void keyPressed(KeyEvent e) {}
				public void keyTyped(KeyEvent e)
				{
					// only allow a certain number of characters
					if(chatTextBox.getText().length() > maximumChatCharacters)
					{
						e.consume();
					}
				}
				public void keyReleased(KeyEvent e)
				{ 
					if(e.getKeyCode() == KeyEvent.VK_ENTER) // check for an ENTER key
					{
						// send the input to the chat bubbles object in the grid
						theGridView.addTextBubble(basicAvatarData.getUsername(), chatTextBox.getText(), 100);

						// send an "Add Chat" message to the server
						theVMKClient.sendMessageToServer(new MessageAddChatToRoom(basicAvatarData.getUsername(), roomID, chatTextBox.getText()));

						// clear the text box
						chatTextBox.setText("");
					}
				}
			});
			add(chatTextBox);
			
			loadingWindow.setDescription("Creating inventory mappings...");
			
			// create the pin mappings
			StaticAppletData.createInvMappings();

			loadingWindow.setDescription("Creating room mappings...");
			
			// create the room mappings
			StaticAppletData.createRoomMappings();

			loadingWindow.setDescription("Creating chat dictionaries...");
			
			// create the dictionaries
			Dictionary.createDictionaries();

			loadingWindow.setDescription("Creating viewer grid...");
			
			// create the grid
			theGridView = new RoomViewerGrid();
			theGridView.setVisible(false);
			theGridView.setBounds(new Rectangle(0,0,800,572));

			loadingWindow.setDescription("Assigning fonts to the grid...");
			
			// assign the fonts to the grid
			theGridView.setTextFont(textFont);
			theGridView.setTextFontBold(textFontBold);

			// hide the grid tiles
			theGridView.showGrid(false);

			// add the grid
			add(theGridView);

			// pack the window and display it
			uiObject.setName("Hawk's Virtual Magic Kingdom");
			uiObject.setVisible(true);

			loadingWindow.setDescription("Setting up the viewer grid...");
			
			// set-up the double-buffering objects and start the grid graphics loop
			theGridView.setOffscreenImage(createImage(800, 572));
			theGridView.setUIObject(uiObject);
			theGridView.start();
			theGridView.loadGridView();

			// auto-load the Walk Test room
			// stop all the threads from the current room
			theGridView.stopAll();

			// load the room
			//FileOperations.loadFile(AppletResourceLoader.getFileFromJar(filename), theGridView);

			// create a reference to the Fireworks game
			gameFireworks = new GameFireworks();
			gameFireworks.setBounds(new Rectangle(0,0,800,572));
			gameFireworks.setVisible(false);
			gameFireworks.setUIObject(uiObject);
			add(gameFireworks);

			// create a reference to the Pirates game
			gamePirates = new GamePirates();
			gamePirates.setBounds(new Rectangle(0,0,800,572));
			gamePirates.setVisible(false);
			gamePirates.setUIObject(uiObject);
			add(gamePirates);

			repaint();
			roomViewerUI = uiObject;

			loadingWindow.setDescription("Starting authentication client...");
			
			// connect to the server and start the client connection
			theVMKClient = new VMKClient();
			theVMKClient.setUIObject(roomViewerUI);
			theVMKClient.startClient();
		}
	}
}
