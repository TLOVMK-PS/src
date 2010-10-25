// MP3SoundTester.java by Matt Fritz
// October 25, 2010
// Class to test the functionality of the JLayer MP3 library
// Adapted from http://www.informit.com/guides/content.aspx?g=java&seqNum=290

package sounds;

import javazoom.jl.player.*;
import java.io.*;

import util.AppletResourceLoader;

public class MP3SoundTester
{
	private Player player;
	private InputStream is;
	
	private boolean playing = false;
	private PlayerThread pt = null;
	private String filename = "";
	private boolean replay = false;

	/** Creates a new instance of MP3Player */
	public MP3SoundTester( String filename, boolean replay ) 
	{
		try
		{
			// set the filename
			this.filename = filename;
			
			// set the replay boolean
			this.replay = replay;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void play()
	{
		try
		{
			// Create an InputStream to the file
			is = AppletResourceLoader.getFileFromJar(filename);
			
			player = new Player( is );
			pt = new PlayerThread();
			pt.start();
			
			// set the playing status
			playing = true;
			
			while(playing)
			{
				int position = player.getPosition();
				System.out.println( "Position: " + position );
				
				// check to see if the sound has finished playing
				if(player.isComplete())
				{
					playing = false;
				}
				
				try
				{
					Thread.sleep( 1000 );
				}
				catch( Exception ee )
				{
					ee.printStackTrace();
				}
			}

			System.out.println("Playing completed");
			
			/*try
			{
				Thread.sleep(1000);
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}*/
			
			// replay?
			if(replay)
			{
				System.out.println("Replay!");
				play();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		playing = false;
		pt.interrupt();
		player.close();
	}

	class PlayerThread extends Thread
	{
		public void run()
		{
			try
			{
				System.out.println("Playing MP3 file...");
				player.play();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	 
	 public static void main( String[] args )
	 {
		String filename = "";
		System.out.println("Currently in " + System.getProperty("user.dir"));
		System.out.print("Please enter the path to the MP3 file: ");
	  
		try
		{
			// open up a stream and buffer it in order to capture user input
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(reader);
			filename = input.readLine();
			
			System.out.print("Loop (y/n): ");
			String loop = input.readLine();
			boolean replay = false;
			
			if(loop.toLowerCase().equals("y"))
			{
				replay = true;
			}
			else
			{
				replay = false;
			}
		  
			MP3SoundTester mp3Player = new MP3SoundTester(filename, replay);
			mp3Player.play();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 }
}
