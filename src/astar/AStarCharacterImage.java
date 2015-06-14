// AStarCharacterImage.java by Matt Fritz
// September 26, 2010
// Essentially a wrapper for a BufferedImage so it can be serialized over the wire

package astar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class AStarCharacterImage implements Serializable
{
	private transient BufferedImage theImage = null;
	
	public AStarCharacterImage() {super();}
	public AStarCharacterImage(BufferedImage theImage)
	{
		super();
		this.theImage = theImage;
	}
	
	public BufferedImage getImage() {return theImage;}
	public void setImage(BufferedImage theImage) {this.theImage = theImage;}
	
	// write the image out to the stream
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		ImageIO.write(getImage(), "png", new MemoryCacheImageOutputStream(out));
	}

	// read the image in from the stream
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		try
		{
			setImage(ImageIO.read(new MemoryCacheImageInputStream(in)));
		}
		catch(IIOException iioe) {}
	} 
}
