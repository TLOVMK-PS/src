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
		// badges
		pinMappings.put("badge_0", new PinInfo("badge_0", "VMK Staff", "img/badges/staff_badge.png"));
		pinMappings.put("badge_1", new PinInfo("badge_1", "Development Team", "img/badges/dev_team_badge_small.png"));
		pinMappings.put("badge_2", new PinInfo("badge_2", "Here From Day One", "img/badges/day_one_badge.png"));
		pinMappings.put("badge_3", new PinInfo("badge_3", "VIP Member", "img/badges/vip_badge.png"));
		pinMappings.put("badge_4", new PinInfo("badge_4", "Community Leader", "img/badges/community_leader_badge.png"));
		
		// magic pins
		pinMappings.put("magic_pin_0", new PinInfo("magic_pin_0", "Dancing Inferno Magic Pin", "img/pins/magic_pin_dancing_inferno.png"));
		
		// pins - Whiskey Set
		pinMappings.put("pin_whiskey_0", new PinInfo("pin_whiskey_0", "Jack Daniel's Whiskey", "img/pins/pin_whiskey_jack_daniels.png"));
		pinMappings.put("pin_whiskey_1", new PinInfo("pin_whiskey_1", "Jim Beam Black Whiskey", "img/pins/pin_whiskey_jim_beam_black.png"));
		pinMappings.put("pin_whiskey_2", new PinInfo("pin_whiskey_2", "Jim Beam White Whiskey", "img/pins/pin_whiskey_jim_beam_white.png"));
		pinMappings.put("pin_whiskey_3", new PinInfo("pin_whiskey_3", "Southern Comfort Liqueur", "img/pins/pin_whiskey_southern_comfort.png"));
		pinMappings.put("pin_whiskey_4", new PinInfo("pin_whiskey_4", "Johnnie Walker Black", "img/pins/pin_whiskey_johnnie_walker_black.png"));
		
		// pins - Miscellaneous
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
