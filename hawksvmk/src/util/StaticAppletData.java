package util;

import java.net.URL;
import java.util.HashMap;

public class StaticAppletData
{
	private static String codebase = "";
	private static URL codebaseURL;
	
	public static final int MAX_DISPLAYABLE_BADGES = 4; // maximum number of displayable badges
	public static final int MAX_DISPLAYABLE_PINS = 8; // maximum number of displayable pins
	private static HashMap<String,PinInfo> pinMappings = new HashMap<String,PinInfo>(); // id,pin info
	
	public static void setCodeBase(String newCodebase) {codebase = newCodebase;}
	public static String getCodeBase() {return codebase;}
	
	public static void setCodeBaseURL(URL newCodebaseURL) {codebaseURL = newCodebaseURL;}
	public static URL getCodeBaseURL() {return codebaseURL;}
	
	// create the pin mappings
	public static void createPinMappings()
	{	
		// load pin mappings from the mappings file
		pinMappings = FileOperations.loadPinMappings();
		
		System.out.println("Created badge and pin mappings");
	}
	
	// add a pin mapping
	public static void addPinMapping(String pinID, String pinName, String pinPath)
	{
		pinMappings.put(pinID, new PinInfo(pinID, pinName, pinPath));
	}
	
	// remove a pin mapping
	public static void removePinMapping(String pinID)
	{
		pinMappings.remove(pinID);
	}
	
	// get pin info
	public static PinInfo getPinInfo(String pinID)
	{
		PinInfo pin = pinMappings.get(pinID);
		
		if(pin != null)
		{
			return pin;
		}
		else
		{
			// return a blank pin
			return new PinInfo("", "", "");
		}
	}
}
