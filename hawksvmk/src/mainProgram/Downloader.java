// Downloader.java by Matt Fritz
// November 8, 2010
// Small program illustrating how to grab a file from a remote location and place it on the local disk

package mainProgram;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class Downloader
{
	// file paths for the remote and local files
	private String remoteFile = "http://localhost/boot_hill_sfx.mp3";
	private String localFile = "boot_hill_sfx.mp3";
	
	// input and output streams, both buffered for performance
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	
	public Downloader(String remoteFile, String localFile)
	{
		// set the remote and local files
		this.remoteFile = remoteFile;
		this.localFile = localFile;
	}
	
	// start downloading the file
	public void downloadFile()
	{
		try
		{
			// initialize the input stream for the remote file
			bis = new BufferedInputStream(new URL(remoteFile).openStream());
			
			// initialize the output stream for the local file
			bos = new BufferedOutputStream(new FileOutputStream(localFile));
			
			System.out.println("Download started...");
			
			// read bytes in from the input stream and write them back out with the output stream
			int theByte = 0;
			while((theByte = bis.read()) != -1)
			{
				// write the byte out to the output stream for the local file
				bos.write(theByte);
			}
			
			System.out.println("Download completed.");
			
			// flush and close the output stream
			bos.flush();
			bos.close();
			
			// close the input stream
			bis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		// initialize the Downloader and grab a file
		Downloader d = new Downloader("http://localhost/boot_hill_sfx.mp3", "boot_hill_sfx.mp3");
		d.downloadFile();
	}
}
