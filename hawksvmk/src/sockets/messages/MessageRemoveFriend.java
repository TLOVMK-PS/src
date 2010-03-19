// MessageRemoveFriend.java by Matt Fritz
// March 18, 2010
// Handle removing a friend from both the sender's and receiver's lists

package sockets.messages;

import java.io.Serializable;

public class MessageRemoveFriend extends Message implements Serializable
{
	private String sender = "";
	private String recipient = "";
	
	public MessageRemoveFriend()
	{
		super("MessageRemoveFriend");
	}
	
	public MessageRemoveFriend(String sender, String recipient)
	{
		super("MessageRemoveFriend");
		
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
