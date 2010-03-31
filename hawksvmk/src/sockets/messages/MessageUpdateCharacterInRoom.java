// MessageUpdateCharacterInRoom.java by Matt Fritz
// November 21, 2009
// Update a given character in a specific room

package sockets.messages;

import java.io.Serializable;

import astar.AStarCharacter;

public class MessageUpdateCharacterInRoom extends Message implements Serializable
{
	private AStarCharacter character;
	private String roomID = "";
	
	private int row = 0;
	private int col = 0;
	
	public MessageUpdateCharacterInRoom() {super("MessageUpdateCharacterInRoom");}
	
	public MessageUpdateCharacterInRoom(AStarCharacter character, String roomID)
	{
		super("MessageUpdateCharactersInRoom");
		this.character = character;
		this.roomID = roomID;
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
