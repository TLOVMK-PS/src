// MessageMoveCharacter.java by Matt Fritz
// November 21, 2009
// Handles moving a specific character in a specific room

package sockets.messages;

import astar.AStarCharacter;

public class MessageMoveCharacter extends MessageSecure 
{
	private AStarCharacter character;
	private String roomID = "";
	private int destGridX = 0;
	private int destGridY = 0;
	
	public MessageMoveCharacter() {super("MessageMoveCharacter");}
	
	public MessageMoveCharacter(AStarCharacter character, String roomID, int destGridX, int destGridY)
	{
		super("MessageMoveCharacter");
		
		this.character = character;
		this.roomID = roomID;
		this.destGridX = destGridX;
		this.destGridY = destGridY;
	}

	public AStarCharacter getCharacter() {
		return character;
	}
	
	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
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
