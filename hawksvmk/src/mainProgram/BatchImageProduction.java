// BatchImageProduction.java by Matt Fritz
// October 4, 2010
// Take a directory full of PNG images at a certain size, copy them, and scale them to other sizes

package mainProgram;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class BatchImageProduction
{
	String directory = ""; // concatenated directory String from multiple sources
	int startingSize = 64; // size of the source image to pull from
	String directions[] = {"n","ne","e","se","s","sw","w","nw"}; // the image rotations to create
	String sizes[] = {"48","32"}; // the TILE WIDTH sizes of the new images to create
	
	// start accepting input and producing the images
	public void startUp()
	{
		// width, height, and proportion of source:final height
		int width = 0;
		int height = 0;
		double proportion = 0.0;
		BufferedImage source = null;
		
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
		try
		{
			// e.g. "img/clothing/shirts/
			System.out.print("Enter a working directory (e.g. \"img/clothing/shirts\"): ");
			workingDirectory = input.readLine();
			
			// e.g. "shirt_0"
			System.out.print("Enter a collection name (e.g. \"shirt_0\"): ");
			collectionName = input.readLine();
			
			// set the full directory from which to produce images
			initialDirectory = directory + "/" + workingDirectory + "/" + collectionName + "/";
			
			System.out.println();
			System.out.println("Batch-producing images in " + initialDirectory + "...");
			System.out.println();
		}
		catch(Exception e) {e.printStackTrace();}
		
		String sourceImageFile = "";
		
		// iterate through the specified directions for the image
		for(String direction : directions)
		{
			try
			{
				// read the image from the disk into the sourceImageFile object
				sourceImageFile = collectionName + "_" + direction + "_" + startingSize + ".png";
				source = ImageIO.read(new File(initialDirectory + sourceImageFile));
			}
			catch(Exception e) {e.printStackTrace();}

			// iterate through the specified sizes for the image
			for(String size : sizes)
			{
				System.out.println("Source: " + sourceImageFile);
				
				// figure out what the new height needs to be given the TILE WIDTH for the image
				if(Integer.parseInt(size) == 48)
				{
					height = 87;
				}
				else if(Integer.parseInt(size) == 32)
				{
					height = 58;
				}
				
				// calculate the proportion of the new height to the original height
				proportion = (double)(height) / source.getHeight();
				
				// apply the calculated proportion to figure out the width of the produced image
				width = (int)(source.getWidth() * proportion);
				
				// print out some image debugging info
				System.out.println("Source size: " + source.getWidth() + "x" + source.getHeight());
				System.out.println("Proportion: " + proportion);
				System.out.println("Produced size: " + width + "x" + height);

				// create a new BufferedImage of the new width and height with alpha support
				BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				// create a Graphics instance, a scaling/rendering hint set, and then scale the original image onto
				// the newly-created scaledImage BufferedImage object
				Graphics2D graphics2D = scaledImage.createGraphics();
				graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				graphics2D.drawImage(source, 0, 0, width, height, null);
				
				try
				{	
					// write the produced image to the disk in the same original working directory
					ImageIO.write(scaledImage, "png", new File(initialDirectory + collectionName + "_" + direction + "_" + size + ".png"));
					
					// print out some debugging info
					System.out.println("Produced " + collectionName + "_" + direction + "_" + size + ".png");
					System.out.println();
				}
				catch(Exception e) {e.printStackTrace();}
				
				// destroy the Graphics2D object
				graphics2D.dispose();
			}
		}
		
		System.out.println("Finished producing images");
		
		try
		{
			// ask the user if he wants to produce more images
			System.out.println();
			System.out.print("Produce more? (y/n): ");
			String produceMore = input.readLine();
			
			if(produceMore.toLowerCase().equals("y"))
			{
				// the user wants to produce more images, so start back up
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
		// print out the title banner
		System.out.println("===========================");
		System.out.println("HVMK BATCH IMAGE PRODUCTION");
		System.out.println("===========================");
		System.out.println();
		
		// start accepting input
		new BatchImageProduction().startUp();
	}
}
