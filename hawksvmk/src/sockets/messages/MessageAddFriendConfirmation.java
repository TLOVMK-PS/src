// MessageAddFriendConfirmation.java by Matt Fritz
// December 28, 2009
// Handle the accepting of a Friend Request

package sockets.messages;

import java.io.Serializable;

public class MessageAddFriendConfirmation extends Message implements Serializable
{
	private String sender = "";
	private String recipient = "";
	
	public MessageAddFriendConfirmation()
	{
		super("MessageAddFriendConfirmation");
	}
	
	public MessageAddFriendConfirmation(String sender, String recipient)
	{
		super("MessageAddFriendConfirmation");
		
		this.sender = sender;
		this.recipient = recipient;
	}
}
