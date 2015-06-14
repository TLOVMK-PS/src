// RoomFurniture.java by Matt Fritz
// April 4, 2010
// Class to describe a furniture item placed in a guest room

package roomobject;

public class RoomFurniture extends RoomItem
{
	public RoomFurniture()
	{
		super();
		setType(RoomItem.FURNITURE);
	}
	
	public RoomFurniture(int x, int y)
	{
		super(x,y);
		setType(RoomItem.FURNITURE);
	}
	
	public RoomFurniture(int x, int y, int layer)
	{
		super(x,y,layer);
		setType(RoomItem.FURNITURE);
	}
	
	public RoomFurniture(int x, int y, int tiles, String id, String name, String directory, String rotation)
	{
		super(x,y,tiles,id,name,directory,rotation,RoomItem.FURNITURE);
	}
}
