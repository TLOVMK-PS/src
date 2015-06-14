// MessageSendMailToUser.java by Matt Fritz
// December 28, 2009
// Handle sending mail messages among users

package sockets.messages;

import java.util.Date;

public class MessageSendMailToUser extends MessageSecure
{
	private String sender = ""; // who sent the message
	private String recipient = ""; // who received the message
	private String message = ""; // message text
	private Date dateSent; // when the message was sent
	
	public MessageSendMailToUser()
	{
		super("MessageSendMailToUser");
	}
	
	public MessageSendMailToUser(String sender, String recipient, String message, Date dateSent)
	{
		super("MessageSendMailToUser");
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		this.dateSent = dateSent;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateSent() {
		return dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}
}
