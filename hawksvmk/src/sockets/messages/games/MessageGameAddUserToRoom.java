// MessageGameAddUserToRoom.java by Matt Fritz
// October 17, 2010
// Handle adding a player to an internal game room

package sockets.messages.games;

import games.GameScore;

import java.io.Serializable;

import astar.AStarCharacter;

import sockets.messages.Message;

public class MessageGameAddUserToRoom extends Message implements Serializable
{
	private String roomID = ""; // BLANK STRING until it's passed back to the client from the server
	private String gameID = "";
	private AStarCharacter character = null;
	
	public MessageGameAddUserToRoom(String gameID, AStarCharacter character)
	{
		super("MessageGameAddUserToRoom");
		this.gameID = gameID;
		this.character = character;
	}

	// USED BY THE CLIENT
	public String getRoomID() {
		return roomID;
	}

	// USED BY THE SERVER
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	public String getGameID() {
		return gameID;
	}

	public AStarCharacter getCharacter() {
		return character;
	}

	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}
}
