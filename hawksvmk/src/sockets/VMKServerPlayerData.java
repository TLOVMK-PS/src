// VMKServerPlayerData.java by Matt Fritz
// November 21, 2009
// Store player position information in a static HashMap

package sockets;

import java.util.HashMap;

import util.FriendsList;
import util.VMKRoom;

import astar.AStarCharacter;

public class VMKServerPlayerData
{
	private static HashMap<String, AStarCharacter> characters = new HashMap<String, AStarCharacter>(); // character HashMap
	
	private static HashMap<String, VMKRoom> rooms = new HashMap<String, VMKRoom>(); // HashMap of VMK rooms
	private static int numGuestRooms = 0; // the current number of guest rooms
	
	private static HashMap<String, FriendsList> friendsLists = new HashMap<String, FriendsList>();
	private static HashMap<String, String> usernameToEmail = new HashMap<String, String>();
	
	private static int MAX_FIREWORKS_ROOMS = 3; // maximum number of rooms for the Fireworks game
	private static final int MAX_PLAYERS_PER_FIREWORKS_ROOM = 20; // maximum number of players in any given Fireworks room
	
	private static int MAX_PIRATES_ROOMS = 3; // maximum number of rooms for the Pirates game
	private static final int MAX_PLAYERS_PER_PIRATES_ROOM = 8; // maximum number of players in any given Pirates room
	
	// add a character to the HashMap
	public static void addCharacter(String username, AStarCharacter character)
	{
		if(character != null)
		{
			//System.out.println("Server-side PUT character: " + username + "; Row=" + character.getRow() + "; Col=" + character.getCol());
		}
		characters.put(username, character);
	}
	
	// add a character to the HashMap and a room
	public static void addCharacter(String username, AStarCharacter character, String roomID)
	{
		// clear the path so the character doesn't seem like he's walking face-first into a fucking tree
		//character.clearPath();
		
		// TODO: Set the X and Y coordinates of the character here, given the tile row
		// and tile column of the last tile in his path
		
		characters.put(username, character);
		rooms.get(roomID).addCharacterName(username);
		
		System.out.println("Characters in " + roomID + ": " + rooms.get(roomID).countCharacters());
	}
	
	// get a character from the HashMap
	public static AStarCharacter getCharacter(String username)
	{
		AStarCharacter character = characters.get(username);
		return character;
	}
	
	// TODO: Write a getCharactersInRoom(String roomID) method to return all the
	// character names in a given room and then convert them to an ArrayList of
	// AStarCharacter objects to be returned to the VMKServerThread class
	
	// remove a character from the HashMap
	public static void removeCharacter(String username)
	{
		characters.remove(username);
	}
	
	// remove a character from the HashMap and room
	public static void removeCharacter(String username, String room)
	{
		characters.remove(username);
		rooms.get(room).removeCharacterName(username);
	}
	
	// check whether a room contains a given user
	public static boolean roomContainsUser(String username, String room)
	{
		return rooms.get(room).contains(username);
	}
	
	// set the rooms
	public static void setRooms(HashMap<String,VMKRoom> theRooms)
	{
		rooms = theRooms;
	}
	
	// add a room
	public static void addRoom(String roomID, VMKRoom room)
	{
		rooms.put(roomID, room);
	}
	
	// get a room
	protected static VMKRoom getRoom(String roomID)
	{
		return rooms.get(roomID);
	}
	
	// return how many characters are in a room
	public static int countCharactersInRoom(String roomID)
	{
		return rooms.get(roomID).countCharacters();
	}
	
	// create the game room instances for the internal games
	public static void createGameRooms()
	{
		for(int i = 0; i < MAX_FIREWORKS_ROOMS; i++)
		{
			addRoom("fireworks_" + i, new VMKRoom("fireworks_" + i, "Fireworks Game " + i, ""));
		}
		
		for(int i = 0; i < MAX_PIRATES_ROOMS; i++)
		{
			addRoom("pirates_" + i, new VMKRoom("pirates_" + i, "Pirates Game " + i, ""));
		}
	}
	
