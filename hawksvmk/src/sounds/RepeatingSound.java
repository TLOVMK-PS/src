// RepeatingSound.java by Matt Fritz
// November 10, 2009
// Handles a sound that repeats with optional delay

// TODO: Switch between two Player objects in order to get a constant sound if there is no delay

package sounds;

import java.applet.AudioClip;

public class RepeatingSound implements Runnable, SoundPlayable
{
	private String name = ""; // name of the sound
	private int delay = 0; // delay in milliseconds from when the sound starts up again
	
	private AudioClip sound; // sound player
	
	private Thread soundThread; // thread for this sound
	
	public RepeatingSound() {}
	
	public RepeatingSound(String name, int delay, AudioClip soundFile)
	{
		this();
		
		this.name = name;
		this.delay = delay;
		
		// create the sound
		createSound(soundFile);
	}
	
	// create the sound
	public void createSound(AudioClip clip)
	{
		this.sound = clip;
	}
	
	public void run()
	{
		while(soundThread != null)
		{
			if(delay > 0) // check if there's a delay
			{
				try
				{
					// stop the sound
					sound.stop();
					
					Thread.sleep(delay); // sleep for the delay
					
					// restart the sound
					playSound();
				}
				catch(Exception e) {}
			}
		}
	}
	
	// play the sound
	public void playSound()
	{
		if(delay > 0)
		{
			if(soundThread == null)
			{
				soundThread = new Thread(this);
				soundThread.start();
			}
			sound.play();
		}
		else
		{
			sound.loop();
		}

		System.out.println("Sound played");
	}
	
	// stop the thread and the sound
	public void stop()
	{
		soundThread.interrupt();
		soundThread = null;
		
		sound.stop();
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
