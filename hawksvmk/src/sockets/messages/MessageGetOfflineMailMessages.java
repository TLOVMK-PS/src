// MessageGetOfflineMailMessages.java by Matt Fritz
// March 18, 2010
// Handle sending a player's offline messages

package sockets.messages;

import java.util.ArrayList;

import util.MailMessage;

public class MessageGetOfflineMailMessages extends MessageSecure
{
	private String recipient = "";
	private ArrayList<MailMessage> messages = new ArrayList<MailMessage>();
	
	public MessageGetOfflineMailMessages()
	{
		super("MessageGetOfflineMailMessages");
	}
	
	public MessageGetOfflineMailMessages(String recipient, ArrayList<MailMessage> messages)
	{
		super("MessageGetOfflineMailMessages");
		this.recipient = recipient;
		this.messages = messages;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public ArrayList<MailMessage> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<MailMessage> messages) {
		this.messages = messages;
	}
}
