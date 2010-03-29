// VMKRoom.java by Matt Fritz
// March 28, 2010
// Data structure that handles a given VMK room

package util;

import java.io.Serializable;
import java.util.HashMap;

public class VMKRoom implements Serializable
{
	private String roomID = "";
	private String roomName = "";
	private String roomPath = ""; // path to the room file
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
}
