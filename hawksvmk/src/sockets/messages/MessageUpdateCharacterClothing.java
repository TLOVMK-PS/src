// MessageUpdateCharacterClothing.java by Matt Fritz
// October 5, 2010
// Update a given character's clothing in a specific room

package sockets.messages;

import astar.AStarCharacter;

public class MessageUpdateCharacterClothing extends MessageSecure
{
	private AStarCharacter character;
	private String roomID = "";
	
	public MessageUpdateCharacterClothing() {super("MessageUpdateCharacterClothing");}
	
	public MessageUpdateCharacterClothing(AStarCharacter character, String roomID)
	{
		super("MessageUpdateCharacterClothing");
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
}
