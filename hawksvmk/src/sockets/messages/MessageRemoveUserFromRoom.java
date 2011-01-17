// MessageRemoveUserFromRoom.java by Matt Fritz
// November 21, 2009
// Handles removing a specific user from a specific room

package sockets.messages;

public class MessageRemoveUserFromRoom extends MessageSecure
{
	private String username = "";
	private String roomID = "";
	
	public MessageRemoveUserFromRoom() {super("MessageRemoveUserFromRoom");}
	
	public MessageRemoveUserFromRoom(String username, String roomID)
	{
		super("MessageRemoveUserFromRoom");
		
		this.username = username;
		this.roomID = roomID;
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
}
