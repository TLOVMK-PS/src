// MessageLogin.java by Matt Fritz
// November 20, 2009
// Handles a login message sent from the client

package sockets.messages;

import astar.AStarCharacter;
import astar.AStarCharacterBasicData;

public class MessageLogin extends MessageSecure
{
	private AStarCharacterBasicData basicData;
	private AStarCharacter character;
	
	public MessageLogin(AStarCharacterBasicData basicData)
	{
		super("MessageLogin");
		this.basicData = basicData;
	}
	
	public void setAvatarBasicData(AStarCharacterBasicData basicData) {this.basicData = basicData;}
	public AStarCharacterBasicData getAvatarBasicData() {return basicData;}
	
	public void setCharacter(AStarCharacter character) {this.character = character;}
	public AStarCharacter getCharacter() {return character;}
}
