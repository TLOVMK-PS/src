// MessageUpdateCharacterInRoom.java by Matt Fritz
// October 22, 2010
// Tell the server that we just re-connected

package sockets.messages;

import astar.AStarCharacter;

public class MessageReconnectToServer extends MessageSecure
{
	private AStarCharacter character;
	private String roomID = "";
	
	private int row = 0;
	private int col = 0;
	
	public MessageReconnectToServer() {super("MessageReconnectToServer");}
	
	public MessageReconnectToServer(AStarCharacter character, String roomID)
	{
		super("MessageReconnectToServer");
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
