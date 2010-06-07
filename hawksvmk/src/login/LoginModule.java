// LoginModule.java by Matt Fritz
// November 17, 2009
// Handle connections to the database for login requests

package login;

import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class LoginModule
{
	private String username = "";
	private String staffType = "";
	
	public LoginModule() {}
	
	// login to the game
	public boolean login(String url, String email, String password)
	{
		URL loginURL;
		InputStream loginStream;
		Scanner loginScanner;
		
		try
		{
			// set up the URL and open up a stream to the file
			loginURL = new URL(url + "?e=" + email + "&p=" + password);
			System.out.println("Logging in...");
			loginStream = loginURL.openStream();
			
			// set up a scanner for the input stream
			loginScanner = new Scanner(loginStream);
			
			// get all the data from the connection
			String line;
			while(loginScanner.hasNextLine())
			{
				line = loginScanner.nextLine();
				
				if(line.startsWith("USERNAME: "))
				{
					// get the username from the connection
					username = line.replaceAll("USERNAME: ", "");
				}
				else if(line.startsWith("STAFF: "))
				{
					// get the staff type from the connection
					staffType = line.replaceAll("STAFF: ", "");
				}
			}
		}
		catch(UnknownHostException e)
		{
			// can't find host, so probably not connected to the Internet
			// not a problem, so return true since this will mostly only occur on the Dev box
			System.out.println("No host found for login: " + e.getMessage());
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Trouble logging in: " + e.getClass().getName() + "-" + e.getMessage());
			return false;
		}
		
		if(username.equals(""))
		{
			// no authentication, so return false
			return false;
		}
		
		return true; // positive authentication, so return true
	}
	
	// return the username
	public String getUsername() {
		return username;
	}
	
	// return the staff type
	public String getStaffType() {
		return staffType;
	}
}
