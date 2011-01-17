// MessageGetFriendsList.java by Matt Fritz
// March 17, 2010
// Handles sending a player his Friends List once he logs in

package sockets.messages;

import util.FriendsList;

public class MessageGetFriendsList extends MessageSecure
{
	private FriendsList friendsList;
	
	public MessageGetFriendsList()
	{
		super("MessageGetFriendsList");
	}
	
	public MessageGetFriendsList(FriendsList friendsList)
	{
		super("MessageGetFriendsList");
		
		this.friendsList = friendsList;
	}

	public void setFriendsList(FriendsList friendsList) {
		this.friendsList = friendsList;
	}
	
	public FriendsList getFriendsList() {
		return friendsList;
	}
}
