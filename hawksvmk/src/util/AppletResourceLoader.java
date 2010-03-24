// JarResourceLoader.java by Matt Fritz
// November 18, 2009
// Load a resource from a JAR file or the local filesystem

package util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;

import javax.swing.ImageIcon;

public class AppletResourceLoader implements Serializable
{
	private static boolean tryToLoadFromJar = false; // TRUE to allow loading from the JAR
	
	public AppletResourceLoader() {}
	
	// get an ImageIcon from a JAR file
	public static ImageIcon getImageFromJar(String path)
	{
		// make sure it starts with a forward slash
		if(!path.startsWith("/")) {path = "/" + path;}
		
		// preserve transparency?
		//java.awt.Toolkit.getDefaultToolkit().getImage(url);
		
		//System.out.println("Image path: " + path.toString());
		
		/*if(tryToLoadFromJar)
		{
			try
			{
				// loading from JAR
				//System.out.println("LOADING FROM JAR...");
				return new ImageIcon(this.getClass().getResource(path));
			}
			catch(Exception e)
			{
				// loading from local file system
				//System.out.println("LOADING FROM LOCAL FILE SYSTEM...");
				return new ImageIcon(path.substring(1));
			}
		}
		else
		{*/
			// loading from local file system
			//System.out.println("LOADING FROM LOCAL FILE SYSTEM...");
			try
			{
				if(StaticAppletData.getCodeBase().contains("/bin"))
				{
					// Eclipse development environment
					return new ImageIcon(new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)));
				}
				else
				{
					// release environment
					try
					{
						// release environment (local machine)
						return new ImageIcon(new URL("file:///" + System.getProperty("user.dir") + path));
						//return new ImageIcon(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
					}
					catch(Exception e)
					{
						// release environment (web server)
						return new ImageIcon(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
					}
				}
			}
			catch(Exception e)
			{
				//System.out.println("User dir: " + System.getProperty("user.dir"));
				System.out.println("Invalid Image URL: " + StaticAppletData.getCodeBase() + "../" + path.substring(1));
				return null;
			}
			//return new ImageIcon(StaticAppletData.getCodeBase() + "../" + path.substring(1));
		//}
	}
	
	// get an InputStream representing a file from a JAR file
	public static InputStream getFileFromJar(String path)
	{
		// make sure it starts with a forward slash
		//System.out.println("In getFileFromJar()");
		
		if(!path.startsWith("/")) {path = "/" + path;}
		
		/*if(tryToLoadFromJar)
		{
			try
			{	
				// inside the JAR
				//System.out.println("Loading file from JAR");
				return getClass().getResource(path).openStream();
			}
			catch(Exception e)
			{
				//System.out.println("Loading file from local file system");
				// local file system
				try
				{
					return new URL("file:///" + System.getProperty("user.dir") + path).openStream();
				}
				catch(Exception ex)
				{
					//System.out.println("Problem loading from local file system");
					return null;
				}
			}
		}
		else
		{*/
			//System.out.println("Loading file from local file system");
			// local file system
			try
			{
				if(StaticAppletData.getCodeBase().contains("/bin"))
				{
					// Eclipse development environment
					//System.out.println("FILE: " + StaticAppletData.getCodeBase() + "../" + path.substring(1));
					return new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)).openStream();
				}
				else
				{
					// release environment
					//return (new URL("file:///" + System.getProperty("user.dir") + path)).openStream();
					try
					{
						// release environment (local machine)
						return (new URL("file:///" + System.getProperty("user.dir") + path)).openStream();
						//return (new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream();
					}
					catch(Exception e)
					{
						// release environment (web server)
						return (new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream();
					}
				}
			}
			catch(Exception ex)
			{
				System.out.println("Problem loading file from local file system: " + ex.getClass().getName() + " - " + ex.getMessage());
				ex.printStackTrace();
				return null;
			}
		//}
	}
	
	// get an InputStream representing a character from a JAR file
	public static InputStream getCharacterFromJar(String path)
	{
		if(!path.startsWith("/")) {path = "/" + path;}
		try
		{
			if(StaticAppletData.getCodeBase().contains("/bin"))
			{
				// Eclipse development environment (local machine)
				return new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)).openStream();
			}
			else
			{
				// release environment (local machine)
				return (new URL("file:///" + System.getProperty("user.dir") + path)).openStream();
			}
		}
		catch(Exception e)
		{
			// release environment (web server)
			try
			{
				return (new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream();
			}
			catch(Exception ex)
			{
				System.out.println("Problem loading character from local file system");
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	// get a URL representing a sound file from a JAR file
	public static AudioClip getSoundFromJar(String path)
	{
		// make sure it starts with a forward slash
		//System.out.println("In getSoundFromJar()");
		
		if(!path.startsWith("/")) {path = "/" + path;}
		
		/*if(tryToLoadFromJar)
		{
			try
			{	
				// inside the JAR
				String soundPath = getClass().getResource(path).toString(); // THIS MUST BE PRESENT TO ENSURE THE EXCEPTION IS THROWN
				//System.out.println("Loading sound from JAR: " + soundPath);
				return getClass().getResource(path);
			}
			catch(Exception e)
			{
				// local file system
				//System.out.println("Loading sound from local file system");
				
				try
				{
					return new URL("file:///" + System.getProperty("user.dir") + path);
				}
				catch(Exception ex)
				{
					System.out.println("Problem loading sound from local file system");
					return null;
				}
			}
		}
		else
		{*/
			// local file system
			//System.out.println("Loading sound from local file system");
			
			try
			{
				if(StaticAppletData.getCodeBase().contains("/bin"))
				{
					// Eclipse development environment
					return Applet.newAudioClip(new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)));
					//return new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1));
				}
				else
				{
					// release environment
					try
					{
						// release environment (local machine)
						return Applet.newAudioClip(new URL("file:///" + System.getProperty("user.dir") + path));
						//return new URL("file:///" + System.getProperty("user.dir") + path);
					}
					catch(Exception e)
					{
						// release environment (web server)
						return Applet.newAudioClip(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
						//return new URL(StaticAppletData.getCodeBase() + path.substring(1));
					}
				}
			}
			catch(Exception ex)
			{
				System.out.println("Problem loading sound from local file system");
				return null;
			}
		//}
	}
}
