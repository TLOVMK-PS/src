// RoomDateCreatedComparator.java by Matt Fritz
// April 25, 2010
// Compare two VMKRoom classes based upon the date they were created

package comparators;

import java.util.Comparator;

import rooms.VMKRoom;


public class RoomDateCreatedComparator implements Comparator<VMKRoom>
{
	// compare the rooms so the later rooms are after the earlier rooms
	public int compare(VMKRoom r1, VMKRoom r2)
	{
		if(r1.getRoomTimestamp() > r2.getRoomTimestamp()) {return 1;}
		if(r1.getRoomTimestamp() == r2.getRoomTimestamp()) {return 0;}
		if(r1.getRoomTimestamp() < r2.getRoomTimestamp()) {return -1;}
		
		return 0;
	}
}
