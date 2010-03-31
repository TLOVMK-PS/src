// MessageAddChatToRoom.java by Matt Fritz
// November 21, 2009
// Handle adding chat from a specific user to a specific room

package sockets.messages;

import java.io.Serializable;

public class MessageAddChatToRoom extends Message implements Serializable
{
	private String username = "";
	private String roomID = "";
	private String text = "";
	
	public MessageAddChatToRoom() {super("MessageAddChatToRoom");}
	
	public MessageAddChatToRoom(String username, String roomID, String text)
	{
		this.username = username;
		this.roomID = roomID;
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
