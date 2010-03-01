// Message.java by Matt Fritz
// November 20, 2009
// General Message class that all messages extend

package sockets.messages;

public class Message
{
	private String type = "Message";
	
	public Message() {}
	
	public Message(String type) {this.type = type;}
	
	public String getType() {return type;}
	public void setType(String type) {this.type = type;}
}
