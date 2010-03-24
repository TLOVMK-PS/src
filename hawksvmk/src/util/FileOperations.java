// FileOperations.java by Matt Fritz
// November 7, 2009
// Handles the saving and loading of the room files

// TODO: Include functionality for reading in STATIONARY room objects

package util;

import interfaces.GridViewable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import animations.Animation;
import animations.AnimationFrame;
import astar.AStarCharacter;

import sockets.VMKServerPlayerData;
import sounds.RepeatingSound;
import sounds.SingleSound;
import sounds.SoundPlayable;
import tiles.Tile;

public class FileOperations
{
	private static String commentDelimeter = "//"; // pattern to search for comment lines in files
	private static String newPlayerMessage = "Welcome to Hawk's Virtual Magic Kingdom! If you played the original Virtual Magic Kingdom, you will already be familiar with the game.  If not, please feel free to ask around!  We hope you enjoy the game.";
	
	// save a file given a filename and a map of tiles
	public static void saveFile(String filename, String backgroundImagePath, HashMap<String,Tile> tiles)
	{
		PrintWriter fileWriter;
		try
		{
			fileWriter = new PrintWriter(new File(filename));
			
			// write out the background image location
			fileWriter.println("IMAGE: " + backgroundImagePath);
			
			for(Tile t : tiles.values())
			{
				// write out a tile to the file
				fileWriter.println(t.toString());
			}
			
			fileWriter.close();
		}
		catch(Exception e)
		{
		}
	}
	
	// load a file given a filename
	public static void loadFile(InputStream filename, GridViewable gridView)
	{
		Scanner fileReader;
		HashMap<String,Tile> tiles = new HashMap<String,Tile>();
		String backgroundImagePath = "";
		ArrayList<Animation> animations = new ArrayList<Animation>();
		ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>();
		
		Scanner tileScanner;
		Scanner soundScanner;
		
		try
		{
			fileReader = new Scanner(filename);
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("IMAGE: "))
				{
					line = line.replaceAll("IMAGE: ", "");
					
					// set the background image path
					backgroundImagePath = line;
				}
				else if(line.startsWith("SOUND: "))
				{
					line = line.replaceAll("SOUND: ", "");
					
					soundScanner = new Scanner(line);
					
					String soundFilename = soundScanner.next();
					String soundName = soundScanner.next();
					
					// add the single sound to the ArrayList
					sounds.add(new SingleSound(soundName, AppletResourceLoader.getSoundFromJar(soundFilename)));
					
					soundScanner.close();
				}
				else if(line.startsWith("REPEATING SOUND: "))
				{
					line = line.replaceAll("REPEATING SOUND: ", "");
					
					// remove the commas and turn them into spaces
					line = line.replaceAll(",", " ");
					
					soundScanner = new Scanner(line);
					
					String soundFilename = soundScanner.next();
					String soundName = soundScanner.next();
					int soundDelay = Integer.parseInt(soundScanner.next());
					
					// add the repeating sound to the ArrayList
					sounds.add(new RepeatingSound(soundName, soundDelay, AppletResourceLoader.getSoundFromJar(soundFilename)));
					
					soundScanner.close();
				}
				else if(line.startsWith("ANIMATION: "))
				{
					line = line.replaceAll("ANIMATION: ", "");
					
					// make sure an animation exists
					if(!line.equals("none"))
					{
						System.out.println("Loading animation: " + line + "...");
						// load an animation and add it to the ArrayList
						animations.add(loadAnimation(line));
					}
				}
				else if(line.startsWith(commentDelimeter))
				{
					// comment line, so ignore
				}
				else
				{
					// row,col,TILE_TYPE
					// remove the commas and turn them into spaces
					line = line.replaceAll(",", " ");
					
					// remove the brackets
					line = line.replaceAll("@", "");
					
					tileScanner = new Scanner(line);
					
					int row = Integer.parseInt(tileScanner.next());
					int col = Integer.parseInt(tileScanner.next());
					String tileType = tileScanner.next();
					
					// add the tile to the HashMap
					Tile newTile = new Tile(row,col,tileType);
					tiles.put(row + "-" + col, newTile);
					
					tileScanner.close();
				}
			}
			
