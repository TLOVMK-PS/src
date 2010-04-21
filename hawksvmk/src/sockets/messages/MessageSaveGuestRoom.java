// MessageSaveGuestRoom.java by Matt Fritz
// April 19, 2010
// Handles passing a message to save a guest room

package sockets.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import roomobject.RoomItem;

public class MessageSaveGuestRoom extends Message implements Serializable
{
	private ArrayList<RoomItem> roomItems = new ArrayList<RoomItem>();
	private HashMap<String,String> roomInfo = new HashMap<String,String>();
	
	public MessageSaveGuestRoom()
	{
		super("MessageSaveGuestRoom");
	}
	
	public MessageSaveGuestRoom(ArrayList<RoomItem> roomItems, HashMap<String,String> roomInfo)
	{
		super("MessageSaveGuestRoom");
		
		this.roomItems = roomItems;
		this.roomInfo = roomInfo;
	}
	
	public ArrayList<RoomItem> getRoomItems() {
		return roomItems;
	}
	
	public HashMap<String,String> getRoomInfo() {
		return roomInfo;
	}
}
