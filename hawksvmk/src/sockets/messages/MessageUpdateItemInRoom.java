// MessageUpdateItemInRoom.java by Matt Fritz
// April 4, 2010
// Update a specific item in the given room

package sockets.messages;

import roomobject.RoomItem;

public class MessageUpdateItemInRoom extends MessageSecure
{
	private String roomID = "";
	private RoomItem item;
	
	public MessageUpdateItemInRoom()
	{
		super("MessageUpdateItemInRoom");
	}
	
	public MessageUpdateItemInRoom(String roomID, RoomItem item)
	{
		super("MessageUpdateItemInRoom");
		
		this.roomID = roomID;
		this.item = item;
	}

	public String getRoomID() {
		return roomID;
	}
	
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	public RoomItem getItem() {
		return item;
	}

	public void setItem(RoomItem item) {
		this.item = item;
	}
}
