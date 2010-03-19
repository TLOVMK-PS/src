// MailMessage.java by Matt Fritz
// March 18, 2010
// Handles a mail message sent to another player

package util;

import java.io.Serializable;
import java.util.Date;

public class MailMessage implements Serializable
{
	private String sender = ""; // who sent the message
	private String recipient = ""; // who received the message
	private String message = ""; // message text
	private String dateSent; // when the message was sent
	
	public MailMessage()
	{
	}
	
	public MailMessage(String sender, String recipient, String message, String dateSent)
	{
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

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}
}
