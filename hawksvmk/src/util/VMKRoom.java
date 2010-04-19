// VMKRoom.java by Matt Fritz
// March 28, 2010
// Data structure that handles a given VMK room

package util;

import java.io.Serializable;
import java.util.HashMap;

public class VMKRoom implements Serializable, Comparable<VMKRoom>
{
	private String roomID = "";
	private String roomName = "";
	private String roomPath = ""; // path to the room file
	
	private String roomOwner = ""; // owner of the room
	private String roomDescription = ""; // description of the room
	private HashMap<String, String> characterNames = new HashMap<String, String>();
	
	public VMKRoom(String roomID, String roomName, String roomPath)
	{
		this.roomID = roomID;
		this.roomName = roomName;
		this.roomPath = roomPath;
	}

	public String getRoomID() {
		return roomID;
	}
	
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public String getRoomPath() {
		return roomPath;
	}
	
	public void setRoomPath(String roomPath) {
		this.roomPath = roomPath;
	}
	
	public String getRoomOwner() {
		return roomOwner;
	}
	
	public void setRoomOwner(String roomOwner) {
		this.roomOwner = roomOwner;
	}
	
	public String getRoomDescription() {
		return roomDescription;
	}
	
	public void setRoomDescription(String roomDescription)
	{
		this.roomDescription = roomDescription;
	}

	public HashMap<String, String> getCharacterNames() {
		return characterNames;
	}

	public void setCharacterNames(HashMap<String, String> characterNames) {
		this.characterNames = characterNames;
	}
	
	public void addCharacterName(String characterName)
	{
		characterNames.put(characterName, characterName);
	}
	
	public void removeCharacterName(String characterName)
	{
		characterNames.remove(characterName);
	}
	
	public String getCharacterName(String characterName)
	{
		return characterNames.get(characterName);
	}
	
	public boolean contains(String characterName)
	{
		return characterNames.containsKey(characterName);
	}
	
	// compare the rooms based upon the number of players currently in them
	public int compareTo(VMKRoom v)
	{
		if(this.getCharacterNames().size() > v.getCharacterNames().size()) {return 1;}
		if(this.getCharacterNames().size() == v.getCharacterNames().size()) {return 0;}
		if(this.getCharacterNames().size() < v.getCharacterNames().size()) {return -1;}
		
		return 0;
	}
}
