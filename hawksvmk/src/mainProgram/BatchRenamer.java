// BatchRenamer.java by Matt Fritz
// August 3, 2011
// Take a batch of files and rename them to anything else

package mainProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BatchRenamer
{
	String directory = ""; // concatenated directory String from multiple sources
	
	// start accepting input and producing the images
	public void startUp()
	{
		// current directory where this program has been started
		directory = System.getProperty("user.dir");
		System.out.println("Current Directory: " + directory);
		
		System.out.println();
		
		// open up a stream and buffer it in order to capture user input
		InputStreamReader reader = new InputStreamReader(System.in);
		BufferedReader input = new BufferedReader(reader);
		
		String workingDirectory = "";
		String collectionName = "";
		String initialDirectory = "";
		
		String searchPortion = "";
		String replacePortion = "";
		
		try
		{
			// e.g. "img/clothing/shirts/
			System.out.print("Enter a working directory (e.g. \"img/clothing/shirts\"): ");
			workingDirectory = input.readLine();
			
			// e.g. "shirt_0"
			System.out.print("Enter a collection name (e.g. \"shirt_0_0\"): ");
			collectionName = input.readLine();
			
			System.out.print("Filename portion to find: ");
			searchPortion = input.readLine();
			
			System.out.print("Replace found portion with: ");
			replacePortion = input.readLine();
			
			// set the full directory from which to rename files
			initialDirectory = directory + "/" + workingDirectory + "/" + collectionName + "/";
			
			System.out.println();
			System.out.println("Batch-renaming files in " + initialDirectory + "...");
			System.out.println();
			
			// get all the files from the requested directory
			File[] files = new File(initialDirectory).listFiles();
			
			File renamedFile = null;
			for(File f : files)
			{
				// resolve the new name for the file
				renamedFile = new File(initialDirectory + f.getName().replaceFirst(searchPortion, replacePortion));
				
				// make sure it would not be the same filename
				if(!renamedFile.getName().equals(f.getName()))
				{
					// attempt to rename the file
					if(f.renameTo(renamedFile))
					{
						System.out.println("Renamed " + f.getName() + " to " + renamedFile.getName() + " successfully.");
					}
					else
					{
						System.err.println("Could not rename file [" + f.getName() + "] to " + renamedFile.getPath());
					}
				}
				else
				{
					System.out.println("File [" + f.getName() + "] does not contain the search string.");
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
		
		System.out.println("Finished renaming files.");
		
		try
		{
			// ask the user if he wants to rename more files
			System.out.println();
			System.out.print("Rename more? (y/n): ");
			String renameMore = input.readLine();
			
			if(renameMore.toLowerCase().equals("y"))
			{
				// the user wants to rename more files, so start back up
				System.out.println();
				startUp();
			}
			else
			{
				// close the input streams
				input.close();
				reader.close();
				
				// close the program
				System.exit(0);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	public static void main(String args[])
	{
		new BatchRenamer().startUp();
	}
}
