// MessageLogout.java by Matt Fritz
// November 20, 2009
// Handle a logout request from the client

package sockets.messages;

import java.io.Serializable;

public class MessageLogout extends Message implements Serializable
{
	public MessageLogout()
	{
		super("MessageLogout");
	}
}
