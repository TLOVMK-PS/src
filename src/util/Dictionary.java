// Dictionary.java by Matt Fritz
// June 18, 2010
// Handles the changing of inappropriate words based on a user's content level

package util;

import java.util.ArrayList;

import astar.AStarCharacter;

public class Dictionary
{
	private static ArrayList<String> gDictionary = new ArrayList<String>();
	private static ArrayList<String> pgDictionary = new ArrayList<String>();
	private static ArrayList<String> pg13Dictionary = new ArrayList<String>();
	
	// load the dictionaries from FileOperations
	public static void createDictionaries()
	{
		gDictionary = FileOperations.loadDictionary("G");
		pgDictionary = FileOperations.loadDictionary("PG");
		pg13Dictionary = FileOperations.loadDictionary("PG13");
	}
	
	// check the dictionary for a specified user
	public static String cleanInappropriateText(AStarCharacter character, String text)
	{
		String cleanedText = text; // the original text to be cleaned, with the punctuation
		
		// make sure we only perform the checking operation for users with a content rating index below Mature
		if(character.getContentRatingIndex() <= 2)
		{
			String rawText = text.replaceAll("\\p{Punct}+", ""); // raw text to check, minus the punctuation
			String words[] = rawText.split("\\s+"); // split the text into raw tokens
			
			for(String word : words)
			{
				// check on lower-case operations in order to catch all possible capitalization
				if(character.getContentRatingIndex() == 0)
				{
					// check each word in the dictionary
					for(String dictWord : gDictionary)
					{
						// inappropriate if: the actual word is found or the word contains other inappropriate words
						if(dictWord.equalsIgnoreCase(word) || word.toLowerCase().startsWith(dictWord) || word.toLowerCase().endsWith(dictWord))
						{
							// replace the original word in the original text
							cleanedText = cleanedText.replaceAll(word, "###");
						}
					}
				}
				else if(character.getContentRatingIndex() == 1)
				{
					// check each word in the dictionary
					for(String dictWord : pgDictionary)
					{
						// inappropriate if: the actual word is found or the word contains other inappropriate words
						if(dictWord.equalsIgnoreCase(word) || word.toLowerCase().startsWith(dictWord) || word.toLowerCase().endsWith(dictWord))
						{
							// replace the original word in the original text
							cleanedText = cleanedText.replaceAll(word, "###");
						}
					}
				}
				else if(character.getContentRatingIndex() == 2)
				{
					// check each word in the dictionary
					for(String dictWord : pg13Dictionary)
					{
						// inappropriate if: the actual word is found or the word contains other inappropriate words
						if(dictWord.equalsIgnoreCase(word) || word.toLowerCase().startsWith(dictWord) || word.toLowerCase().endsWith(dictWord))
						{
							// replace the original word in the original text
							cleanedText = cleanedText.replaceAll(word, "###");
						}
					}
				}
			}
		}
		
		return cleanedText;
	}
}
