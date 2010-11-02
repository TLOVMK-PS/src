// MessageGetServerThreadsForRoom.java by Matt Fritz
// November 21, 2009
// Handle getting all the server threads (i.e. user sockets) for a specific room

package sockets.messages;

import java.io.Serializable;
import java.util.ArrayList;

import astar.AStarCharacter;

import sockets.VMKServerThread;

public class MessageGetCharacterInRoom extends Message implements Serializable
{
	private String roomID = "";
	private AStarCharacter character = new AStarCharacter(); // character to add
	
	public MessageGetCharacterInRoom() {super("MessageGetCharacterInRoom");}
	
	public MessageGetCharacterInRoom(String roomID)
	{
		super("MessageGetCharacterInRoom");
		this.roomID = roomID;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public AStarCharacter getCharacter() {
		return character;
	}
	
	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}
}
