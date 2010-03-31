// SoundPlayable.java by Matt Fritz
// November 10, 2009
// Interface implemented by RepeatingSound and SingleSound

package sounds;

import java.applet.AudioClip;

public interface SoundPlayable
{
	public String getName();
	public void setName(String name);
	
	public void setPath(String path);
	public String getPath();
	
	public void createSound(AudioClip clip);
	public void playSound();
	public void stop();
}
