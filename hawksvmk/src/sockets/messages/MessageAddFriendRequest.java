// MessageAddFriendRequest.java by Matt Fritz
// December 28, 2009
// Handles sending a Friend Request from one user to another

package sockets.messages;

import java.io.Serializable;

public class MessageAddFriendRequest extends Message implements Serializable
{
	private String sender = "";
	private String recipient = "";
	
	public MessageAddFriendRequest()
	{
		super("MessageAddFriendRequest");
	}
	
	public MessageAddFriendRequest(String sender, String recipient)
	{
		super("MessageAddFriendRequest");
		
		this.sender = sender;
		this.recipient = recipient;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
