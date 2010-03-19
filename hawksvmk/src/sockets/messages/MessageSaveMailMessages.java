// MessageSaveMailMessages.java by Matt Fritz
// March 18, 2010
// Handle sending a friends list back to the server for saving

package sockets.messages;

import java.io.Serializable;
import java.util.ArrayList;

import util.MailMessage;

public class MessageSaveMailMessages extends Message implements Serializable
{
	private String sender = "";
	private ArrayList<MailMessage> messages = new ArrayList<MailMessage>();
	
	public MessageSaveMailMessages()
	{
		super("MessageSaveMailMessages");
	}
	
	public MessageSaveMailMessages(String sender, ArrayList<MailMessage> messages)
	{
		super("MessageSaveMailMessages");
		this.sender = sender;
		this.messages = messages;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public ArrayList<MailMessage> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<MailMessage> messages) {
		this.messages = messages;
	}
}
