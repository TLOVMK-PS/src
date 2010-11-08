// RepeatingSound.java by Matt Fritz
// November 10, 2009
// Handles a sound that repeats with optional delay

// TODO: Switch between two Player objects in order to get a constant sound if there is no delay

package sounds;

import javazoom.jl.player.Player;

public class RepeatingSound extends Thread implements SoundPlayable
{
	private int length = 0; // length of the sound in milliseconds
	private int delay = 0; // delay in milliseconds from when the sound starts up again
	private String path = "";
	
	private PlayerThread playerThread;
	
	private Player player = null;
	private Player player2 = null;
	
	private ShittyInputStream soundStream = null; // sound player stream
	private ShittyInputStream secondSoundStream = null; // second player sound stream
	
	private boolean playing = false;
	private boolean useSecondBuffer = false; // TRUE to activate the second sound stream
	
	private final int BUFFER_SWITCH_THRESHOLD = 800; // the threshold in milliseconds when the buffer should be switched
	
	public RepeatingSound() {}
	
	public RepeatingSound(String name, int length, int delay, String path, ShittyInputStream soundStream)
	{
		this();
		
		// set the name of the thread
		setName(name);
		
		this.length = length;
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
		int position = 0; // holds the position of one of the players
		
		try
		{	
			// check to see which player we need to create
			if(!useSecondBuffer)
			{
				player = new Player(soundStream);
			}
			else
			{
				player2 = new Player(secondSoundStream);
			}
			
			// start the playing thread
			playerThread = new PlayerThread();
			playerThread.start();
			
			// set the playing status
			playing = true;
			
			while(playing)
			{
				// check to see if either of the players have completed playback, and close it if so
				if(player.isComplete())
				{
					player.close();
				}
				if(player2 != null && player2.isComplete())
				{
					player2.close();
				}
				
				// check to see which buffer we need to use
				if(!useSecondBuffer) // use the first buffer
				{
					// get the current position of the player
					position = player.getPosition();
					
					// check to see if we have hit the buffer switch threshold
					if((Math.abs(length - position) <= BUFFER_SWITCH_THRESHOLD) && secondSoundStream != null)
					{
						// restart playback to switch the buffers
						restart();
					}
					else
					{
						// still playing, so sleep for a little bit
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
				else // use the second buffer
				{
					// get the current position of the player
					position = player2.getPosition();
					
					// check to see if we have hit the buffer switch threshold for the second buffer
					if(Math.abs(length - position) <= BUFFER_SWITCH_THRESHOLD)
					{
						// restart playback to switch the buffers back to the first
						restart();
					}
					else
					{
						// still playing, so sleep for a little bit
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
		
		// check to see if the second stream was active
		if(secondSoundStream != null)
		{
			// close the second ShittyInputStream manually
			secondSoundStream.closeManually();
		}
		
		// interrupt the thread
		if(!isInterrupted())
		{
			this.interrupt();
		}

		// close the player and set the "playing" attribute to false
		if(player != null)
		{
			player.close();
		}
		
		// check to see if the second player was active
		if(player2 != null)
		{
			// close the second player
			player2.close();
		}
		
		// no longer playing the sounds
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
			
			// check to see if the buffers should be flipped
			if(secondSoundStream != null)
			{
				// we actually have a second sound stream, so flip the buffer
				useSecondBuffer = !useSecondBuffer;
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
	
	// add a second ShittyInputStream so we can make an attempt at continuous sound
	public void addDualBuffer(ShittyInputStream secondSoundStream)
	{
		this.secondSoundStream = secondSoundStream;
	}
	
	class PlayerThread extends Thread
	{
		public void run()
		{
			try
			{
				// check which buffer to use
				if(!useSecondBuffer)
				{
					// use the first buffer to play the sound		
					player.play();
				}
				else
				{
					// use the second buffer to play the sound
					player2.play();
				}
			}
			catch(Exception e) {}
		}
	}
}
