// AStarCharacterBasicData.java by Matt Fritz
// August 18, 2011
// Data structure to store basic avatar data for message-passing

package astar;

import java.io.Serializable;

public class AStarCharacterBasicData implements Serializable
{
	private String username;
	private String email;
	private String gender;
	private String contentRating;
	private long credits;
	
	public AStarCharacterBasicData(String username, String email, String gender, String contentRating, long credits)
	{
		this.username = username;
		this.email = email;
		this.gender = gender;
		this.contentRating = contentRating;
		this.credits = credits;
	}
	
	// setter methods
	public void setUsername(String username) {this.username = username;}
	public void setEmail(String email) {this.email = email;}
	public void setGender(String gender) {this.gender = gender;}
	public void setContentRating(String contentRating) {this.contentRating = contentRating;}
	public void setCredits(long credits) {this.credits = credits;}
	
	// getter methods
	public String getUsername() {return username;}
	public String getEmail() {return email;}
	public String getGender() {return gender;}
	public String getContentRating() {return contentRating;}
	public long getCredits() {return credits;}
}
