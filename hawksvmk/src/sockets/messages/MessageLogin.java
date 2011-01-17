// MessageLogin.java by Matt Fritz
// November 20, 2009
// Handles a login message sent from the client

package sockets.messages;

import astar.AStarCharacter;

public class MessageLogin extends MessageSecure
{
	private String name = "";
	private String email = "";
	private AStarCharacter character;
	
	public MessageLogin(String name, String email)
	{
		super("MessageLogin");
		this.name = name;
		this.email = email;
	}
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
	
	public void setEmail(String email) {this.email = email;}
	public String getEmail() {return email;}
	
	public void setCharacter(AStarCharacter character) {this.character = character;}
	public AStarCharacter getCharacter() {return character;}
}
