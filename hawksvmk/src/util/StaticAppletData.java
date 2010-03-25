package util;

import java.net.URL;
import java.util.HashMap;

public class StaticAppletData
{
	private static String codebase = "";
	private static URL codebaseURL;
	
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
	
	// add an inventory mapping
	public static void addInvMapping(String invID, String invName, String invPath, String invCardPath)
	{
		invMappings.put(invID, new InventoryInfo(invID, invName, invPath, invCardPath));
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
			return new InventoryInfo("", "", "", "");
		}
	}
}
