// MessageGameMoveCharacter.java by Matt Fritz
// November 7, 2010
// Handles moving a specific character in a game room

package sockets.messages.games;

import java.io.Serializable;

import sockets.messages.Message;

import astar.AStarCharacter;

public class MessageGameMoveCharacter extends Message implements Serializable
{
	private AStarCharacter character;
	private String gameID = "";
	private String gameRoomID = "";
	private int destGridX = 0;
	private int destGridY = 0;
	
	public MessageGameMoveCharacter() {super("MessageGameMoveCharacter");}
	
	public MessageGameMoveCharacter(AStarCharacter character, String gameID, String gameRoomID, int destGridX, int destGridY)
	{
		super("MessageGameMoveCharacter");
		
		this.character = character;
		this.gameID = gameID;
		this.gameRoomID = gameRoomID;
		this.destGridX = destGridX;
		this.destGridY = destGridY;
	}

	public AStarCharacter getCharacter() {
		return character;
	}
	
	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}

	public String getGameID() {
		return gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}

	public String getGameRoomID() {
		return gameRoomID;
	}

	public void setGameRoomID(String gameRoomID) {
		this.gameRoomID = gameRoomID;
	}

	public int getDestGridX() {
		return destGridX;
	}

	public void setDestGridX(int destGridX) {
		this.destGridX = destGridX;
	}

	public int getDestGridY() {
		return destGridY;
	}

	public void setDestGridY(int destGridY) {
		this.destGridY = destGridY;
	}
}
