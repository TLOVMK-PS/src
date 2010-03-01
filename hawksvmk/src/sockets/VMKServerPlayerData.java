// VMKServerPlayerData.java by Matt Fritz
// November 21, 2009
// Store player position information in a static HashMap

package sockets;

import java.util.HashMap;

import astar.AStarCharacter;

public class VMKServerPlayerData
{
	private static HashMap<String, AStarCharacter> characters = new HashMap<String, AStarCharacter>(); // character HashMap
	
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
		if(character != null)
		{
			//System.out.println("Server-side GET character: " + character.getUsername() + "; Row=" + character.getRow() + "; Col=" + character.getCol());
		}
		return character;
	}
	
	// remove a character from the HashMap
	public static void removeCharacter(String username)
	{
		characters.remove(username);
	}
}
