// MessageGameMoveCharacter.java by Matt Fritz
// November 7, 2010
// Handles moving a specific character in a game room

package sockets.messages.games;

import sockets.messages.MessageSecure;

public class MessageGameMoveCharacter extends MessageSecure
{
	private String username;
	private String gameID = "";
	private String gameRoomID = "";
	private int destGridX = 0;
	private int destGridY = 0;
	
	public MessageGameMoveCharacter() {super("MessageGameMoveCharacter");}
	
	public MessageGameMoveCharacter(String username, String gameID, String gameRoomID, int destGridX, int destGridY)
	{
		super("MessageGameMoveCharacter");
		
		this.username = username;
		this.gameID = gameID;
		this.gameRoomID = gameRoomID;
		this.destGridX = destGridX;
		this.destGridY = destGridY;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
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
