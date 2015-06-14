// VMKGameRoom.java by Matt Fritz
// November 11, 2010
// General class to handle a game room in HVMK

package rooms;

public class VMKGameRoom extends VMKRoom
{
	boolean started = false; // boolean describing whether the game is already started
	
	public VMKGameRoom(String roomID, String roomName, String roomPath)
	{
		super(roomID, roomName, roomPath);
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}
