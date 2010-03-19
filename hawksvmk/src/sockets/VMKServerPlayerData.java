// VMKServerPlayerData.java by Matt Fritz
// November 21, 2009
// Store player position information in a static HashMap

package sockets;

import java.util.HashMap;

import util.FriendsList;

import astar.AStarCharacter;

public class VMKServerPlayerData
{
	private static HashMap<String, AStarCharacter> characters = new HashMap<String, AStarCharacter>(); // character HashMap
	private static HashMap<String, FriendsList> friendsLists = new HashMap<String, FriendsList>();
	private static HashMap<String, String> usernameToEmail = new HashMap<String, String>();
	
	// add a character to the HashMap
	public static void addCharacter(String username, AStarCharacter character)
	{
		if(character != null)
		{
			//System.out.println("Server-side PUT character: " + username + "; Row=" + character.getRow() + "; Col=" + character.getCol());
		}
		characters.put(username, character);
	}
	
	// get a character from the HashMap
	public static AStarCharacter getCharacter(String username)
	{
		AStarCharacter character = characters.get(username);
		return character;
	}
	
	// remove a character from the HashMap
	public static void removeCharacter(String username)
	{
		characters.remove(username);
	}
	
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
