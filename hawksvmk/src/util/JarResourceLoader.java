// JarResourceLoader.java by Matt Fritz
// November 18, 2009
// Load a resource from a JAR file or the local filesystem

package util;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import javax.swing.ImageIcon;

public class JarResourceLoader implements Serializable
{
	private boolean tryToLoadFromJar = false; // TRUE to allow loading from the JAR
	
	public JarResourceLoader() {}
	
	// get an ImageIcon from a JAR file
	public ImageIcon getImageFromJar(String path)
	{
		// make sure it starts with a forward slash
		if(!path.startsWith("/")) {path = "/" + path;}
		
		//System.out.println("Image path: " + path.toString());
		
		if(tryToLoadFromJar)
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
		{
			// loading from local file system
			//System.out.println("LOADING FROM LOCAL FILE SYSTEM...");
			return new ImageIcon(path.substring(1));
		}
	}
	
	// get an InputStream representing a file from a JAR file
	public InputStream getFileFromJar(String path)
	{
		// make sure it starts with a forward slash
		//System.out.println("In getFileFromJar()");
		
		if(!path.startsWith("/")) {path = "/" + path;}
		
		if(tryToLoadFromJar)
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
	
	// get a URL representing a sound file from a JAR file
	public URL getSoundFromJar(String path)
	{
		// make sure it starts with a forward slash
		//System.out.println("In getSoundFromJar()");
		
		if(!path.startsWith("/")) {path = "/" + path;}
		
		if(tryToLoadFromJar)
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
		{
			// local file system
			//System.out.println("Loading sound from local file system");
			
			try
			{
				return new URL("file:///" + System.getProperty("user.dir") + path);
			}
			catch(Exception ex)
			{
				//System.out.println("Problem loading sound from local file system");
				return null;
			}
		}
	}
}
