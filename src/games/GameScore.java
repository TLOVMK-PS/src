// GameScore.java by Matt Fritz
// October 17, 2010
// Handles the name:score mapping for an internal game

package games;

import java.io.Serializable;

public class GameScore implements Serializable, Comparable<GameScore>
{
	private String game = "";
	private String username = "";
	private long score = 0;
	
	public GameScore(String game, String username, long score)
	{
		this.game = game;
		this.username = username;
		this.score = score;
	}
	
	public String getGame() {
		return game;
	}
	
	public void setGame(String game) {
		this.game = game;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}
	
	public int compareTo(GameScore g2)
	{
		// higher scores appear higher on the list
		if(score > g2.getScore()) {return -1;}
		if(score == g2.getScore()) {return 0;}
		if(score < g2.getScore()) {return 1;}
		
		return 0;
	}
}
