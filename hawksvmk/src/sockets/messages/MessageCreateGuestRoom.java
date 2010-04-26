// MessageCreateGuestRoom.java by Matt Fritz
// April 25, 2010
// Handles passing a message to create a guest room

package sockets.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import roomobject.RoomItem;

public class MessageCreateGuestRoom extends Message implements Serializable
{
	private HashMap<String,String> roomInfo = new HashMap<String,String>();
	
	public MessageCreateGuestRoom()
	{
		super("MessageCreateGuestRoom");
	}
	
	public MessageCreateGuestRoom(HashMap<String,String> roomInfo)
	{
		super("MessageCreateGuestRoom");
		
		this.roomInfo = roomInfo;
	}
	
	public HashMap<String,String> getRoomInfo() {
		return roomInfo;
	}
	
	public void addRoomInfo(String key, String value)
	{
		roomInfo.put(key, value);
	}
}
