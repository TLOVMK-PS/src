// FileOperations.java by Matt Fritz
// November 7, 2009
// Handles the saving and loading of the room files

// TODO: Include functionality for reading in STATIONARY room objects

package util;

import interfaces.GridViewable;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

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
	private static PrintWriter usernameEmailMappingWriter = null; // writer for username:email mappings
	
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
	
	// load a character given an email address
	public static synchronized AStarCharacter loadCharacter(String email)
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
		
		Scanner fileReader;
		
		String username = "";
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
		
		try
		{
			fileWriter = new PrintWriter(filename);
			
			// write out the friends list
			for(int i = 0; i < friendsList.getFriends().size(); i++)
			{
				fileWriter.println("FRIEND: " + friendsList.getFriends().get(i));
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
					
					if(!line.equals("")) // username:email
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
	
	// append a new username:email mapping to the mappings file
	public static void addUsernameEmailMapping(String username, String email)
	{
		String filename = "data/mappings/usernameToEmail.dat";
		
		try
		{
			if(usernameEmailMappingWriter == null)
			{
				// load the mapping writer if we haven't already done so
				usernameEmailMappingWriter = new PrintWriter(filename);
				
				// write out the entire HashMap first with an iterator before we can start appending
				Iterator<Entry<String,String>> i = VMKServerPlayerData.getUsernameEmailMappings().entrySet().iterator();
				while(i.hasNext())
				{
					Entry<String,String> entry = (Entry<String,String>)i.next();
					usernameEmailMappingWriter.println(entry.getKey() + ":" + entry.getValue());
				}
			}

			// append out the username and email data if it doesn't already exist
			if(!VMKServerPlayerData.containsUsernameEmailMapping(username))
			{
				usernameEmailMappingWriter.println(username + ":" + email);
				usernameEmailMappingWriter.flush();
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveUsernameEmailMappings()");
			e.printStackTrace();
		}
	}
}
