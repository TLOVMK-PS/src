// RoomPoster.java by Matt Fritz
// April 4, 2010
// Class to describe a poster item in a guest room

package roomobject;

public class RoomPoster extends RoomItem
{
	public RoomPoster()
	{
		super();
		setType(RoomItem.POSTER);
	}
	
	public RoomPoster(int x, int y)
	{
		super(x,y);
		setType(RoomItem.POSTER);
	}
	
	public RoomPoster(int x, int y, int layer)
	{
		super(x,y,layer);
		setType(RoomItem.POSTER);
	}
	
	public RoomPoster(int x, int y, int tiles, String id, String name, String directory, String rotation)
	{
		super(x,y,tiles,id,name,directory,rotation,RoomItem.POSTER);
	}
}
