// MessageGameRemoveUserFromRoom.java by Matt Fritz
// October 17, 2010
// Handles removing a specific user from an internal game room

package sockets.messages.games;

import astar.AStarCharacter;

import sockets.messages.MessageSecure;

public class MessageGameRemoveUserFromRoom extends MessageSecure
{
	private String username = "";
	private AStarCharacter character = null;
	private String gameRoomID = "";
	private String destRoomID = "";
	
	public MessageGameRemoveUserFromRoom() {super("MessageGameRemoveUserFromRoom");}
	
	public MessageGameRemoveUserFromRoom(String username, AStarCharacter character, String gameRoomID, String destRoomID)
	{
		super("MessageGameRemoveUserFromRoom");
		
		this.username = username;
		this.character = character;
		this.gameRoomID = gameRoomID;
		this.destRoomID = destRoomID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AStarCharacter getCharacter() {
		return character;
	}

	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}

	public String getGameRoomID() {
		return gameRoomID;
	}

	public void setGameRoomID(String gameRoomID) {
		this.gameRoomID = gameRoomID;
	}

	public String getDestRoomID() {
		return destRoomID;
	}

	public void setDestRoomID(String destRoomID) {
		this.destRoomID = destRoomID;
	}
}
