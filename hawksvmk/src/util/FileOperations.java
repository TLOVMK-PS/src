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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import animations.Animation;
import animations.MovingAnimation;
import animations.StationaryAnimation;
import animations.AnimationFrame;
import astar.AStarCharacter;

import roomobject.RoomFurniture;
import roomobject.RoomItem;
import roomobject.RoomPoster;
import sockets.VMKServerPlayerData;
import sounds.RepeatingSound;
import sounds.SingleSound;
import sounds.SoundPlayable;
import tiles.Tile;

public class FileOperations
{
	private static String commentDelimeter = "//"; // pattern to search for comment lines in files
	private static String newPlayerMessage = "Welcome to Hawk's Virtual Magic Kingdom! If you played the original Virtual Magic Kingdom, you will already be familiar with the game.  If not, please feel free to ask around!  We hope you enjoy the game.<br><br>You've been given 1000 Credits, a Dancing Inferno Magic Pin, a Here From Day One Badge, and an HVMK Virtual Pin.";
	
	// save a file given a filename and a map of tiles
	public static void saveFile(String filename, String backgroundImagePath, HashMap<String,String> roomInfo, HashMap<String,Tile> tiles, ArrayList<Animation> animations, ArrayList<SoundPlayable> sounds, ArrayList<RoomItem> roomItems, String tileSize)
	{
		PrintWriter fileWriter;
		try
		{
			fileWriter = new PrintWriter(new File(filename));
			
			// print out the filename and creation date
			int fileIndex = filename.lastIndexOf("\\"); // get the position of the last directory separator
			if(fileIndex == -1)
			{
				// not a Windows machine, so check the other kind of directory separator
				fileIndex = filename.lastIndexOf("/");
			}
			fileWriter.println("// " + filename.substring(fileIndex + 1));
			fileWriter.println("// Created on " + new Date().toString());
			fileWriter.println();
			
			// write out the room information
			fileWriter.println("// Room information");
			fileWriter.println();
			fileWriter.println("ID: " + roomInfo.get("ID"));
			fileWriter.println("NAME: " + roomInfo.get("NAME"));
			fileWriter.println("OWNER: " + roomInfo.get("OWNER"));
			fileWriter.println("DESCRIPTION: " + roomInfo.get("DESCRIPTION"));
			fileWriter.println();
			
			// write out the background image location
			fileWriter.println("// Background image");
			fileWriter.println();
			fileWriter.println("IMAGE: " + backgroundImagePath);
			fileWriter.println();
			
			// write out the tile size
			fileWriter.println("// Size of the tiles (width by height)");
			fileWriter.println();
			fileWriter.println("TILES: " + tileSize);
			fileWriter.println();
			
			// write out the animations
			fileWriter.println("// Animations");
			fileWriter.println();
			for(int i = 0; i < animations.size(); i++)
			{
				fileWriter.println("ANIMATION: " + animations.get(i).getPath());
			}
			fileWriter.println();
			
			// write out the sounds
			fileWriter.println("// Sounds");
			fileWriter.println();
			for(int j = 0; j < sounds.size(); j++)
			{
				SoundPlayable sound = sounds.get(j);
				
				if(sound instanceof SingleSound)
				{
					// single sound
					fileWriter.println("SOUND: " + sound.getPath());
				}
				else if(sound instanceof RepeatingSound)
				{
					// repeating sound
					fileWriter.println("REPEATING SOUND: " + sound.getPath());
				}
			}
			fileWriter.println();
			
			fileWriter.println("// Tile map");
			fileWriter.println();
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
		ArrayList<RoomItem> items = new ArrayList<RoomItem>(); // room items like furniture and posters
		String backgroundImagePath = "";
		String[] tileDimensions = null;
		ArrayList<Animation> animations = new ArrayList<Animation>();
		ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>();
		
		Scanner tileScanner;
		Scanner soundScanner;
		
		String roomID = "";
		
		try
		{
			fileReader = new Scanner(filename);
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("ID: "))
				{
					line = line.replaceAll("ID: ", "");
					roomID = line;
					
					// set the room ID if it isn't a guest room
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("ID", line);
					}
				}
				else if(line.startsWith("NAME: "))
				{
					line = line.replaceAll("NAME: ", "");
					
					// set the room name
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("NAME", line);
					}
				}
				else if(line.startsWith("OWNER: "))
				{
					line = line.replaceAll("OWNER: ", "");
					
					// set the room owner
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("OWNER", line);
					}
				}
				else if(line.startsWith("DESCRIPTION: "))
				{
					line = line.replaceAll("DESCRIPTION: ", "");
					
					// set the room description
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("DESCRIPTION", line);
					}
				}
				else if(line.startsWith("IMAGE: "))
				{
					line = line.replaceAll("IMAGE: ", "");
					
					// set the background image path
					backgroundImagePath = line;
				}
				else if(line.startsWith("TILES: "))
				{
					// get the size of the tiles
					line = line.replaceAll("TILES: ", "");
					
					tileDimensions = line.split("x"); // split at the "x"
				}
				else if(line.startsWith("SOUND: "))
				{
					line = line.replaceAll("SOUND: ", "");
					
					soundScanner = new Scanner(line);
					
					String soundFilename = soundScanner.next();
					String soundName = soundScanner.next();
					
					// add the single sound to the ArrayList
					sounds.add(new SingleSound(soundName, soundFilename, AppletResourceLoader.getSoundFromJar(soundFilename)));
					
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
					sounds.add(new RepeatingSound(soundName, soundDelay, soundFilename, AppletResourceLoader.getSoundFromJar(soundFilename)));
					
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
				else if(line.startsWith(commentDelimeter) || line.equals(""))
				{
					// comment line or blank line, so ignore
				}
				else if(line.startsWith("@"))
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
					String tileDest = "";
					
					try
					{
						tileDest = tileScanner.next();
					}
					catch(NoSuchElementException e)
					{
						tileDest = "";
					}
					
					// add the tile to the HashMap
					Tile newTile = new Tile(row,col,tileType,tileDest);
					newTile.setWidth(Integer.parseInt(tileDimensions[0]));
					newTile.setHeight(Integer.parseInt(tileDimensions[1]));
					newTile.setAbsoluteCoordinates();
					tiles.put(row + "-" + col, newTile);
					
					tileScanner.close();
				}
			}
			
			// set the background image
			gridView.setBackgroundImage(backgroundImagePath);
			
			// set the tile size
			gridView.changeTileSize(Integer.parseInt(tileDimensions[0]), Integer.parseInt(tileDimensions[1]));
			
			// set the tiles
			gridView.setTilesMap(tiles);
			
			// set the room items
			gridView.setRoomItems(items);
			
			// set the animations
			gridView.setAnimations(animations);
			
			// set the sounds
			gridView.setSounds(sounds);
			
			// set up the chat bubbles
			gridView.setupChatBubbles();
			
			fileReader.close();
			filename.close();
			
			System.out.println("Room file loaded");
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadFile(): " + e.getClass().getName() + " - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// load a Guest Room from a file
	public static synchronized void loadGuestRoom(String roomPath, GridViewable gridView)
	{
		Scanner fileReader;
		ArrayList<RoomItem> items = new ArrayList<RoomItem>(); // room items like furniture and posters
		HashMap<String,Tile> tiles = new HashMap<String,Tile>();
		
		String filename = roomPath;
		
		try
		{	
			System.out.println("Guest room filename: " + filename);
			fileReader = new Scanner(AppletResourceLoader.getCharacterFromJar(filename));
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("TEMPLATE: "))
				{
					// load the template room file
					line = line.replaceAll("TEMPLATE: ", "");
					loadFile(AppletResourceLoader.getCharacterFromJar(line), gridView);
					
					// get the tiles back
					tiles = gridView.getTilesMap();
					
					// add the template to the room info
					gridView.addRoomInfo("TEMPLATE", line);
				}
				else if(line.startsWith("FURNITURE: "))
				{
					// room furniture
					line = line.replaceAll("FURNITURE: ", "");
					
					// id,row,col,rotation
					String furniture = line.replaceAll(",", " ");
					Scanner furnitureScanner = new Scanner(furniture);
					
					String id = furnitureScanner.next();
					int row = Integer.parseInt(furnitureScanner.next());
					int col = Integer.parseInt(furnitureScanner.next()) / 2;
					String rotation = furnitureScanner.next();
					
					furnitureScanner.close();
					
					// add a new piece of furniture
					Tile furniTile = tiles.get(row + "-" + col);
					InventoryInfo furniInfo = StaticAppletData.getInvInfo(id);
					
					RoomFurniture newItem = new RoomFurniture(furniTile.getX(), furniTile.getY(), furniInfo.getTiles(), id, furniInfo.getName(), furniInfo.getPath(), rotation);
					newItem.setRow(row);
					newItem.setCol(col);
					newItem.setContentRating(RatingSystem.getContentRating(furniInfo.getRatingIndex()));
					items.add(newItem);
				}
				else if(line.startsWith("POSTER: "))
				{
					// room poster
					line = line.replaceAll("POSTER: ", "");
					
					// id,row,col,rotation
					String poster = line.replaceAll(",", " ");
					Scanner posterScanner = new Scanner(poster);
					
					String id = posterScanner.next();
					int row = Integer.parseInt(posterScanner.next());
					int col = Integer.parseInt(posterScanner.next()) / 2;
					String rotation = posterScanner.next();
					
					posterScanner.close();
					
					// add a new poster
					Tile furniTile = tiles.get(row + "-" + col);
					InventoryInfo furniInfo = StaticAppletData.getInvInfo(id);
					
					RoomPoster newItem = new RoomPoster(furniTile.getX(), furniTile.getY(), furniInfo.getTiles(), id, furniInfo.getName(), furniInfo.getPath(), rotation);
					newItem.setRow(row);
					newItem.setCol(col);
					newItem.setContentRating(RatingSystem.getContentRating(furniInfo.getRatingIndex()));
					items.add(newItem);
				}
				else if(line.startsWith(commentDelimeter) || line.equals(""))
				{
					// comment line or blank line, so ignore
				}
			}
			
			// set the room information first
			HashMap<String,String> roomInfo = getInfoFromRoom(roomPath);
			gridView.addRoomInfo("ID", roomInfo.get("ID"));
			gridView.addRoomInfo("NAME", roomInfo.get("NAME"));
			gridView.addRoomInfo("OWNER", roomInfo.get("OWNER"));
			gridView.addRoomInfo("DESCRIPTION", roomInfo.get("DESCRIPTION"));
			gridView.addRoomInfo("TIMESTAMP", roomInfo.get("TIMESTAMP"));
			
			// set the room items
			gridView.setRoomItems(items);
			
			fileReader.close();
			
			System.out.println("Guest room loaded");
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadGuestRoom(): " + e.getClass().getName() + " - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// save a guest room file
	public static synchronized String saveGuestRoom(String email, HashMap<String,String> roomInfo, ArrayList<RoomItem> items, boolean newRoom)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// use the email specified
			filename = "rooms/guest/" + email + "/" + roomInfo.get("ID") + ".room";
		}
		else
		{
			// no email, so use the default account
			filename = "rooms/guest/default/" + roomInfo.get("ID") + ".room";
		}
		
		PrintWriter fileWriter;
		try
		{
			fileWriter = new PrintWriter(filename);
			
			// print out the room information
			fileWriter.println("// Room information");
			fileWriter.println();
			fileWriter.println("TEMPLATE: " + roomInfo.get("TEMPLATE"));
			fileWriter.println("ID: " + roomInfo.get("ID"));
			fileWriter.println("NAME: " + roomInfo.get("NAME"));
			fileWriter.println("OWNER: " + roomInfo.get("OWNER"));
			fileWriter.println("DESCRIPTION: " + roomInfo.get("DESCRIPTION"));
			fileWriter.println("TIMESTAMP: " + roomInfo.get("TIMESTAMP"));
			fileWriter.println();
			
			// print out the furniture information
			fileWriter.println("// Furniture");
			fileWriter.println();
			
			for(int i = 0; i < items.size(); i++)
			{
				// get the next item
				RoomItem r = items.get(i);
				
				if(r instanceof RoomFurniture)
				{
					// regular furniture
					fileWriter.println("FURNITURE: " + r.toString());
				}
				else if(r instanceof RoomPoster)
				{
					// poster
					fileWriter.println("POSTER: " + r.toString());
				}
			}
			
			fileWriter.close();
			
			// check if we created a new room
			if(newRoom)
			{
				// append a new entry to the room mappings file
				addRoomMapping(filename, roomInfo.get("ID"));
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in saveGuestRoom()");
			e.printStackTrace();
		}
		
		return filename; // return the saved path
	}
	
	// load and return an animation from a file
	public static Animation loadAnimation(String filename)
	{
		Scanner fileReader;
		
		String animationType = "";
		String animationName = "";
		int totalFrames = 0;
		int x_coord = 0;
		int y_coord = 0;
		Animation animation = new Animation();
		
		Scanner animationFrameScanner;
		int currentFrame = 0;
		int x = 0;
		int y = 0;
		
		try
		{
			fileReader = new Scanner(AppletResourceLoader.getFileFromJar(filename));
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("TYPE: "))
				{
					// figure out the animation type
					line = line.replaceAll("TYPE: ", "");
					animationType = line;
					if(animationType.equals("stationary"))
					{
						animation = new StationaryAnimation();
					}
					else
					{
						animation = new MovingAnimation();
					}
				}
				else if(line.startsWith("ANIMATION NAME: "))
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
					// stationary: image path,delay
					// moving: image path,delay,x,y
					// remove the commas and turn them into spaces
					line = line.replaceAll(",", " ");
					
					// remove the brackets
					line = line.replaceAll("@", "");
					
					animationFrameScanner = new Scanner(line);
					
					String imagePath = animationFrameScanner.next();
					
					// check if it's a moving animation
					if(!animationType.equals("stationary"))
					{
						x = Integer.parseInt(animationFrameScanner.next());
						y = Integer.parseInt(animationFrameScanner.next());
					}
					
					int delay = Integer.parseInt(animationFrameScanner.next());
					
					// create a new animation frame
					AnimationFrame newFrame = new AnimationFrame(currentFrame, AppletResourceLoader.getImageFromJar(imagePath), delay);
					newFrame.setX(x);
					newFrame.setY(y);
					
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
		animation.setPath(filename);
		return animation;
	}
	
	// create a new player with blank data files
	private static synchronized void createNewPlayerFiles(String username, String email)
	{
		PrintWriter fileWriter = null;
		String filename = "";
		
		long credits = 1000; // default amount of credits to give a new player
		
		int defaultBadges = 1; // how many badges to put on the new user
		
		try
		{
			// character file
			filename = "data/characters/" + email + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("USERNAME: " + username);
			fileWriter.println("CREDITS: " + credits);
			fileWriter.println("SIGNATURE: " + username); // give the player a default signature with only his username
			fileWriter.println("RATING: G"); // assign a default content rating of General
			
			if(username.startsWith("QA_") || username.startsWith("HOST_") || username.startsWith("VMK_"))
			{
				defaultBadges++; // staff get one extra badge when they create their account
				fileWriter.println("BADGE: badge_0"); // HVMK Staff badge
			}
			
			fileWriter.println("BADGE: badge_2"); // give the user a "Here From Day One" badge
			
			// print out empty badge entries
			for(int i = 0; i < StaticAppletData.MAX_DISPLAYABLE_BADGES - defaultBadges; i++)
			{
				fileWriter.println("BADGE: ");
			}
			
			// print out empty pin entries
			for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_PINS; j++)
			{
				fileWriter.println("PIN: ");
			}
			fileWriter.close();
			
			// inventory file
			filename = "data/inventory/" + email + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("// Furniture");
			fileWriter.println();
			fileWriter.println("// Pins");
			fileWriter.println();
			fileWriter.println("PIN: misc_pin_0"); // give the user an HVMK Virtual Pin
			fileWriter.println("PIN: magic_pin_0"); // give the user a Dancing Inferno Magic Pin
			fileWriter.println();
			fileWriter.println("// Posters");
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
		
		long credits = 1000;
		String signature = "";
		String contentRating = "";
		
		InventoryInfo displayedBadges[] = new InventoryInfo[StaticAppletData.MAX_DISPLAYABLE_BADGES];
		InventoryInfo displayedPins[] = new InventoryInfo[StaticAppletData.MAX_DISPLAYABLE_PINS];
		
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
					else if(line.startsWith("CREDITS: ")) // credits
					{
						line = line.replaceAll("CREDITS: ", "");
						credits = Long.parseLong(line);
					}
					else if(line.startsWith("SIGNATURE: ")) // signature
					{
						line = line.replaceAll("SIGNATURE: ", "");
						signature = line;
					}
					else if(line.startsWith("RATING: ")) // content rating
					{
						line = line.replaceAll("RATING: ", "");
						contentRating = line;
					}
					else if(line.startsWith("BADGE: ")) // badge
					{
						line = line.replaceAll("BADGE: ", "");
						displayedBadges[badgeNum] = StaticAppletData.getInvInfo(line);
						badgeNum++;
					}
					else if(line.startsWith("PIN: ")) // pin
					{
						line = line.replaceAll("PIN: ", "");
						displayedPins[pinNum] = StaticAppletData.getInvInfo(line);
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
				newCharacter.setCredits(credits);
				newCharacter.setSignature(signature);
				
				// create blank badges
				for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_BADGES; j++)
				{
					displayedBadges[j] = StaticAppletData.getInvInfo("");
				}
				
				// create blank pins
				for(int j = 0; j < StaticAppletData.MAX_DISPLAYABLE_PINS; j++)
				{
					displayedPins[j] = StaticAppletData.getInvInfo("");
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
		newCharacter.setCredits(credits);
		newCharacter.setSignature(signature);
		newCharacter.setContentRating(contentRating);
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
			
			// write out the credits
			fileWriter.println("CREDITS: " + character.getCredits());
			
			// write out the signature
			fileWriter.println("SIGNATURE: " + character.getSignature());
			
			// write out the content rating
			fileWriter.println("RATING: " + character.getContentRatingAsString());
			
			// write out the badges
			InventoryInfo[] displayedBadges = character.getDisplayedBadges();
			for(int i = 0; i < displayedBadges.length; i++)
			{
				fileWriter.println("BADGE: " + displayedBadges[i].getID());
			}
			
			// write out the pins
			InventoryInfo[] displayedPins = character.getDisplayedPins();
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
	public static HashMap<String,InventoryInfo> loadInventoryMappings()
	{
		String filename = "data/mappings/inventoryMappings.dat";
		HashMap<String,InventoryInfo> inventoryMappings = new HashMap<String,InventoryInfo>();
		
		Scanner fileReader;
		
		String invID = "";
		String invName = "";
		String invPath = "";
		String invCardPath = "";
		String invIconPath = "";
		int invRatingIndex = 0;
		int invTiles = 0;
		
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
						// get the inventory ID
						line = line.replaceAll("ID: ", "");
						invID = line;
					}
					else if(line.startsWith("NAME: "))
					{
						// get the inventory name
						line = line.replaceAll("NAME: ", "");
						invName = line;
					}
					else if(line.startsWith("PATH: "))
					{
						// get the inventory path
						line = line.replaceAll("PATH: ", "");
						invPath = line;
					}
					else if(line.startsWith("CARD: "))
					{
						// get the inventory card path
						line = line.replaceAll("CARD: ", "");
						invCardPath = line;
					}
					else if(line.startsWith("ICON: "))
					{
						// get the inventory icon path (used in the Inventory window)
						line = line.replaceAll("ICON: ", "");
						invIconPath = line;
					}
					else if(line.startsWith("TILES: "))
					{
						// get the inventory tiles
						line = line.replaceAll("TILES: ", "");
						invTiles = Integer.parseInt(line);
					}
					else if(line.startsWith("RATING: "))
					{
						// get the content rating
						line = line.replaceAll("RATING: ", "");
						invRatingIndex = RatingSystem.getContentRatingIndex(line);
					}
					else if(line.startsWith("@END@"))
					{
						// add the inventory mapping to the HashMap
						inventoryMappings.put(invID, new InventoryInfo(invID, invName, invPath, invCardPath, invIconPath, invTiles, invRatingIndex));
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				return inventoryMappings;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadInventoryMappings(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new mappings list from the file data
		return inventoryMappings;
	}
	
	// load a player's inventory
	public static ArrayList<InventoryItem> loadInventory(String email)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// load the inventory file
			filename = "data/inventory/" + email + ".dat";
		}
		else
		{
			// load the default inventory file
			filename = "data/inventory/default.dat";
		}
		
		ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
		String inventoryID = "";
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
						if(line.startsWith("FURNITURE: "))
						{
							// get the furniture item
							line = line.replaceAll("FURNITURE: ", "");
							inventoryID = line;
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.FURNITURE));
						}
						else if(line.startsWith("PIN: "))
						{
							// get the pin item
							line = line.replaceAll("PIN: ", "");
							inventoryID = line;
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.PIN));
						}
						else if(line.startsWith("POSTER: "))
						{
							// get the poster item
							line = line.replaceAll("POSTER: ", "");
							inventoryID = line;
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.POSTER));
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
				// return the default empty inventory list
				return inventoryItems;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadInventory(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new sorted inventory list from the file data
		Collections.sort(inventoryItems);
		return inventoryItems;
	}
	
	// save a player's inventory
	public static synchronized void saveInventory(String email, ArrayList<InventoryItem> items)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the inventory file
			filename = "data/inventory/" + email + ".dat";
		}
		else
		{
			// save the default inventory file
			filename = "data/inventory/default.dat";
		}
		
		PrintWriter fileWriter;
		try
		{
			fileWriter = new PrintWriter(filename);
			
			for(int i = 0; i < items.size(); i++)
			{
				// get the next item
				InventoryItem r = items.get(i);
				
				if(r.getType() == InventoryItem.FURNITURE)
				{
					// regular furniture
					fileWriter.println("FURNITURE: " + r.getId());
				}
				else if(r.getType() == InventoryItem.PIN)
				{
					// pin
					fileWriter.println("PIN: " + r.getId());
				}
				else if(r.getType() == InventoryItem.POSTER)
				{
					// poster
					fileWriter.println("POSTER: " + r.getId());
				}
			}
			
			fileWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in saveInventory()");
			e.printStackTrace();
		}
	}
	
	// get a HashMap of room information from a room file
	private synchronized static HashMap<String,String> getInfoFromRoom(String path)
	{
		HashMap<String,String> infoMap = new HashMap<String,String>();
		Scanner fileReader;
		
		try
		{
			InputStream is = AppletResourceLoader.getCharacterFromJar(path);

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
						// get the room ID
						line = line.replaceAll("ID: ", "");
						infoMap.put("ID", line);
					}
					else if(line.startsWith("NAME: "))
					{
						// get the room name
						line = line.replaceAll("NAME: ", "");
						infoMap.put("NAME", line);
					}
					else if(line.startsWith("OWNER: "))
					{
						// get the room owner
						line = line.replaceAll("OWNER: ", "");
						infoMap.put("OWNER", line);
					}
					else if(line.startsWith("DESCRIPTION: "))
					{
						// get the room description
						line = line.replaceAll("DESCRIPTION: ", "");
						infoMap.put("DESCRIPTION", line);
					}
					else if(line.startsWith("COST: "))
					{
						// get the room cost (guest rooms only)
						line = line.replaceAll("COST: ", "");
						infoMap.put("COST", line);
					}
					else if(line.startsWith("TIMESTAMP: "))
					{
						// get the room timestamp (guest rooms only)
						line = line.replaceAll("TIMESTAMP: ", "");
						infoMap.put("TIMESTAMP", line);
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				return infoMap;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN getInfoFromRoom(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new information map from the file data
		infoMap.put("PATH", path);
		return infoMap;
	}
	
	// load the room mappings
	public static HashMap<String,VMKRoom> loadRoomMappings()
	{
		String filename = "data/mappings/roomNames.dat";
		HashMap<String,VMKRoom> roomMappings = new HashMap<String,VMKRoom>();
		
		Scanner fileReader;
		
		String roomID = "";
		String roomName = "";
		String roomPath = "";
		String roomOwner = "";
		String roomDescription = "";
		
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
						// get the room ID
						line = line.replaceAll("ID: ", "");
						roomID = line;
						
						// check if it's a guest room template
						if(roomID.startsWith("template_"))
						{
							// get the ID of the template
							String templateID = roomID.replaceAll("template_", "");
							
							// add the template ID to the list of templates
							StaticAppletData.addGuestRoomTemplate(templateID);
						}
						else if(roomID.startsWith("gr"))
						{
							// it's an actual guest room
							VMKServerPlayerData.incrementGuestRoomCount(); // increment the number of guest rooms
						}
					}
					else if(line.startsWith("PATH: "))
					{
						// get the room path
						line = line.replaceAll("PATH: ", "");
						roomPath = line;
						
						// get the information map from the room
						HashMap<String,String> infoMap = getInfoFromRoom(roomPath);
						
						// get each element of room information
						roomName = infoMap.get("NAME");
						roomOwner = infoMap.get("OWNER");
						roomDescription = infoMap.get("DESCRIPTION");
						
						// add the room mapping to the HashMap
						VMKRoom newRoom = new VMKRoom(roomID, roomName, roomPath);
						newRoom.setRoomOwner(roomOwner);
						newRoom.setRoomDescription(roomDescription);
						
						// check if there is a given cost for the room (guest rooms only)
						if(infoMap.containsKey("COST"))
						{
							// add the cost to the room definition as well
							newRoom.setRoomCost(Long.parseLong(infoMap.get("COST")));
						}
						
						roomMappings.put(roomID, newRoom);
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				return roomMappings;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadRoomMappings(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new mappings list from the file data
		return roomMappings;
	}
	
	// append a room mapping to the file
	private static void addRoomMapping(String path, String id)
	{
		String filename = "data/mappings/roomNames.dat";
		
		try
		{	
			// open the file for appending
			FileOutputStream appendedFile = new FileOutputStream(filename, true);
			PrintWriter writer = new PrintWriter(appendedFile);
			
			// write the mapping to the file
			writer.println();
			writer.println("ID: " + id);
			writer.println("PATH: " + path);
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN saveUsernameEmailMappings()");
			e.printStackTrace();
		}
	}
	
	// load the tile destinations for the Room Editor
	public static String[] loadEditorTileDestinations()
	{
		String filename = "tileDestinations.dat";
		ArrayList<String> destinations = new ArrayList<String>();
		String[] destinationArray;
		
		Scanner fileReader;
		
		String dest = "";
		
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
					else if(line.startsWith("DEST: "))
					{
						// get the room ID
						line = line.replaceAll("DEST: ", "");
						dest = line;
						
						if(dest == null)
						{
							destinations.add("");
						}
						else
						{
							destinations.add(dest);
						}
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default empty mappings list
				destinationArray = new String[0];
				return destinations.toArray(destinationArray);
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadEditorTileDestinations(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new mappings list from the file data
		destinationArray = new String[destinations.size()];
		return destinations.toArray(destinationArray);
	}
	
	// load a dictionary given a content rating (G, PG, PG13)
	public static ArrayList<String> loadDictionary(String contentRating)
	{
		String filename = "data/mappings/" + contentRating.toLowerCase().replace("-", "") + "Dictionary.dat";
		ArrayList<String> dictionary = new ArrayList<String>();
		
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
					
					if(line.equals("") || line.startsWith(commentDelimeter))
					{
						// reached a blank line/comment line, so ignore
					}
					else
					{
						dictionary.add(line);
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default dictionary
				return dictionary;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadDictionary(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new dictionary from the file data
		return dictionary;
	}
}
