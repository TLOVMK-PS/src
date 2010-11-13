// SingleSound.java by Matt Fritz
// November 10, 2009
// Handle playing a sound only once

package sounds;

import javazoom.jl.player.Player;

public class SingleSound extends Thread implements SoundPlayable
{
	private String path = ""; // path to the sound file
	
	private Player player;
	private ShittyInputStream soundStream; // sound player
	
	private PlayerThread playerThread = new PlayerThread();
	private boolean playing = false;
	
	public SingleSound() {}
	
	public SingleSound(String name, String path, ShittyInputStream soundStream)
	{
		this();

		// set the name of the thread
		setName(name);

		this.path = path;
		
		// create the sound
		createSound(soundStream);
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	// create the sound
	public void createSound(ShittyInputStream soundStream)
	{
		this.soundStream = soundStream;
	}
	
	public void run()
	{
		try
		{
			// get the first ShittyInputStream and start playing
			player = new Player(soundStream);
			playerThread = new PlayerThread();
			playerThread.start();
			
			// set the playing status
			playing = true;
			
			while(playing)
			{
				// get the current position
				//int position = player.getPosition();
				
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
						// obviously, the sound might be interrupted
					}
				}
			}
			
			// stop the sound since it shouldn't replay
			stopSound();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	// play the sound
	public void playSound()
	{
		start();
	}
	
	public void stopSound()
	{
		// close the ShittyInputStream manually
		//soundStream.closeManually();
		
		// interrupt the thread
		if(!isInterrupted())
		{
			this.interrupt();
		}

		// close the player and set the "playing" attribute to false
		player.close();
		playing = false;
	}
	
	class PlayerThread extends Thread
	{
		public void run()
		{
			try
			{
				// play the sound buffer
				player.play();
			}
			catch(Exception e) {}
		}
	}
	
	// we don't need the second buffer since this is a sound that will only be played once
	public void addDualBuffer(ShittyInputStream secondSoundStream) {}
}
