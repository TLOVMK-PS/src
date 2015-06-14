// HawksVirtualMagicKingdom.java by Matt Fritz
// November 17, 2009
// Driver program for the Hawk's Virtual Magic Kingdom game

package mainProgram;

import java.io.PrintStream;

import login.LoginWindow;

public class HawksVirtualMagicKingdom
{
	private static boolean logToFile = false; // TRUE to set up logging capabilities
	public static void main(String args[])
	{
		try
		{
			if(logToFile)
			{
				System.setOut(new PrintStream("logfile.txt"));
				System.setErr(new PrintStream("logfile.txt"));
			}
		}
		catch(Exception e) {}
		
		// load the login window
		LoginWindow theLoginWindow = new LoginWindow();
		theLoginWindow.loadLoginWindow();
	}
}
