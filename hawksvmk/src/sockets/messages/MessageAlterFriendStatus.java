// MessageAlterFriendStatus.java by Matt Fritz
// March 19, 2010
// Handles altering the online status of a friend

package sockets.messages;

import java.io.Serializable;

public class MessageAlterFriendStatus extends Message implements Serializable 
{
	private String friend = "";
	private boolean online = false;
	
	public MessageAlterFriendStatus()
	{
		super("MessageAlterFriendStatus");
	}
	
	public MessageAlterFriendStatus(String friend, boolean online)
	{
		super("MessageAlterFriendStatus");
		this.friend = friend;
		this.online = online;
	}

	public String getFriend() {
		return friend;
	}

	public void setFriend(String friend) {
		this.friend = friend;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
