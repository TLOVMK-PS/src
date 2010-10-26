// RepeatingSound.java by Matt Fritz
// November 10, 2009
// Handles a sound that repeats with optional delay

// TODO: Switch between two Player objects in order to get a constant sound if there is no delay

package sounds;

import javazoom.jl.player.Player;

public class RepeatingSound extends Thread implements SoundPlayable
{
	private int delay = 0; // delay in milliseconds from when the sound starts up again
	private String path = "";
	
	private Player player;
	private Player player2;
	private ShittyInputStream soundStream; // sound player stream
	
	private boolean firstStream = true;
	private boolean playing = false;
	
	public RepeatingSound() {}
	
	public RepeatingSound(String name, int delay, String path, ShittyInputStream soundStream)
	{
		this();
		
		// set the name of the thread
		setName(name);
		
		this.delay = delay;
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
			
			// set the playing status
			playing = true;
			
			while(playing)
			{
				// get the current position of the player
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
						Thread.sleep( 750 );
					}
					catch( Exception ee )
					{
						// obviously, the sound will get interrupted during sleep eventually
					}
				}
			}
			
			// loop the sound since it's supposed to repeat
			restart();
		}
		catch( Exception e )
		{
			// the stream is going to close, and that will throw an exception here during playback
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
		soundStream.closeManually();
		
		// interrupt the thread
		if(!isInterrupted())
		{
			this.interrupt();
		}

		// close the player and set the "playing" attribute to false
		player.close();
		player2.close();
		playing = false;
	}
	
	// restart the sound
	private void restart()
	{	
		if(!isInterrupted())
		{
			// check to make sure there's an actual delay specified
			if(delay > 0)
			{
				// sleep for the delay
				try
				{
					Thread.sleep(delay);
				}
				catch(Exception e)
				{
					// could get interrupted here too
				}
			}
			
			// start playing the sound again
			playing = true;
			run();
		}
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}
