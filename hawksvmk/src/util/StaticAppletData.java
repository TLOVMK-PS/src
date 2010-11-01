package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StaticAppletData
{
	private static String codebase = "";
	private static URL codebaseURL;
	
	private static HashMap<String, VMKRoom> roomMappings = new HashMap<String, VMKRoom>();
	private static ArrayList<String> guestRoomTemplates = new ArrayList<String>();
	
	public static final int MAX_DISPLAYABLE_BADGES = 4; // maximum number of displayable badges
	public static final int MAX_DISPLAYABLE_PINS = 8; // maximum number of displayable pins
	private static HashMap<String,InventoryInfo> invMappings = new HashMap<String,InventoryInfo>(); // id,pin info
	
	public static void setCodeBase(String newCodebase) {codebase = newCodebase;}
	public static String getCodeBase() {return codebase;}
	
	public static void setCodeBaseURL(URL newCodebaseURL) {codebaseURL = newCodebaseURL;}
	public static URL getCodeBaseURL() {return codebaseURL;}
	
	// create the inventory mappings
	public static void createInvMappings()
	{	
		// load pin mappings from the mappings file
		invMappings = FileOperations.loadInventoryMappings();
		
		System.out.println("Created inventory mappings");
	}
	
	// create the room mappings
	public static void createRoomMappings()
	{
		// load room mappings from the mappings file
		roomMappings = FileOperations.loadRoomMappings(false);
		
		System.out.println("Created room mappings");
	}
	
	// add a guest room template
	public static void addGuestRoomTemplate(String templateID)
	{
		guestRoomTemplates.add(templateID);
	}
	
	// get the guest room templates
	public static ArrayList<String> getGuestRoomTemplates()
	{
		return guestRoomTemplates;
	}
	
	// add an inventory mapping
	public static void addInvMapping(String invID, String invName, String invPath, String invCardPath, String invIconPath, int tiles, int price, int ratingIndex)
	{
		invMappings.put(invID, new InventoryInfo(invID, invName, invPath, invCardPath, invIconPath, tiles, price, ratingIndex));
	}
	
	// remove an inventory mapping
	public static void removeInvMapping(String invID)
	{
		invMappings.remove(invID);
	}
	
	// get inventory info
	public static InventoryInfo getInvInfo(String invID)
	{
		InventoryInfo inv = invMappings.get(invID);
		
		if(inv != null)
		{
			return inv;
		}
		else
		{
			// return blank inventory info
			return new InventoryInfo("", "", "", "", "", 0, -1, 0);
		}
	}
	
	// get room mapping
	public static VMKRoom getRoomMapping(String roomID)
	{
		return roomMappings.get(roomID);
	}
	
	// add room mapping
	public static void addRoomMapping(String roomID, VMKRoom room)
	{
		roomMappings.put(roomID, room);
	}
	
	// get all room mappings that are owned by the given owner, optionally requesting only occupied rooms
	public static ArrayList<VMKRoom> getRoomMappingsForOwner(String owner, boolean occupied)
	{
		ArrayList<VMKRoom> theRooms = new ArrayList<VMKRoom>();
		
		// get the iterator for the values
		Iterator<VMKRoom> roomIt = roomMappings.values().iterator();
		
		// get the room mappings
		while(roomIt.hasNext())
		{
			VMKRoom theRoom = roomIt.next();
			
			if(theRoom.getRoomOwner().equals(owner))
			{
				// this room is owned by the requested owner
				if(occupied)
				{
					// room needs to be occupied
					if(theRoom.getCharacterNames().size() > 0)
					{
						theRooms.add(theRoom);
					}
				}
				else
				{
					// room doesn't need to be occupied
					theRooms.add(theRoom);
				}
			}
		}
		
		return theRooms;
	}
}
