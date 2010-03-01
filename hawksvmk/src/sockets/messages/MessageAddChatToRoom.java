// MessageAddChatToRoom.java by Matt Fritz
// November 21, 2009
// Handle adding chat from a specific user to a specific room

package sockets.messages;

import java.io.Serializable;

public class MessageAddChatToRoom extends Message implements Serializable
{
	private String username = "";
	private String roomName = "";
	private String text = "";
	
	public MessageAddChatToRoom() {super("MessageAddChatToRoom");}
	
	public MessageAddChatToRoom(String username, String roomName, String text)
	{
		this.username = username;
		this.roomName = roomName;
		this.text = text;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
