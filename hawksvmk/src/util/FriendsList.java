// FriendsList.java by Matt Fritz
// March 17, 2010
// Custom data structure for a friends list

package util;

import java.io.Serializable;
import java.util.ArrayList;

public class FriendsList implements Serializable
{
	private ArrayList<String> friends = new ArrayList<String>();
	
	public FriendsList() {}
	
	public ArrayList<String> getFriends()
	{
		return friends;
	}
	
	public void setFriends(ArrayList<String> friends)
	{
		this.friends = friends;
	}
	
	// remove a friend from the list
	public void remove(String friend)
	{
		for(int i = 0; i < friends.size(); i++)
		{
			if(friends.get(i).equals(friend))
			{
				friends.remove(i);
				break;
			}
		}
	}
	
	// add a friend to the list
	public void add(String friend)
	{
		// make sure it isn't already in the ArrayList
		if(!friends.contains(friend))
		{
			friends.add(friend);
		}
	}
}
