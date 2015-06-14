// ContentRateable.java by Matt Fritz
// May 21, 2010
// General interface to handle content rating

package interfaces;

public interface ContentRateable
{
	public void setContentRating(String contentRating);
	public int getContentRatingIndex();
	public String getContentRatingAsString();
}
