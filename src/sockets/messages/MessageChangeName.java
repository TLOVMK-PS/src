// MessageChangeName.java by Matt Fritz
// November 20, 2009
// Change the internal name of the server thread

package sockets.messages;

public class MessageChangeName extends MessageSecure
{
	private String name = ""; // new name
	
	public MessageChangeName(String name)
	{
		super("MessageChangeName");
		this.name = name;
	}
	
	public void setName(String name) {this.name = name;}
	public String getName() {return name;}
}
