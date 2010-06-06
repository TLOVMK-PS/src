// RatingSystem.java by Matt Fritz
// May 21, 2010
// Handles the assignment of content ratings

package util;

public class RatingSystem
{
	// array of possible ratings (String-based)
	private static String[] contentRatings = {"G","PG","PG-13","M"};
	
	public static int getContentRatingIndex(String contentRating)
	{
		int contentRatingIndex = 0;
		for(int i = 0; i < contentRatings.length; i++)
		{
			if(contentRatings[i].equals(contentRating))
			{
				contentRatingIndex = i;
			}
		}
		
		// the "what the fuck?" moment is that it defaults to G if it can't find the rating
		
		return contentRatingIndex;
	}
	
	// get the String representation of a content rating index
	public static String getContentRating(int contentRatingIndex)
	{
		return contentRatings[contentRatingIndex];
	}
	
	// return whether given content is allowed for a player based upon the content and player ratings
	public static boolean isContentAllowed(int contentRatingIndex, int playerRatingIndex)
	{
		if(contentRatingIndex <= playerRatingIndex) {return true;}
		return false;
	}
}
