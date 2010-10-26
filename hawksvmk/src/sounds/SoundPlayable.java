// SoundPlayable.java by Matt Fritz
// November 10, 2009
// Interface implemented by RepeatingSound and SingleSound

package sounds;

public interface SoundPlayable
{
	public String getName();
	public void setName(String name);
	
	public void setPath(String path);
	public String getPath();
	
	public void createSound(ShittyInputStream soundStream);
	public void playSound();
	public void stopSound();
	
	public void interrupt();
	
	public void addDualBuffer(ShittyInputStream secondSoundStream);
}
