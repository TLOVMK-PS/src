// MessageUpdateCharacterInRoom.java by Matt Fritz
// November 21, 2009
// Update a given character in a specific room

package sockets.messages;

import astar.AStarCharacter;

public class MessageUpdateCharacterInRoom extends MessageSecure
{
	private AStarCharacter character;
	private String roomID = "";
	
	private int row = 0;
	private int col = 0;
	
	public MessageUpdateCharacterInRoom() {super("MessageUpdateCharacterInRoom");}
	
	public MessageUpdateCharacterInRoom(AStarCharacter character, String roomID)
	{
		super("MessageUpdateCharacterInRoom");
		this.character = character;
		this.roomID = roomID;
		this.row = character.getRow();
		this.col = character.getCol();
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

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
