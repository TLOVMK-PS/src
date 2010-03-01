// MessageRemoveUserFromRoom.java by Matt Fritz
// November 21, 2009
// Handles removing a specific user from a specific room

package sockets.messages;

import java.io.Serializable;

public class MessageRemoveUserFromRoom extends Message implements Serializable
{
	private String username = "";
	private String roomName = "";
	
	public MessageRemoveUserFromRoom() {super("MessageRemoveUserFromRoom");}
	
	public MessageRemoveUserFromRoom(String username, String roomName)
	{
		super("MessageRemoveUserFromRoom");
		
		this.username = username;
		this.roomName = roomName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
