// FileOperations.java by Matt Fritz
// November 7, 2009
// Handles the saving and loading of the game's data files

// TODO: Include functionality for reading in STATIONARY room objects

package util;

import games.fireworks.FireworkEntry;
import gridobject.GridObject;
import gridobject.StationaryGridObject;
import interfaces.GridSortable;
import interfaces.GridViewable;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import clickable.ClickableArea;

import animations.Animation;
import animations.MovingAnimation;
import animations.StationaryAnimation;
import animations.AnimationFrame;
import astar.AStarCharacter;
import astar.AStarCharacterBasicData;

import roomobject.RoomFurniture;
import roomobject.RoomItem;
import roomobject.RoomPoster;
import rooms.VMKRoom;
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
	public static void saveFile(String filename, String backgroundImagePath, HashMap<String,String> roomInfo, HashMap<String,Tile> tiles, ArrayList<Animation> animations, ArrayList<SoundPlayable> sounds, ArrayList<RoomItem> roomItems, ArrayList<ClickableArea> clickableAreas, ArrayList<GridSortable> gridObjects, String tileSize)
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
			
			// write out the clickable areas
			fileWriter.println("// Clickable areas");
			fileWriter.println();
			for(int k = 0; k < clickableAreas.size(); k++)
			{
				ClickableArea area = clickableAreas.get(k);
				
				// write out the clickable area
				fileWriter.println("CLICKABLE AREA: " + area.toString());
			}
			fileWriter.println();
			
			// write out the stationary objects
			fileWriter.println("// Grid objects ");
			fileWriter.println();
			for(int l = 0; l < gridObjects.size(); l++)
			{
				GridObject obj = (GridObject)gridObjects.get(l);
				
				// write out the object
				if(obj instanceof StationaryGridObject)
				{
					fileWriter.println("STATIONARY OBJECT: " + obj.toString());
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
		ArrayList<ClickableArea> clickableAreas = new ArrayList<ClickableArea>();
		ArrayList<GridSortable> gridObjects = new ArrayList<GridSortable>();
		
		Scanner tileScanner;
		
		String roomID = "";
		
		try
		{
			fileReader = new Scanner(filename);
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("ID: "))
				{
					roomID = line.replaceFirst("ID: ", "");
					
					// set the room ID if it isn't a guest room
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("ID", roomID);
					}
				}
				else if(line.startsWith("NAME: "))
				{
					line = line.replaceFirst("NAME: ", "");
					
					// set the room name
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("NAME", line);
					}
				}
				else if(line.startsWith("OWNER: "))
				{
					line = line.replaceFirst("OWNER: ", "");
					
					// set the room owner
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("OWNER", line);
					}
				}
				else if(line.startsWith("DESCRIPTION: "))
				{
					line = line.replaceFirst("DESCRIPTION: ", "");
					
					// set the room description
					if(!roomID.startsWith("gr"))
					{
						gridView.addRoomInfo("DESCRIPTION", line);
					}
				}
				else if(line.startsWith("IMAGE: "))
				{
					// set the background image path
					backgroundImagePath = line.replaceFirst("IMAGE: ", "");
				}
				else if(line.startsWith("TILES: "))
				{
					// get the size of the tiles
					tileDimensions = line.replaceFirst("TILES: ", "").split("x"); // split at the "x"
				}
				else if(line.startsWith("SOUND: "))
				{
					// load the sound and it to the ArrayList
					SoundPlayable sound = loadSound(line.replaceFirst("SOUND: ", ""));
					if(sound != null)
					{
						// only add the sound if it returned a valid resource
						sounds.add(sound);
					}
				}
				else if(line.startsWith("ANIMATION: "))
				{
					line = line.replaceFirst("ANIMATION: ", "");
					
					// make sure an animation exists
					if(!line.equals("none"))
					{
						System.out.println("Loading animation: " + line + "...");
						
						// load an animation and add it to the ArrayList
						animations.add(loadAnimation(line));
					}
				}
				else if(line.startsWith("CLICKABLE AREA: "))
				{
					// create a new ClickableArea from the String
					clickableAreas.add(ClickableArea.fromString(line.replaceFirst("CLICKABLE AREA: ", "")));
				}
				else if(line.startsWith("STATIONARY OBJECT: "))
				{
					// create a new StationaryObject from the String
					gridObjects.add(StationaryGridObject.fromString(line.replaceFirst("STATIONARY OBJECT: ", "")));
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
			
			// set the clickable areas
			gridView.setClickableAreas(clickableAreas);
			
			// set the grid objects
			gridView.setGridObjects(gridObjects);
			
			// check to make sure this isn't a guest room since there could be a music override
			if(!roomID.startsWith("gr"))
			{
				// start the sounds
				gridView.startSounds();
			}
			
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
	
	// load and return a sound from a file
	public static SoundPlayable loadSound(String filename)
	{
		Scanner fileReader;
		
		String type = "";
		String name = "";
		String path = "";
		int bufferSize = -1;
		int length = -1;
		int delay = 0;
		
		SoundPlayable sound = null;

		try
		{
			fileReader = new Scanner(AppletResourceLoader.getFileFromJar(filename));
			
			while(fileReader.hasNextLine())
			{
				String line = fileReader.nextLine();
				
				if(line.startsWith("TYPE: "))
				{
					// get the sound type
					type = line.replaceFirst("TYPE: ", "");
				}
				else if(line.startsWith("NAME: "))
				{
					// get the sound name
					name = line.replaceFirst("NAME: ", "");
				}
				else if(line.startsWith("PATH: "))
				{
					// get the sound path
					path = line.replaceFirst("PATH: ", "");
				}
				else if(line.startsWith("BUFFER SIZE: "))
				{
					// get the sound buffer size
					bufferSize = Integer.parseInt(line.replaceFirst("BUFFER SIZE: ", ""));
				}
				else if(line.startsWith("LENGTH: "))
				{
					// get the sound length
					length = Integer.parseInt(line.replaceFirst("LENGTH: ", ""));
				}
				else if(line.startsWith("DELAY: "))
				{
					// get the sound delay
					delay = Integer.parseInt(line.replaceFirst("DELAY: ", ""));
				}
				else if(line.startsWith(commentDelimeter) || line.equals(""))
				{
					// do nothing since it's either a comment or a blank line
				}
			}
			
			fileReader.close();
			
			System.out.println("Sound \"" + name + "\" loaded from file");
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadSound(): " + e.getClass().getName() + " - " + e.getMessage());
		}
		
		// check to see if we have received a playlist file first
		if(path.toLowerCase().contains(".pls"))
		{
			// parse the available stream from the .pls playlist return it
			sound = getStreamFromPlaylist(path, "pls");
		}
		else if(path.toLowerCase().contains(".m3u"))
		{
			// parse the available stream from the .m3u playlist and return it
			sound = getStreamFromPlaylist(path, "m3u");
		}
		else
		{
			// check the type of the sound before we create the object
			if(type.equals("REPEATING"))
			{
				System.out.println("Creating repeating sound (" + name + ")...");
				
				// create a repeating sound with dual buffers for an attempt at continuous sound
				sound = new RepeatingSound(name, length, delay, path, AppletResourceLoader.getSoundFromJar(path, bufferSize));
				
				System.out.println("Created repeating sound (" + name + ")!");
				
				// add a dual buffer if the buffer size there should be no delay when the sound repeats
				if(delay == 0 && !path.startsWith("http:"))
				{
					// add the second buffer
					sound.addDualBuffer(AppletResourceLoader.getSoundFromJar(path, bufferSize));
				}
			}
			else if(type.equals("SINGLE"))
			{
				// create a single sound
				sound = new SingleSound(name, path, AppletResourceLoader.getSoundFromJar(path, bufferSize));
			}
		}
		
		// return the sound
		return sound;
	}
	
	// parse an online playlist .pls file to find the next available stream
	public static synchronized SoundPlayable getStreamFromPlaylist(String path, String type)
	{
		Scanner fileReader;
		InputStream inputStream = null;
		String line = "";
		
		ArrayList<String> streams = new ArrayList<String>();
		
		SoundPlayable sound = null;
		
		try
		{
			inputStream = AppletResourceLoader.getFileFromJar(path);
			fileReader = new Scanner(inputStream);
			
			while(fileReader.hasNextLine())
			{
				line = fileReader.nextLine();
				
				if(type.equals("pls")) // Winamp playlist file
				{
					// check to see if this line contains a stream
					if(line.startsWith("File") && line.contains("="))
					{
						// get the URL of the stream and add it to the list of streams
						int startOfStream = line.indexOf("=") + 1;
						line = line.substring(startOfStream);
						streams.add(line);
					}
				}
				else if(type.equals("m3u")) // m3u playlist file
				{
					// check to see if this line contains a reference to a multimedia file
					if(line.startsWith("http:"))
					{
						// check to see if this line redirects to another playlist file
						if(line.contains(".m3u"))
						{
							// follow the redirection to the m3u playlist
							fileReader.close();
							inputStream.close();
							return getStreamFromPlaylist(line, "m3u");
						}
						else if(line.contains(".pls"))
						{
							// follow the redirection to the pls playlist
							fileReader.close();
							inputStream.close();
							return getStreamFromPlaylist(line, "pls");
						}
						else
						{
							// add the line to the stream
							streams.add(line);
						}
					}
				}
			}
			
			// close the scanner
			fileReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// iterate through the streams
		for(int i = 0; i < streams.size(); i++)
		{
			// get the next stream
			String stream = streams.get(i);
			
			// try to connect to the stream
			sound = new SingleSound(stream, stream, AppletResourceLoader.getSoundFromJar(stream, -1));
			
			// check to make sure we successfully connected to the stream
			if(sound != null)
			{
				// let the user know we're playing the sound
				System.out.println("Loaded online stream: " + stream);
				
				// return the sound stream
				return sound;
			}
		}
		
		// return the sound stream
		return null;
	}
	
	// load a Guest Room from a file
	public static synchronized void loadGuestRoom(String roomPath, GridViewable gridView)
	{
		Scanner fileReader;
		ArrayList<RoomItem> items = new ArrayList<RoomItem>(); // room items like furniture and posters
		HashMap<String,Tile> tiles = new HashMap<String,Tile>();
		
		String filename = roomPath;
		
		String musicPath = "";
		SoundPlayable sound = null;
		
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
					line = line.replaceFirst("TEMPLATE: ", "");
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
				else if(line.startsWith("MUSIC: "))
				{
					// a music override has been specified
					musicPath = line.replaceFirst("MUSIC: ", "");
				}
				else if(line.startsWith(commentDelimeter) || line.equals(""))
				{
					// comment line or blank line, so ignore
				}
			}
			
			// check to see if a music override was specified
			if(!musicPath.equals(""))
			{
				gridView.setLoadingDescription("Loading online audio stream...");
				
				// check to see if we have received a playlist file first
				if(musicPath.toLowerCase().contains(".pls"))
				{
					// parse the available stream from the .pls playlist return it
					sound = getStreamFromPlaylist(musicPath, "pls");
				}
				else if(musicPath.toLowerCase().contains(".m3u"))
				{
					// parse the available stream from the .m3u playlist and return it
					sound = getStreamFromPlaylist(musicPath, "m3u");
				}
				else
				{
					// create a connection to the stream directly
					sound = new SingleSound(musicPath, musicPath, AppletResourceLoader.getSoundFromJar(musicPath, -1));
				}
				
				// stop the current sounds
				gridView.stopSounds();
				
				// create a new list of sounds and add the new overridden music stream
				ArrayList<SoundPlayable> sounds = new ArrayList<SoundPlayable>();
				sounds.add(sound);
				gridView.setSounds(sounds);
			}
			
			// set the room information first
			HashMap<String,String> roomInfo = getInfoFromRoom(roomPath);
			gridView.addRoomInfo("ID", roomInfo.get("ID"));
			gridView.addRoomInfo("NAME", roomInfo.get("NAME"));
			gridView.addRoomInfo("OWNER", roomInfo.get("OWNER"));
			gridView.addRoomInfo("DESCRIPTION", roomInfo.get("DESCRIPTION"));
			gridView.addRoomInfo("TIMESTAMP", roomInfo.get("TIMESTAMP"));
			
			// check to see if there was a music override
			if(!musicPath.equals(""))
			{
				// add the music override so it can be saved to the file later
				gridView.addRoomInfo("MUSIC", musicPath);
			}
			
			// set the room items
			gridView.setRoomItems(items);
			
			// start the sounds
			gridView.startSounds();
			
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
			filename = GameConstants.PATH_GUEST_ROOMS + email + "/" + roomInfo.get("ID") + ".room";
		}
		else
		{
			// no email, so use the default account
			filename = GameConstants.PATH_GUEST_ROOMS_DEFAULT + roomInfo.get("ID") + ".room";
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
			
			// check to see if there was a music override specified
			if(roomInfo.get("MUSIC") != null && !roomInfo.get("MUSIC").equals(""))
			{
				// write out the music override to the file
				fileWriter.println("// Music override");
				fileWriter.println();
				fileWriter.println("MUSIC: " + roomInfo.get("MUSIC"));
				fileWriter.println();
			}
			
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
					animationType = line.replaceFirst("TYPE: ", "");
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
					animationName = line.replaceFirst("ANIMATION NAME: ", "");
					
					// set the animation name
					animation.setName(animationName);
				}
				else if(line.startsWith("TOTAL FRAMES: "))
				{
					totalFrames = Integer.parseInt(line.replaceFirst("TOTAL FRAMES: ", ""));
					
					// set the total number of frames
					animation.setTotalFrames(totalFrames);
				}
				else if(line.startsWith("X-COORD: "))
				{
					x_coord = Integer.parseInt(line.replaceFirst("X-COORD: ", ""));
					
					// set the x-coordinate
					animation.setX(x_coord);
				}
				else if(line.startsWith("Y-COORD: "))
				{
					y_coord = Integer.parseInt(line.replaceFirst("Y-COORD: ", ""));
					
					// set the y-coordinate
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
	private static synchronized void createNewPlayerFiles(AStarCharacterBasicData basicData)
	{
		PrintWriter fileWriter = null;
		String filename = "";
		
		long credits = 1000; // default amount of credits to give a new player
		
		int defaultBadges = 2; // how many badges to put on the new user
		
		try
		{
			// character file
			filename = GameConstants.PATH_CHARACTERS + basicData.getEmail() + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("USERNAME: " + basicData.getUsername());
			fileWriter.println("GENDER: " + basicData.getGender());
			fileWriter.println("CREDITS: " + credits);
			fileWriter.println("SIGNATURE: " + basicData.getUsername()); // give the player a default signature with only his username
			fileWriter.println("RATING: G"); // assign a default content rating of General
			
			// assign some default clothing
			fileWriter.println("BASE AVATAR: base_0_0");
			fileWriter.println("HAIR: hair_0_0");
			fileWriter.println("EYES: eyes_0_0");
			fileWriter.println("MOUTH: mouth_0_0");
			fileWriter.println("FACIALHAIR: ");
			fileWriter.println("SHIRT: shirt_0_0");
			fileWriter.println("SHOES: shoes_0_0");
			fileWriter.println("PANTS: pants_0_0");
			fileWriter.println("HAT: hat_0");
			
			if(basicData.getUsername().startsWith("QA_") || basicData.getUsername().startsWith("HOST_") || basicData.getUsername().startsWith("VMK_"))
			{
				defaultBadges++; // staff get one extra badge when they create their account
				fileWriter.println("BADGE: badge_0"); // HVMK Staff badge
			}
			
			fileWriter.println("BADGE: badge_2"); // give the user a "Here From Day One" badge
			fileWriter.println("BADGE: badge_3"); // give the user a "Charter Member" badge
			
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
			filename = GameConstants.PATH_INVENTORY + basicData.getEmail() + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("// Clothing");
			fileWriter.println();
			fileWriter.println("CLOTHING: shirt_0_0");
			fileWriter.println("CLOTHING: pants_0_0");
			fileWriter.println("CLOTHING: shoes_0_0");
			fileWriter.println("CLOTHING: hat_0");
			fileWriter.println();
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
			filename = GameConstants.PATH_FRIENDS + basicData.getEmail() + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println(); // blank friends file
			fileWriter.close();
			
			// messages file (give them one new message from VMK Staff)
			filename = GameConstants.PATH_MESSAGES + basicData.getEmail() + ".dat";
			fileWriter = new PrintWriter(filename);
			fileWriter.println("SENDER: HVMK Staff");
			fileWriter.println("DATE: " + new Date().toString());
			fileWriter.println("BODY: " + newPlayerMessage);
			fileWriter.println();
			fileWriter.close();
			
			// create the new user's Guest Rooms folder
			new File(GameConstants.PATH_GUEST_ROOMS + basicData.getEmail()).mkdir();
			
			// create the new user's Avatar folder
			new File(GameConstants.PATH_AVATAR_IMAGES + basicData.getEmail()).mkdir();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN createNewPlayerFiles()");
			e.printStackTrace();
		}
	}
	
	// load a character given an email address
	public static synchronized AStarCharacter loadCharacter(AStarCharacterBasicData basicData)
	{
		String username = "";
		boolean isNewPlayer = false;
		String filename = "";
		
		if(!basicData.getEmail().equals(""))
		{
			filename = GameConstants.PATH_CHARACTERS + basicData.getEmail() + ".dat"; // filename of the character file
		}
		else
		{
			filename = GameConstants.PATH_CHARACTERS_DEFAULT; // load default character file
		}
		
		// make sure the files exist; if not, create the necessary files
		if(!(new File(filename).exists()))
		{
			// create a new default player with the given email address
			isNewPlayer = true;
			createNewPlayerFiles(basicData);
		}
		
		Scanner fileReader;
		
		long credits = 1000;
		String signature = "";
		String contentRating = "G";
		
		String baseAvatarID = "base_0_0";
		String hairID = "hair_0_0";
		String eyesID = "eyes_0_0";
		String mouthID = "mouth_0_0";
		String facialhairID = "";
		String shirtID = "shirt_0_0";
		String shoesID = "shoes_0_0";
		String pantsID = "pants_0_0";
		String hatID = "hat_0";
		
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
						username = line.replaceFirst("USERNAME: ", "");
					}
					else if(line.startsWith("CREDITS: ")) // credits
					{
						credits = Long.parseLong(line.replaceFirst("CREDITS: ", ""));
					}
					else if(line.startsWith("SIGNATURE: ")) // signature
					{
						signature = line.replaceFirst("SIGNATURE: ", "");
					}
					else if(line.startsWith("RATING: ")) // content rating
					{
						contentRating = line.replaceFirst("RATING: ", "");
					}
					else if(line.startsWith("BASE AVATAR: ")) // base avatar ID
					{
						baseAvatarID = line.replaceFirst("BASE AVATAR: ", "");
					}
					else if(line.startsWith("HAIR: ")) // hair ID
					{
						hairID = line.replaceFirst("HAIR: ", "");
					}
					else if(line.startsWith("EYES: ")) // eyes ID
					{
						eyesID = line.replaceFirst("EYES: ", "");
					}
					else if(line.startsWith("MOUTH: ")) // mouth ID
					{
						mouthID = line.replaceFirst("MOUTH: ", "");
					}
					else if(line.startsWith("FACIALHAIR: ")) // facial-hair ID
					{
						facialhairID = line.replaceFirst("FACIALHAIR: ", "");
					}
					else if(line.startsWith("SHIRT: ")) // shirt ID
					{
						shirtID = line.replaceFirst("SHIRT: ", "");
					}
					else if(line.startsWith("SHOES: ")) // shoes ID
					{
						shoesID = line.replaceFirst("SHOES: ", "");
					}
					else if(line.startsWith("PANTS: ")) // pants ID
					{
						pantsID = line.replaceFirst("PANTS: ", "");
					}
					else if(line.startsWith("HAT: ")) // hat ID
					{
						hatID = line.replaceFirst("HAT: ", "");
					}
					else if(line.startsWith("BADGE: ")) // badge
					{
						displayedBadges[badgeNum] = StaticAppletData.getInvInfo(line.replaceFirst("BADGE: ", ""));
						badgeNum++;
					}
					else if(line.startsWith("PIN: ")) // pin
					{
						displayedPins[pinNum] = StaticAppletData.getInvInfo(line.replaceFirst("PIN: ", ""));
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
				// new character creation didn't work, so the file doesn't exist
				// create a new character and don't worry about it
				AStarCharacter newCharacter = new AStarCharacter(username, 15, 7);
				newCharacter.setEmail(basicData.getEmail());
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
		newCharacter.setGender(basicData.getGender());
		newCharacter.setEmail(basicData.getEmail());
		newCharacter.setCredits(credits);
		newCharacter.setSignature(signature);
		newCharacter.setContentRating(contentRating);
		newCharacter.setBaseAvatarID(baseAvatarID);
		newCharacter.setHairID(hairID);
		newCharacter.setEyesID(eyesID);
		newCharacter.setMouthID(mouthID);
		newCharacter.setFacialhairID(facialhairID);
		newCharacter.setShirtID(shirtID);
		newCharacter.setShoesID(shoesID);
		newCharacter.setPantsID(pantsID);
		newCharacter.setHatID(hatID);
		newCharacter.setDisplayedBadges(displayedBadges);
		newCharacter.setDisplayedPins(displayedPins);
		
		// check to see if this is a new player
		if(isNewPlayer)
		{
			// it's a new player, so create default clothing for him in his avatar directory
			buildAvatarImages(newCharacter);
		}
		
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
			filename = GameConstants.PATH_CHARACTERS + character.getEmail() + ".dat";
		}
		else
		{
			// save the default character file
			filename = GameConstants.PATH_CHARACTERS_DEFAULT;
		}
		
		try
		{
			fileWriter = new PrintWriter(filename);
			
			// write out the username
			fileWriter.println("USERNAME: " + character.getUsername());
			
			// write out the gender
			fileWriter.println("GENDER: " + character.getGender());
			
			// write out the credits
			fileWriter.println("CREDITS: " + character.getCredits());
			
			// write out the signature
			fileWriter.println("SIGNATURE: " + character.getSignature());
			
			// write out the content rating
			fileWriter.println("RATING: " + character.getContentRatingAsString());
			
			// write out the clothing information
			fileWriter.println("BASE AVATAR: " + character.getBaseAvatarID());
			fileWriter.println("HAIR: " + character.getHairID());
			fileWriter.println("EYES: " + character.getEyesID());
			fileWriter.println("MOUTH: " + character.getMouthID());
			fileWriter.println("FACIALHAIR: " + character.getFacialhairID());
			fileWriter.println("SHIRT: " + character.getShirtID());
			fileWriter.println("SHOES: " + character.getShoesID());
			fileWriter.println("PANTS: " + character.getPantsID());
			fileWriter.println("HAT: " + character.getHatID());
			
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
	
	// take a character and basically re-create his clothing and avatar images
	public static synchronized AStarCharacter buildAvatarImages(AStarCharacter character)
	{
		// figure out what the email should be
		String email = "";
		if(character.getEmail().equals(""))
		{
			email = GameConstants.CONST_DEFAULT_EMAIL;
		}
		else
		{
			email = character.getEmail();
		}
		
		// grab the character from the parameter
		AStarCharacter theCharacter = character;
		
		// directions that the character needs
		String directions[] = GameConstants.CONST_CHARACTER_DIRECTIONS_ARRAY;
		
		// resolve the forward-facing directions as an ArrayList for comparisons
		ArrayList<String> forwardDirections = new ArrayList<String>(Arrays.asList(GameConstants.CONST_CHARACTER_DIRECTIONS_FORWARD_ARRAY));
		
		// animations that the character needs
		String animations[] = GameConstants.CONST_CHARACTER_ANIMS_ARRAY;
		
		// sizes that the character needs
		String sizes[] = {"64","48","32"};
		
		// image objects for the avatar compositions
		BufferedImage base = null;
		BufferedImage head = null;
		BufferedImage hair = null;
		BufferedImage eyes = null;
		BufferedImage mouth = null;
		BufferedImage facialhair = null;
		BufferedImage shirt = null;
		BufferedImage shoes = null;
		BufferedImage pants = null;
		BufferedImage hat = null;
		
		try
		{
			// iterate through the necessary directions
			for(String direction : directions)
			{
				// iterate through the necessary animations
				for(String animation: animations)
				{
					// iterate through the necessary sizes
					for(String size: sizes)
					{
						// get the respective images given the clothing IDs for elements that share the same animation names
						base = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_BASE_IMAGES + character.getBaseAvatarID() + "/" + character.getBaseAvatarID() + "_" + direction + animation + size + ".png");
						shirt = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_SHIRTS_IMAGES + character.getShirtID() + "/" + character.getShirtID() + "_" + direction + animation + size + ".png");
						shoes = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_SHOES_IMAGES + character.getShoesID() + "/" + character.getShoesID() + "_" + direction + animation + size + ".png");
						pants = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_PANTS_IMAGES + character.getPantsID() + "/" + character.getPantsID() + "_" + direction + animation + size + ".png");
						
						// check to see if there is actually a hat specified
						if(!character.getHatID().equals(""))
						{
							hat = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_HATS_IMAGES + character.getHatID() + "/" + character.getHatID() + "_" + direction + "_" + size + ".png");
						}
						
						// resolve the head from the base
						head = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_HEAD_IMAGES + character.getHeadID() + "/" + character.getHeadID() + "_" + direction + "_" + size + ".png");
						
						// resolve the hair
						hair = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_HAIR_IMAGES + character.getHairID() + "/" + character.getHairID() + "_" + direction + "_" + size + ".png");
						
						// should we construct any forward-facing elements?
						if(forwardDirections.contains(direction))
						{
							// also build the exclusively-forward-facing elements
							eyes = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_EYES_IMAGES + character.getEyesID() + "/" + character.getEyesID() + "_" + direction + "_" + size + ".png");
							mouth = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_MOUTH_IMAGES + character.getMouthID() + "/" + character.getMouthID() + "_" + direction + "_" + size + ".png");
							
							// make sure the avatar actually has some facial-hair
							if(!character.getFacialhairID().equals(""))
							{
								facialhair = AppletResourceLoader.getBufferedImageFromJar(GameConstants.PATH_CLOTHING_FACIAL_HAIR_IMAGES + character.getFacialhairID() + "/" + character.getFacialhairID() + "_" + direction + "_" + size + ".png");
							}
						}
						
						// create the combined BufferedImage object and allow for transparency
						BufferedImage combined = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
						
						// create the Graphics instance so we can draw on the canvas
						Graphics g = combined.getGraphics();
						
						// apply all the base images in the necessary order so they layer properly
						g.drawImage(base,0,0,null);
						
						// draw the clothing images before the head so the head can layer on top of the clothes
						g.drawImage(shirt,0,0,null);
						g.drawImage(shoes,0,0,null);
						g.drawImage(pants,0,0,null);
						
						// draw the head and hair on top of the clothing elements
						g.drawImage(head,0,0,null);
						g.drawImage(hair,0,0,null);
						
						// check to see if we need to draw the forward-facing images
						if(eyes != null)
						{
							// we need to draw the forward-facing images
							g.drawImage(eyes,0,0,null);
							g.drawImage(mouth,0,0,null);
							
							// facial-hair is optional, so we have to check that separately
							if(facialhair != null)
							{
								// draw the facial-hair
								g.drawImage(facialhair,0,0,null);
							}
						}
						
						// check to see if a hat has been specified
						if(!character.getHatID().equals(""))
						{
							g.drawImage(hat,0,0,null);
						}
						
						g.dispose();
						
						// write the generated image back out to the player's avatar folder
						ImageIO.write(combined,"png",new File(GameConstants.PATH_AVATAR_IMAGES + email + "/avatar_" + direction + animation + size + ".png"));
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN createAvatarImages()");
			e.printStackTrace();
		}
		
		return theCharacter;
	}
	
	// load a friends list given a player's email address
	public static synchronized FriendsList loadFriendsList(String email)
	{
		String filename = "";
		FriendsList friendsList = new FriendsList();
		
		if(!email.equals(""))
		{
			filename = GameConstants.PATH_FRIENDS + email + ".dat"; // filename of the character file
		}
		else
		{
			filename = GameConstants.PATH_FRIENDS_DEFAULT; // load default character file
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
						friend = line.replaceFirst("FRIEND: ", "");
						
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
			filename = GameConstants.PATH_FRIENDS + email + ".dat";
		}
		else
		{
			// save the default friends list file
			filename = GameConstants.PATH_FRIENDS_DEFAULT;
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
	
	// load a user's mail messages
	public static ArrayList<MailMessage> loadMailMessages(String username, String email)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the mail messages file
			filename = GameConstants.PATH_MESSAGES + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = GameConstants.PATH_MESSAGES_DEFAULT;
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
							sender = line.replaceFirst("SENDER: ", "");
						}
						else if(line.startsWith("DATE: "))
						{
							// get the date sent
							dateSent = line.replaceFirst("DATE: ", "");
						}
						else if(line.startsWith("BODY: "))
						{
							// get the body text
							bodyText = line.replaceFirst("BODY: ", "");
							
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
			filename = GameConstants.PATH_MESSAGES + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = GameConstants.PATH_MESSAGES_DEFAULT;
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
			filename = GameConstants.PATH_MESSAGES + email + ".dat";
		}
		else
		{
			// save the default mail messages file
			filename = GameConstants.PATH_MESSAGES_DEFAULT;
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
	
	// load the item mappings for a given shop
	public static HashMap<String,ArrayList<InventoryItem>> loadShopMappings(String shopName)
	{
		String filename = GameConstants.PATH_SHOPS + shopName + ".dat";
		
		HashMap<String, ArrayList<InventoryItem>> items = new HashMap<String, ArrayList<InventoryItem>>();
		ArrayList<InventoryItem> furniture = new ArrayList<InventoryItem>();
		ArrayList<InventoryItem> clothing = new ArrayList<InventoryItem>();
		ArrayList<InventoryItem> pins = new ArrayList<InventoryItem>();
		ArrayList<InventoryItem> posters = new ArrayList<InventoryItem>();
		ArrayList<InventoryItem> specials = new ArrayList<InventoryItem>();
		
		Scanner fileReader;
		
		String invID = "";
		int invType = 0;
		boolean invSpecial = false;
		
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
						// inventory ID
						invID = line.replaceFirst("ID: ", "");
					}
					else if(line.startsWith("TYPE: "))
					{
						// inventory type
						line = line.replaceAll("TYPE: ", "");
						if(line.equals("FURNITURE"))
						{
							invType = InventoryItem.FURNITURE;
						}
						else if(line.equals("PIN"))
						{
							invType = InventoryItem.PIN;
						}
						else if(line.equals("POSTER"))
						{
							invType = InventoryItem.POSTER;
						}
						else if(line.equals("CLOTHING"))
						{
							invType = InventoryItem.CLOTHING;
						}
					}
					else if(line.startsWith("SPECIAL: "))
					{
						// whether it's a special
						line = line.replaceAll("SPECIAL: ", "");
						if(line.equals("YES"))
						{
							invSpecial = true;
						}
						else
						{
							invSpecial = false;
						}
					}
					else if(line.startsWith("@END@"))
					{
						// ending delimeter, so create and add the item
						InventoryInfo info = StaticAppletData.getInvInfo(invID);
						InventoryItem theItem = new InventoryItem(info.getName(), invID, invType);
						
						// figure out which type of structure the item should be stored in
						if(invSpecial)
						{
							// should be a Special
							specials.add(theItem);
						}
						else
						{
							// regular item
							if(invType == InventoryItem.FURNITURE)
							{
								// furniture
								furniture.add(theItem);
							}
							else if(invType == InventoryItem.PIN)
							{
								// pin
								pins.add(theItem);
							}
							else if(invType == InventoryItem.POSTER)
							{
								// poster
								posters.add(theItem);
							}
							else if(invType == InventoryItem.CLOTHING)
							{
								// clothing
								clothing.add(theItem);
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadShopMappings() [" + shopName + "]: " + e.getClass().getName() + " - " + e.getMessage());
		}
		
		// add all the mappings to the final HashMap
		items.put("furniture", furniture);
		items.put("clothing", clothing);
		items.put("pins", pins);
		items.put("posters", posters);
		items.put("specials", specials);
		
		return items;
	}
	
	// load the pin and badge mappings
	public static HashMap<String,InventoryInfo> loadInventoryMappings()
	{
		String filename = GameConstants.PATH_MAPPING_INVENTORY;
		HashMap<String,InventoryInfo> inventoryMappings = new HashMap<String,InventoryInfo>();
		
		Scanner fileReader;
		
		String invID = "";
		String invName = "";
		String invPath = "";
		String invCardPath = "";
		String invIconPath = "";
		int invRatingIndex = 0;
		int invPrice = -1;
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
					else if(line.startsWith("BASES: ")) // preliminary total (bases)
					{
						// break the reading into an array and set the totals
						String[] items = line.replaceFirst("BASES: ", "").split("x");
						GameConstants.CONST_BASES_TOTAL = Integer.parseInt(items[0]);
						GameConstants.CONST_BASE_COLORS_TOTAL = Integer.parseInt(items[1]);
					}
					else if(line.startsWith("HAIR: ")) // preliminary total (hair)
					{
						// break the reading into an array and set the totals
						String[] items = line.replaceFirst("HAIR: ", "").split("x");
						GameConstants.CONST_HAIR_TOTAL = Integer.parseInt(items[0]);
						GameConstants.CONST_HAIR_COLORS_TOTAL = Integer.parseInt(items[1]);
					}
					else if(line.startsWith("EYES: ")) // preliminary total (eyes)
					{
						// break the reading into an array and set the totals
						String[] items = line.replaceFirst("EYES: ", "").split("x");
						GameConstants.CONST_EYES_TOTAL = Integer.parseInt(items[0]);
						GameConstants.CONST_EYE_COLORS_TOTAL = Integer.parseInt(items[1]);
					}
					else if(line.startsWith("MOUTHS: ")) // preliminary total (mouths)
					{
						// break the reading into an array and set the totals
						String[] items = line.replaceFirst("MOUTHS: ", "").split("x");
						GameConstants.CONST_MOUTHS_TOTAL = Integer.parseInt(items[0]);
						GameConstants.CONST_MOUTH_COLORS_TOTAL = Integer.parseInt(items[1]);
					}
					else if(line.startsWith("FACIALHAIR: ")) // preliminary total (facialhair)
					{
						// break the reading into an array and set the totals
						String[] items = line.replaceFirst("FACIALHAIR: ", "").split("x");
						GameConstants.CONST_FACIALHAIR_TOTAL = Integer.parseInt(items[0]);
						GameConstants.CONST_FACIALHAIR_COLORS_TOTAL = Integer.parseInt(items[1]);
					}
					else if(line.startsWith("ID: "))
					{
						// get the inventory ID
						invID = line.replaceFirst("ID: ", "");
					}
					else if(line.startsWith("NAME: "))
					{
						// get the inventory name
						invName = line.replaceFirst("NAME: ", "");
					}
					else if(line.startsWith("PATH: "))
					{
						// get the inventory path
						invPath = line.replaceFirst("PATH: ", "");
					}
					else if(line.startsWith("CARD: "))
					{
						// get the inventory card path
						invCardPath = line.replaceFirst("CARD: ", "");
					}
					else if(line.startsWith("ICON: "))
					{
						// get the inventory icon path (used in the Inventory window)
						invIconPath = line.replaceFirst("ICON: ", "");
					}
					else if(line.startsWith("TILES: "))
					{
						// get the inventory tiles
						invTiles = Integer.parseInt(line.replaceFirst("TILES: ", ""));
					}
					else if(line.startsWith("PRICE: "))
					{
						// get the inventory price
						invPrice = Integer.parseInt(line.replaceFirst("PRICE: ", ""));
					}
					else if(line.startsWith("RATING: "))
					{
						// get the content rating
						invRatingIndex = RatingSystem.getContentRatingIndex(line.replaceFirst("RATING: ", ""));
					}
					else if(line.startsWith("@END@"))
					{
						// add the inventory mapping to the HashMap
						inventoryMappings.put(invID, new InventoryInfo(invID, invName, invPath, invCardPath, invIconPath, invTiles, invPrice, invRatingIndex));
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
			filename = GameConstants.PATH_INVENTORY + email + ".dat";
		}
		else
		{
			// load the default inventory file
			filename = GameConstants.PATH_INVENTORY_DEFAULT;
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
							inventoryID = line.replaceFirst("FURNITURE: ", "");
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.FURNITURE));
						}
						else if(line.startsWith("PIN: "))
						{
							// get the pin item
							inventoryID = line.replaceFirst("PIN: ", "");
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.PIN));
						}
						else if(line.startsWith("POSTER: "))
						{
							// get the poster item
							inventoryID = line.replaceFirst("POSTER: ", "");
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.POSTER));
						}
						else if(line.startsWith("CLOTHING: "))
						{
							// get the clothing item
							inventoryID = line.replaceFirst("CLOTHING: ", "");
							inventoryItems.add(new InventoryItem(StaticAppletData.getInvInfo(inventoryID).getName(), inventoryID, InventoryItem.CLOTHING));
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
			filename = GameConstants.PATH_INVENTORY + email + ".dat";
		}
		else
		{
			// save the default inventory file
			filename = GameConstants.PATH_INVENTORY_DEFAULT;
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
				else if(r.getType() == InventoryItem.CLOTHING)
				{
					// clothing
					fileWriter.println("CLOTHING: " + r.getId());
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
	
	// append an inventory item to the player's file
	public static void appendInventory(String email, InventoryItem item)
	{
		String filename = "";
		
		if(!email.equals(""))
		{
			// save the inventory file
			filename = GameConstants.PATH_INVENTORY + email + ".dat";
		}
		else
		{
			// save the default inventory file
			filename = GameConstants.PATH_INVENTORY_DEFAULT;
		}
		
		try
		{	
			// open the file for appending
			FileOutputStream appendedFile = new FileOutputStream(filename, true);
			PrintWriter writer = new PrintWriter(appendedFile);
			
			// write the mapping to the file
			if(item.getType() == InventoryItem.FURNITURE)
			{
				writer.println("FURNITURE: " + item.getId());
			}
			else if(item.getType() == InventoryItem.PIN)
			{
				writer.println("PIN: " + item.getId());
			}
			else if(item.getType() == InventoryItem.POSTER)
			{
				writer.println("POSTER: " + item.getId());
			}
			else if(item.getType() == InventoryItem.CLOTHING)
			{
				writer.println("CLOTHING: " + item.getId());
			}
			
			// flush the stream and close it
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN appendInventory()");
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
						infoMap.put("ID", line.replaceFirst("ID: ", ""));
					}
					else if(line.startsWith("NAME: "))
					{
						// get the room name
						infoMap.put("NAME", line.replaceFirst("NAME: ", ""));
					}
					else if(line.startsWith("OWNER: "))
					{
						// get the room owner
						infoMap.put("OWNER", line.replaceFirst("OWNER: ", ""));
					}
					else if(line.startsWith("DESCRIPTION: "))
					{
						// get the room description
						infoMap.put("DESCRIPTION", line.replaceFirst("DESCRIPTION: ", ""));
					}
					else if(line.startsWith("COST: "))
					{
						// get the room cost (guest rooms only)
						infoMap.put("COST", line.replaceFirst("COST: ", ""));
					}
					else if(line.startsWith("TIMESTAMP: "))
					{
						// get the room timestamp (guest rooms only)
						infoMap.put("TIMESTAMP", line.replaceFirst("TIMESTAMP: ", ""));
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
	
	// load the room mappingse
	// TODO: HERE'S WHERE THE PROBLEM ARISES
	public static HashMap<String,VMKRoom> loadRoomMappings(boolean fromServer)
	{
		String filename = GameConstants.PATH_MAPPING_ROOMS;
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
						roomID = line.replaceFirst("ID: ", "");
						
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
							if(fromServer)
							{
								// it's an actual guest room
								VMKServerPlayerData.incrementGuestRoomCount(); // increment the number of guest rooms
							}
						}
					}
					else if(line.startsWith("PATH: "))
					{
						// get the room path
						roomPath = line.replaceFirst("PATH: ", "");
						
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
		String filename = GameConstants.PATH_MAPPING_ROOMS;
		
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
			System.out.println("ERROR IN addRoomMapping()");
			e.printStackTrace();
		}
	}
	
	// load the tile destinations for the Room Editor
	public static String[] loadEditorTileDestinations()
	{
		String filename = GameConstants.PATH_TILE_DESTINATIONS;
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
						dest = line.replaceFirst("DEST: ", "");
						
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
		String filename = GameConstants.PATH_MAPPINGS + contentRating.toLowerCase().replace("-", "") + "Dictionary.dat";
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
	
	// load the fireworks entries for a given level number
	public static ArrayList<FireworkEntry> loadFireworksEntries(int levelNum, int roundNum)
	{
		String filename = GameConstants.PATH_GAMES_FIREWORKS + "level_" + levelNum + "_" + roundNum + ".dat";
		ArrayList<FireworkEntry> entries = new ArrayList<FireworkEntry>();
		
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
					else if(line.startsWith("@")) // beginning of an entry line
					{
						line = line.replaceAll("@","");
						
						// split the entry line into its respective components
						String entryData[] = line.split(",");
						
						// parse out the components as integers
						int x = Integer.parseInt(entryData[0]);
						int y = Integer.parseInt(entryData[1]);
						int targetX = Integer.parseInt(entryData[2]);
						int targetY = Integer.parseInt(entryData[3]);
						int xSpeed = Integer.parseInt(entryData[4]);
						int ySpeed = Integer.parseInt(entryData[5]);
						int fireworkNumber = Integer.parseInt(entryData[6]);
						int delay = Integer.parseInt(entryData[7]);
						
						// add a new entry to the structure
						entries.add(new FireworkEntry(x, y, targetX, targetY, xSpeed, ySpeed, fireworkNumber, delay));
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default entries structure
				return entries;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadFireworksEntries(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new set of Fireworks entries from the file data
		return entries;
	}
	
	// return the tiles for a specific level and round in the POTC game
	public static HashMap<String, Tile> loadPiratesLevelTiles(int levelNum, int roundNum)
	{
		String filename = GameConstants.PATH_GAMES_PIRATES + "level_" + levelNum + "_" + roundNum + ".room";
		HashMap<String,Tile> tiles = new HashMap<String,Tile>();
		
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
					else if(line.startsWith("@")) // beginning of an entry line
					{
						line = line.replaceAll("@","");
						
						// split the tile line into its respective components
						String tileData[] = line.split(",");
						
						int row = Integer.parseInt(tileData[0]);
						int col = Integer.parseInt(tileData[1]);
						String tileType = tileData[2];
						String tileDest = "";
						
						// add the tile to the HashMap
						Tile newTile = new Tile(row,col,tileType,tileDest);
						newTile.setWidth(48);
						newTile.setHeight(24);
						newTile.setAbsoluteCoordinates();
						tiles.put(row + "-" + col, newTile);
					}
				}
				
				fileReader.close();
				is.close();
			}
			else
			{
				// file doesn't exist
				// return the default tiles structure
				return tiles;
			}
		}
		catch(Exception e)
		{
			System.out.println("ERROR IN loadPiratesLevelTiles(): " + e.getClass().getName() + " - " + e.getMessage());
		}

		// create a new set of Pirates level tiles from the file data
		return tiles;
	}
}
