// MessageAddUserToRoom.java by Matt Fritz
// November 21, 2009
// Message sent to signify adding a user to a specific room at a certain location

package sockets.messages;

import astar.AStarCharacter;

public class MessageAddUserToRoom extends MessageSecure
{
	private String roomID = "gr4";
	private String roomName = "Boot Hill Shooting Gallery Guest Room";
	
	private int row = 0;
	private int col = 0;
	
	private AStarCharacter character;
	
	public MessageAddUserToRoom() {super("MessageAddUserToRoom");}
	public MessageAddUserToRoom(AStarCharacter character, String roomID, String roomName)
	{
		super("MessageAddUserToRoom");
		this.character = character;
		this.row = character.getRow();
		this.col = character.getCol();
		this.roomID = roomID;
		this.roomName = roomName;
	}
	public String getUsername() {
		return character.getUsername();
	}
	public void setUsername(String username) {
		character.setUsername(username);
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public int getRow() {
		return character.getRow();
	}
	public void setRow(int row) {
		this.row = row;
		character.setRow(row);
	}
	public int getCol() {
		return character.getCol();
	}
	public void setCol(int col) {
		this.col = col;
		character.setCol(col);
	}
	public void setCharacter(AStarCharacter character) {
		this.character = character;
	}
	public AStarCharacter getCharacter() {
		return character;
	}
}
