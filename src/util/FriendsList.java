// FriendsList.java by Matt Fritz
// March 17, 2010
// Custom data structure for a friends list

package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendsList implements Serializable
{
	private HashMap<String,String> friends = new HashMap<String,String>();
	
	public FriendsList() {}
	
	public HashMap<String,String> getFriends()
	{
		return friends;
	}
	
	public void setFriends(HashMap<String,String> friends)
	{
		this.friends = friends;
	}
	
	// remove a friend from the list
	public void remove(String friend)
	{
		// make sure the friend is in the ArrayList
		if(friends.containsKey(friend))
		{
			friends.remove(friend);
		}
	}
	
	// add a friend to the list
	public void add(String friend)
	{
		// make sure it isn't already in the ArrayList
		if(!friends.containsKey(friend))
		{
			friends.put(friend, friend);
		}
	}
	
	// check whether a friend is contained in the list
	public boolean contains(String friend)
	{
		return friends.containsKey(friend);
	}
}
