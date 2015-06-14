// MessageAddFriendConfirmation.java by Matt Fritz
// December 28, 2009
// Handle the accepting of a Friend Request

package sockets.messages;

public class MessageAddFriendConfirmation extends MessageSecure
{
	private String sender = "";
	private String recipient = "";
	private boolean accepted = false;
	
	public MessageAddFriendConfirmation()
	{
		super("MessageAddFriendConfirmation");
	}
	
	public MessageAddFriendConfirmation(String sender, String recipient, boolean accepted)
	{
		super("MessageAddFriendConfirmation");
		
		this.sender = sender;
		this.recipient = recipient;
		this.accepted = accepted;
	}
	
	public String getSender() {return sender;}
	public String getRecipient() {return recipient;}
	public boolean isAccepted() {return accepted;}
	
	public void setSender(String sender) {this.sender = sender;}
	public void setRecipient(String recipient) {this.recipient = recipient;}
	public void setAccepted(boolean accepted) {this.accepted = accepted;}
}
