// MessageGameScore.java by Matt Fritz
// October 17, 2010
// Handle sending an internal game score back and forth

package sockets.messages.games;

import games.GameScore;

import sockets.messages.MessageSecure;

public class MessageGameScore extends MessageSecure
{
	private GameScore gameScore = null; // the game score object
	
	public MessageGameScore(GameScore gameScore)
	{
		super("MessageGameScore");
		this.gameScore = gameScore;
	}
	
	public GameScore getGameScore() {
		return gameScore;
	}
	
	public void setGameScore(GameScore gameScore) {
		this.gameScore = gameScore;
	}
}