			// set the background image
			gridView.setBackgroundImage(backgroundImagePath);
			
			// set the tiles
			gridView.setTilesMap(tiles);
			
			// set the animations
			gridView.setAnimations(animations);
			
			// set the sounds
			gridView.setSounds(sounds);
			
			// set up the chat bubbles
			gridView.setupChatBubbles();
			
			fileReader.close();
			
			System.out.println("File loaded");
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadFile(): " + e.getClass().getName() + " - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// load and return an animation from a file
	public static Animation loadAnimation(String filename)
	{
		Scanner fileReader;
		
		String animationName = "";
		int totalFrames = 0;
		int x_coord = 0;
		int y_coord = 0;
		Animation animation = new Animation();
		
		Scanner animationFrameScanner;
		int currentFrame = 0;
		
		try
		{
			fileReader = new Scanner(AppletResourceLoader.getFileFromJar(filename));
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("ANIMATION NAME: "))
				{
					line = line.replaceAll("ANIMATION NAME: ", "");
					
					// set the animation name
					animationName = line;
					animation.setName(animationName);
				}
				else if(line.startsWith("TOTAL FRAMES: "))
				{
					line = line.replaceAll("TOTAL FRAMES: ", "");
					
					// set the total number of frames
					totalFrames = Integer.parseInt(line);
					animation.setTotalFrames(totalFrames);
				}
				else if(line.startsWith("X-COORD: "))
				{
					line = line.replaceAll("X-COORD: ", "");
					
					// set the x-coordinate
					x_coord = Integer.parseInt(line);
					animation.setX(x_coord);
				}
				else if(line.startsWith("Y-COORD: "))
				{
					line = line.replaceAll("Y-COORD: ", "");
					
					// set the y-coordinate
					y_coord = Integer.parseInt(line);
					animation.setY(y_coord);
				}
				else if(line.startsWith(commentDelimeter))
				{
					// comment line, so ignore
				}
				else
				{
					// image path,delay
					// remove the commas and turn them into spaces
					line = line.replaceAll(",", " ");
					
					// remove the brackets
					line = line.replaceAll("@", "");
					
					animationFrameScanner = new Scanner(line);
					
					String imagePath = animationFrameScanner.next();
					int delay = Integer.parseInt(animationFrameScanner.next());
					
					// create a new animation frame
					AnimationFrame newFrame = new AnimationFrame(currentFrame, AppletResourceLoader.getImageFromJar(imagePath), delay);
					
					// add the new frame to the animation
					animation.addFrame(currentFrame, newFrame);
					
					// increment the frame counter
					currentFrame++;
					
					animationFrameScanner.close();
				}
			}
			
			fileReader.close();
			
