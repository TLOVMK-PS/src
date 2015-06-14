// JarResourceLoader.java by Matt Fritz
// November 18, 2009
// Load a resource from a JAR file or the local filesystem

package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import sounds.ShittyInputStream;

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
						// release environment (web server)
						//System.out.println("Web server image: " + new URL(StaticAppletData.getCodeBase() + path.substring(1)));
						return new ImageIcon(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
					}
					catch(Exception e)
					{
						// release environment (local machine)
						//System.out.println("Local image: " + new URL("file:///" + System.getProperty("user.dir") + path).toString());

						return new ImageIcon(new URL("file:///" + System.getProperty("user.dir") + path));
						//return new ImageIcon(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
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
	
	// get a BufferedImage from a JAR file
	public static BufferedImage getBufferedImageFromJar(String path)
	{
		BufferedImage image = null;
		
		// make sure it starts with a forward slash
		if(!path.startsWith("/")) {path = "/" + path;}

		try
		{
			if(StaticAppletData.getCodeBase().contains("/bin"))
			{
				// Eclipse development environment
				image = ImageIO.read(new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)));
			}
			else
			{
				// release environment
				try
				{
					// release environment (web server)
					image = ImageIO.read(new URL(StaticAppletData.getCodeBase() + path.substring(1)));
				}
				catch(Exception e)
				{
					// release environment (local machine)
					image = ImageIO.read(new URL("file:///" + System.getProperty("user.dir") + path));
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Invalid BufferedImage URL: " + StaticAppletData.getCodeBase() + "../" + path.substring(1));
			return null;
		}
		
		return image;
	}
	
	// get an InputStream representing a file from a JAR file
	public static InputStream getFileFromJar(String path)
	{
		// first check to see if this is an online resource
		if(path.startsWith("http:"))
		{
			try
			{
				// try to open an online resource
				return new BufferedInputStream(new URL(path).openStream());
			}
			catch(Exception e)
			{
				return null;
			}
		}
		else
		{
			try
			{
				if(!path.startsWith("/")) {path = "/" + path;}
				
				if(StaticAppletData.getCodeBase().contains("/bin"))
				{
					// Eclipse development environment
					//System.out.println("FILE: " + StaticAppletData.getCodeBase() + "../" + path.substring(1));
					return new BufferedInputStream(new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)).openStream());
				}
				else
				{
					// release environment
					//return (new URL("file:///" + System.getProperty("user.dir") + path)).openStream();
					try
					{
						// release environment (web server)
						//System.out.println("File web server: " + new URL(StaticAppletData.getCodeBase() + path.substring(1)).toString());
						return new BufferedInputStream((new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream());
					}
					catch(Exception e)
					{
						// release environment (local machine)
						//System.out.println("File local machine: " + (new URL("file:///" + System.getProperty("user.dir") + path).toString()));
						return new BufferedInputStream((new URL("file:///" + System.getProperty("user.dir") + path)).openStream());
						//return (new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream();
					}
				}
			}
			catch(Exception ex)
			{
				System.out.println("Problem loading file from local file system: " + ex.getClass().getName() + " - " + ex.getMessage());
				//ex.printStackTrace();
				return null;
			}
		}
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
				return new BufferedInputStream(new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1)).openStream());
			}
			else
			{
				// release environment (web server)
				return new BufferedInputStream((new URL(StaticAppletData.getCodeBase() + path.substring(1))).openStream());
			}
		}
		catch(Exception e)
		{
			
			try
			{
				// release environment (local machine)
				return new BufferedInputStream((new URL("file:///" + System.getProperty("user.dir") + path)).openStream());
			}
			catch(Exception ex)
			{
				System.out.println("Problem loading character from local file system");
				//ex.printStackTrace();
				return null;
			}
		}
	}
	
	// get a ShittyInputStream representing a sound file from a JAR
	public static ShittyInputStream getSoundFromJar(String path, int bufferSize)
	{
		URL theShittyURL = null;
		
		if(path.startsWith("http:"))
		{
			try
			{
				// try to get an online sound stream
				theShittyURL = new URL(path);
				return new ShittyInputStream(theShittyURL.openStream(), -1);
			}
			catch(Exception e)
			{
				System.out.println("Could not get online sound resource: " + path);
				return null;
			}
		}
		else
		{
			if(!path.startsWith("/")) {path = "/" + path;}
			
			try
			{
				if(StaticAppletData.getCodeBase().contains("/bin"))
				{
					// Eclipse development environment (local machine)
					theShittyURL = new URL(StaticAppletData.getCodeBase() + "../" + path.substring(1));
	
					return new ShittyInputStream(theShittyURL.openStream(), bufferSize);
				}
				else
				{
					// release environment (web server)
					theShittyURL = new URL(StaticAppletData.getCodeBase() + path.substring(1));
					
					return new ShittyInputStream(theShittyURL.openStream(), bufferSize);
				}
			}
			catch(Exception e)
			{
				try
				{
					// release environment (local machine)
					theShittyURL = new URL("file:///" + System.getProperty("user.dir") + path);
	
					return new ShittyInputStream(theShittyURL.openStream(), bufferSize);
				}
				catch(Exception ex)
				{
					System.out.println("Problem loading ShittyInputStream from local file system");
					//ex.printStackTrace();
					return null;
				}
			}
		}
	}
}
