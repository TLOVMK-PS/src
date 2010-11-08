// ShittyInputStream.java by Matt Fritz
// October 25, 2010
// A sad excuse for an InputStream, but at least the thing fucking works when applied to audio playback with JLayer

package sounds;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShittyInputStream extends BufferedInputStream
{
	private final InputStream boundStream; // the fucking underlying input stream
	private int length = 0;
	
	// create a new ShittyInputStream with a bound stream and a length of the file
	public ShittyInputStream(InputStream bound, int length)
	{
		super(bound);
		
		this.boundStream = bound; // assign the bound stream
		this.length = length; // assign the buffer length
		
		// make sure we actually have a stream marker
		if(length > 0)
		{
			this.boundStream.mark(length); // mark the location of the stream at the beginning and set the invalidation size to be the size of the file
		}
	}

	// read from the bound stream
	@Override
	public int read() throws IOException
	{
		return boundStream.read();
	}

	// say "Fuck no" when something (i.e. JLayer) tries to close the stream programmatically
	// this allows us to re-use the stream multiple times without having to re-create it from a file
	@Override
	public void close()
	{
		// only try to prevent closing if there was a specified byte marker
		if(length > 0)
		{
			try
			{
				// attempt to reset the bound stream to the beginning so playback can occur again
				boundStream.reset(); // will throw an exception here when closeManually() is used if the player is running, but who the hell cares?
			}
			catch(Exception e)
			{
				// No?  Well, something's fucked, but that's about par for the course.
			}
		}
	}

	// close this shitty input stream manually and completely
	public void closeManually()
	{
		try
		{
			// close the bound stream and this stream
			boundStream.close();
			super.close();
		}
		catch(IOException e)
		{
			// Something's still fucked. Birdie.
		}
	}
	
	// get the buffer size
	public int getBufferSize()
	{
		return length;
	}
}