			System.out.println("Animation \"" + animationName + "\" loaded from file");
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadAnimation(): " + e.getClass().getName() + " - " + e.getMessage());
		}
		
		// return the animation
		return animation;
	}
	
	// create a new player with blank data files
	private static synchronized void createNewPlayerFiles(String username, String email)
	{
		PrintWriter fileWriter = null;
		String filename = "";
		
		try
		{
			// character file
			filename = "data/characters/" + email + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("USERNAME: " + username);
			fileWriter.println("SIGNATURE: " + username); // give the player a default signature with only his username
			fileWriter.println("BADGE: badge_2"); // give the user a "Here From Day One" badge
			
			// print out empty badge entries
			for(int i = 0; i < StaticAppletData.MAX_DISPLAYABLE_BADGES - 1; i++)
			{
				fileWriter.println("BADGE: ");
			}
			
			// print out empty pin entries
			for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_PINS; j++)
			{
				fileWriter.println("PIN: ");
			}
			fileWriter.close();
			
			// friends file
			filename = "data/friends/" + email + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println(); // blank friends file
			fileWriter.close();
			
			// messages file (give them one new message from VMK Staff)
			filename = "data/messages/" + email + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("SENDER: VMK Staff");
			fileWriter.println("DATE: " + new Date().toString());
			fileWriter.println("BODY: " + newPlayerMessage);
			fileWriter.println();
			fileWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN createNewPlayerFiles()");
			e.printStackTrace();
		}
	}
	
	// load a character given an email address
	public static synchronized AStarCharacter loadCharacter(String username, String email)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			filename = "data/characters/" + email + ".dat"; // filename of the character file
		}
		else
		{
			filename = "data/characters/default.dat"; // load default character file
		}
		
		// make sure the files exist; if not, create the necessary files
		if(!(new File(filename).exists()))
		{
			// create a new default player with the given email address
			createNewPlayerFiles(username, email);
		}
		
		Scanner fileReader;
		
		String signature = "";
		
		PinInfo displayedBadges[] = new PinInfo[StaticAppletData.MAX_DISPLAYABLE_BADGES];
		PinInfo displayedPins[] = new PinInfo[StaticAppletData.MAX_DISPLAYABLE_PINS];
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(filename);
			int pinNum = 0;
			int badgeNum = 0;
			
			if(is != null) // file exists
			{
				fileReader = new Scanner(is);
				while(fileReader.hasNextLine())
				{
					String line = fileReader.nextLine();
					
					if(line.startsWith("USERNAME: ")) // username
					{
						line = line.replaceAll("USERNAME: ", "");
						username = line;
					}
					else if(line.startsWith("SIGNATURE: ")) // signature
					{
						line = line.replaceAll("SIGNATURE: ", "");
						signature = line;
					}
					else if(line.startsWith("BADGE: ")) // badge
					{
						line = line.replaceAll("BADGE: ", "");
						displayedBadges[badgeNum] = StaticAppletData.getPinInfo(line);
						badgeNum++;
					}
					else if(line.startsWith("PIN: ")) // pin
					{
						line = line.replaceAll("PIN: ", "");
						displayedPins[pinNum] = StaticAppletData.getPinInfo(line);
						pinNum++;
					}
					else if(line.startsWith(commentDelimeter))
					{
						// comment line, so ignore
					}
				}
				
				fileReader.close();
			}
			else
			{
				// file doesn't exist
				// create a new character and don't worry about it
				AStarCharacter newCharacter = new AStarCharacter(username, 15, 7);
				newCharacter.setEmail(email);
				newCharacter.setSignature(signature);
				
				// create blank badges
				for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_BADGES; j++)
				{
					displayedBadges[j] = StaticAppletData.getPinInfo("");
				}
				
				// create blank pins
				for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_PINS; j++)
				{
					displayedPins[j] = StaticAppletData.getPinInfo("");
				}
				
				newCharacter.setDisplayedBadges(displayedBadges);
				newCharacter.setDisplayedPins(displayedPins);
				return newCharacter;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadCharacter(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new character from the file data
		AStarCharacter newCharacter = new AStarCharacter(username, 15, 7);
		newCharacter.setEmail(email);
		newCharacter.setSignature(signature);
		newCharacter.setDisplayedBadges(displayedBadges);
		newCharacter.setDisplayedPins(displayedPins);
		return newCharacter;
	}
	
	// save a character
	public static synchronized void saveCharacter(AStarCharacter character)
	{
		PrintWriter fileWriter;
		String filename = "";
		
		if(!character.getEmail().equals(""))
		{
			// save the character file
			filename = "data/characters/" + character.getEmail() + ".dat";
		}
		else
		{
			// save the default character file
			filename = "data/characters/default.dat";
		}
		
		try
		{
			fileWriter = new PrintWriter(filename);
			
			// write out the username
			fileWriter.println("USERNAME: " + character.getUsername());
			
			// write out the signature
			fileWriter.println("SIGNATURE: " + character.getSignature());
			
			// write out the badges
			PinInfo[] displayedBadges = character.getDisplayedBadges();
			for(int i = 0; i < displayedBadges.length; i++)
			{
				fileWriter.println("BADGE: " + displayedBadges[i].getID());
			}
			
			// write out the pins
			PinInfo[] displayedPins = character.getDisplayedPins();
			for(int i = 0; i < displayedPins.length; i++)
			{
				fileWriter.println("PIN: " + displayedPins[i].getID());
			}
			
			fileWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveCharacter()");
			e.printStackTrace();
		}
	}
	
	// load a friends list given a player's email address
	public static synchronized FriendsList loadFriendsList(String email)
	{
		String filename = "";
		FriendsList friendsList = new FriendsList();
		
		if(!email.equals(""))
		{
			filename = "data/friends/" + email + ".dat"; // filename of the character file
		}
		else
		{
			filename = "data/friends/default.dat"; // load default character file
		}
		
		Scanner fileReader;
		
		String friend = "";
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(filename);

			if(is != null) // file exists
			{
				fileReader = new Scanner(is);
				while(fileReader.hasNextLine())
				{
					String line = fileReader.nextLine();
					
					if(line.startsWith("FRIEND: ")) // username
					{
						line = line.replaceAll("FRIEND: ", "");
						friend = line;
						
						// add the friend to the list
						friendsList.add(friend);
					}
					else if(line.startsWith(commentDelimeter))
					{
						// comment line, so ignore
					}
				}
				
				fileReader.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty friends list
				return friendsList;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadFriendsList(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new friends list from the file data
		return friendsList;
	}
	
	// save a friends list given an email address
	public static synchronized void saveFriendsList(String email, FriendsList theFriendsList)
	{
		PrintWriter fileWriter;
		String filename = "";
		
		FriendsList friendsList = theFriendsList;
		if(friendsList == null)
		{
			// make sure we have an actual friends list and that the user just isn't offline
			friendsList = loadFriendsList(email);
			
			System.out.println("Loading friends list from file for email: " + email + "...");
		}
		
		if(!email.equals(""))
		{
			// save the friends list file
			filename = "data/friends/" + email + ".dat";
		}
		else
		{
			// save the default friends list file
			filename = "data/friends/default.dat";
		}
		
		System.out.println("Saving friends list: " + filename);
		
		try
		{
			fileWriter = new PrintWriter(filename);
			
			// write out the friends list
			Iterator<String> friendsIterator = friendsList.getFriends().keySet().iterator();
			while(friendsIterator.hasNext())
			{
				fileWriter.println("FRIEND: " + friendsIterator.next());
			}
			
			fileWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveFriendsList()");
			e.printStackTrace();
		}
	}
	
	// load username:email mappings
	public static synchronized HashMap<String,String> loadUsernameEmailMappings()
	{
		String filename = "data/mappings/usernameToEmail.dat";
		HashMap<String,String> usernameEmailMappings = new HashMap<String,String>();
		
		Scanner fileReader;
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(filename);

			if(is != null) // file exists
			{
				fileReader = new Scanner(is);
				while(fileReader.hasNextLine())
				{
					String line = fileReader.nextLine();
					
					if(line.startsWith(commentDelimeter))
					{
						// comment line, so ignore
					}
					else if(!line.equals("")) // username:email
					{
						String dataArray[] = line.split(":");
						usernameEmailMappings.put(dataArray[0], dataArray[1]);
					}
				}
				
				fileReader.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				return usernameEmailMappings;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadUsernameEmailMappings(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new mappings list from the file data
		return usernameEmailMappings;
	}
	
	// load a user's mail messages
	public static ArrayList<MailMessage> loadMailMessages(String username, String email)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the mail messages file
			filename = "data/messages/" + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = "data/messages/default.dat";
		}
		
		ArrayList<MailMessage> messages = new ArrayList<MailMessage>();
		String sender = "";
		String dateSent = "";
		String bodyText = "";
		Scanner fileReader;
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(filename);

			if(is != null) // file exists
			{
				fileReader = new Scanner(is);
				while(fileReader.hasNextLine())
				{
					String line = fileReader.nextLine();
					
					if(!line.equals(""))
					{
						if(line.startsWith("SENDER: "))
						{
							// get the sender
							line = line.replaceAll("SENDER: ", "");
							sender = line;
						}
						else if(line.startsWith("DATE: "))
						{
							// get the date sent
							line = line.replaceAll("DATE: ", "");
							dateSent = line;
						}
						else if(line.startsWith("BODY: "))
						{
							// get the body text
							line = line.replaceAll("BODY: ", "");
							bodyText = line;
							
							// add a new mail message to the array list
							messages.add(new MailMessage(sender, username, bodyText, dateSent));
						}
						else if(line.startsWith(commentDelimeter))
						{
							// comment line, so ignore
						}
					}
				}
				
				fileReader.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty messages list
				return messages;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadMailMessages(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new messages list from the file data
		return messages;
	}
	
	// append a new mail message to a user's messages file
	public static void addMailMessage(String email, String sender, String body, String dateSent)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the mail messages file
			filename = "data/messages/" + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = "data/messages/default.dat";
		}
		
		try
		{	
			// open the file for appending
			FileOutputStream appendedFile = new FileOutputStream(filename, true);
			PrintWriter writer = new PrintWriter(appendedFile);
			
			// write the message to the file
			writer.println("SENDER: " + sender);
			writer.println("DATE: " + dateSent);
			writer.println("BODY: " + body);
			writer.println();
			writer.flush();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN addMailMessage()");
			e.printStackTrace();
		}
	}
	
	// save the user's mail messages to a file
	public static void saveMailMessages(String email, ArrayList<MailMessage> messages)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the mail messages file
			filename = "data/messages/" + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = "data/messages/default.dat";
		}
		
		try
		{	
			// open the file for writing
			PrintWriter writer = new PrintWriter(filename);
			
			// write the messages to the file
			for(int i = 0; i < messages.size(); i++)
			{
				MailMessage m = messages.get(i);
				writer.println("SENDER: " + m.getSender());
				writer.println("DATE: " + m.getDateSent());
				writer.println("BODY: " + m.getMessage());
				writer.println();
			}
			
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveMailMessages()");
			e.printStackTrace();
		}
	}
	
	// append a new username:email mapping to the mappings file
	public static void addUsernameEmailMapping(String username, String email)
	{
		String filename = "data/mappings/usernameToEmail.dat";
		
		try
		{	
			// open the file for appending
			FileOutputStream appendedFile = new FileOutputStream(filename, true);
			PrintWriter writer = new PrintWriter(appendedFile);
			
			// write the mapping to the file
			writer.println(username + ":" + email);
			writer.flush();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveUsernameEmailMappings()");
			e.printStackTrace();
		}
	}
	
	// load the pin and badge mappings
	public static HashMap<String,PinInfo> loadPinMappings()
	{
		String filename = "data/mappings/pinMappings.dat";
		HashMap<String,PinInfo> pinMappings = new HashMap<String,PinInfo>();
		
		Scanner fileReader;
		
		String pinID = "";
		String pinName = "";
		String pinPath = "";
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(filename);

			if(is != null) // file exists
			{
				fileReader = new Scanner(is);
				while(fileReader.hasNextLine())
				{
					String line = fileReader.nextLine();
					
					if(line.equals("") || line.startsWith(commentDelimeter))
					{
						// reached a blank line/comment line, so ignore
					}
					else if(line.startsWith("ID: "))
					{
						// get the pin ID
						line = line.replaceAll("ID: ", "");
						pinID = line;
					}
					else if(line.startsWith("NAME: "))
					{
						// get the pin name
						line = line.replaceAll("NAME: ", "");
						pinName = line;
					}
					else if(line.startsWith("PATH: "))
					{
						// get the pin path
						line = line.replaceAll("PATH: ", "");
						pinPath = line;
						
						// add the pin mapping to the HashMap
						pinMappings.put(pinID, new PinInfo(pinID, pinName, pinPath));
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				return pinMappings;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadPinMappings(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new mappings list from the file data
		return pinMappings;
	}
}
