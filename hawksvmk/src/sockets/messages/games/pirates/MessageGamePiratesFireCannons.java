// MessageGamePiratesFireCannons.java by Matt Fritz
// November 10, 2010
// Fire the cannons for a specific ship in Pirates of the Caribbean

package sockets.messages.games.pirates;

import java.io.Serializable;

import sockets.messages.Message;

public class MessageGamePiratesFireCannons extends Message implements Serializable
{
	private String username = "";
	private String direction = "";
	private String gameRoomID = "";
	
	public MessageGamePiratesFireCannons()
	{
		super("MessageGamePiratesFireCannons");
	}
	
	public MessageGamePiratesFireCannons(String username, String direction, String gameRoomID)
	{
		super("MessageGamePiratesFireCannons");
		this.username = username;
		this.direction = direction;
		this.gameRoomID = gameRoomID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getGameRoomID() {
		return gameRoomID;
	}
	
	public void setGameRoomID(String gameRoomID) {
		this.gameRoomID = gameRoomID;
	}
}
