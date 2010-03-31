// MessageGetServerThreadsForRoom.java by Matt Fritz
// November 21, 2009
// Handle getting all the server threads (i.e. user sockets) for a specific room

package sockets.messages;

import java.io.Serializable;
import java.util.ArrayList;

import astar.AStarCharacter;

import sockets.VMKServerThread;

public class MessageGetCharactersInRoom extends Message implements Serializable
{
	private String roomID = "";
	private ArrayList<AStarCharacter> characters = new ArrayList<AStarCharacter>(); // ArrayList of server threads
	
	public MessageGetCharactersInRoom() {super("MessageGetCharactersInRoom");}
	
	public MessageGetCharactersInRoom(String roomID)
	{
		super("MessageGetCharactersInRoom");
		this.roomID = roomID;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public ArrayList<AStarCharacter> getCharacters() {
		return characters;
	}
	
	public AStarCharacter getCharacter(int index) {
		return characters.get(index);
	}

	public void setCharacters(ArrayList<AStarCharacter> characters) {
		this.characters = characters;
	}
	
	public void addCharacter(AStarCharacter character) {
		characters.add(character);
	}
}
