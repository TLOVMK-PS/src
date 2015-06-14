// MP3SoundTester.java by Matt Fritz
// October 25, 2010
// Class to test the functionality of the JLayer MP3 library
// Adapted from http://www.informit.com/guides/content.aspx?g=java&seqNum=290

package sounds;

import javazoom.jl.player.*;
import java.io.*;
import java.util.ArrayList;

import util.AppletResourceLoader;

public class MP3SoundTester
{
	private Player player;
	private ShittyInputStream is; // shitty input stream
	
	private boolean playing = false;
	private PlayerThread pt = null;
	private boolean replay = false;

	// create a new instance of MP3SoundTester with the specified filename, buffer size, and looping values
	public MP3SoundTester( String filename, int bufferSize, boolean replay ) 
	{
		try
		{
			// set the replay boolean
			this.replay = replay;
			
			// Create a ShittyInputStream for the file with the appropriate shitty buffer size
			is = AppletResourceLoader.getSoundFromJar(filename, bufferSize);
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
			// get the first ShittyInputStream and start playing
			player = new Player(is);
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
				else
				{
					// still playing, so sleep for a second
					try
					{
						Thread.sleep( 1000 );
					}
					catch( Exception ee )
					{
						ee.printStackTrace();
					}
				}
			}

			System.out.println("Playing completed");
			
			// replay?
			if(replay)
			{
				System.out.println("Replay!");
				play();
			}
			else
			{
				// stop
				stop();
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
		
		// close the stream manually
		is.closeManually();
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
			
			System.out.print("Buffer size (size of file in bytes): ");
			int bufferSize = Integer.parseInt(input.readLine());
			
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
		  
			MP3SoundTester mp3Player = new MP3SoundTester(filename, bufferSize, replay);
			mp3Player.play();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 }
}