	// add a player to an internal game room and return the ID of the room
	public static String addCharacterToGameRoom(String gameID, AStarCharacter character)
	{
		String gameRoomID = gameID + "_0000"; // initialize with a dummy value
		
		if(gameID.equals("fireworks")) // Fireworks game
		{
			// check to find a room that currently has less than the maximum number of players available
			for(int i = 0; i < MAX_FIREWORKS_ROOMS; i++)
			{
				// check the number of players in this room
				VMKRoom gameRoom = rooms.get(gameID + "_" + i);
				if(gameRoom.countCharacters() < MAX_PLAYERS_PER_FIREWORKS_ROOM)
				{
					// this will be the room that we put this user into
					gameRoomID = gameRoom.getRoomID();
					addCharacter(character.getUsername(), character, gameRoomID);
					return gameRoomID;
				}
			}
			
			// no suitable room found, so create one and add the player to it
			gameRoomID = "fireworks_" + MAX_FIREWORKS_ROOMS;
			addRoom(gameRoomID, new VMKRoom(gameRoomID, "Fireworks Game " + MAX_FIREWORKS_ROOMS,""));
			addCharacter(character.getUsername(), character, gameRoomID);
			
			// increment the number of Fireworks rooms available
			MAX_FIREWORKS_ROOMS++;
		}
		else if(gameID.equals("pirates")) // Pirates game
		{
			// check to find a room that currently has less than the maximum number of players available
			for(int i = 0; i < MAX_PIRATES_ROOMS; i++)
			{
				// check the number of players in this room
				VMKRoom gameRoom = rooms.get(gameID + "_" + i);
				if(gameRoom.countCharacters() < MAX_PLAYERS_PER_PIRATES_ROOM)
				{
					// this will be the room that we put this user into
					gameRoomID = gameRoom.getRoomID();
					addCharacter(character.getUsername(), character, gameRoomID);
					return gameRoomID;
				}
			}
			
			// no suitable room found, so create one and add the player to it
			gameRoomID = "pirates_" + MAX_PIRATES_ROOMS;
			addRoom(gameRoomID, new VMKRoom(gameRoomID, "Pirates Game " + MAX_PIRATES_ROOMS,""));
			addCharacter(character.getUsername(), character, gameRoomID);
			
			// increment the number of Pirates rooms available
			MAX_PIRATES_ROOMS++;
		}
		
		return gameRoomID; // return a the generated gameRoomID
	}
	
	// increment the number of guest rooms that exist
	public static void incrementGuestRoomCount() {numGuestRooms++;}
	
	public static int getGuestRoomCount() {return numGuestRooms;}
	
	// add a friends list to the HashMap
	public static void addFriendsList(String username, FriendsList friends)
	{
		friendsLists.put(username, friends);
	}
	
	// add a friend to a friends list
	public static void addFriendToList(String username, String friend)
	{
		FriendsList friends = friendsLists.get(username);
		if(friends == null)
		{
			// make sure we don't have a null friends list
			friends = new FriendsList();
		}
		
		// add the friend to the list
		friends.add(friend);
		
		// put the friends list back into the HashMap
		addFriendsList(username, friends);
	}
	
	// remove a friend from a friends list
	public static void removeFriendFromList(String username, String friend)
	{
		FriendsList friends = friendsLists.get(username);
		
		if(friends != null)
		{
			// make sure we aren't working with a null friends list
			friends.remove(friend);
			
			// put the friends list back into the HashMap
			addFriendsList(username, friends);
		}
	}
	
	// get a friends list from the HashMap
	public static FriendsList getFriendsList(String username)
	{
		FriendsList friends = friendsLists.get(username);
		return friends;
	}
	
	// remove a friends list from the HashMap
	public static void removeFriendsList(String username)
	{
		friendsLists.remove(username);
	}
	
	// check whether a friends list is in the HashMap
	public static boolean containsFriendsList(String username)
	{
		return friendsLists.containsKey(username);
	}
	
	// add a username:email mapping to the HashMap
	public static void addUsernameEmailMapping(String username, String email)
	{
		usernameToEmail.put(username, email);
	}
	
	// get an email mapping from the HashMap
	public static String getEmailFromUsername(String username)
	{
		return usernameToEmail.get(username);
	}
	
	// return whether a mapping exists in the HashMap
	public static boolean containsUsernameEmailMapping(String username)
	{
		return usernameToEmail.containsKey(username);
	}
	
	// set the username:email mappings in the HashMap
	public static void setUsernameEmailMappings(HashMap<String,String> usernameToEmailNew)
	{
		usernameToEmail = usernameToEmailNew;
	}
	
	// get the username:email mappings in the HashMap
	public static HashMap<String,String> getUsernameEmailMappings()
	{
		return usernameToEmail;
	}
}
