// InternalGame.java by Matt Fritz
// October 31, 2010 (Happy Halloween!)
// Generalized class to be extended by the different internal mini-games

package games;

import javax.swing.JPanel;

import roomviewer.RoomViewerUI;

public class InternalGame extends JPanel
{
	private final String GAME_ID;
	private final String GAME_TITLE;
	
	private final int GRAPHICS_DELAY = 40; // approx. 25 frames-per-second
	
	private String roomID = ""; // ID of the current game room
	
	private final int MAX_ROUNDS_PER_LEVEL; // maximum number of rounds-per-level
	private final int MAX_LEVELS; // maximum number of levels
	private int roundNum = 1; // the number of the current round
	private int levelNum = 1; // the number of the current level
	
	private int width = 800; // width of this game's area
	private int height = 572; // height of this game's area
	
	private RoomViewerUI uiObject = null; // handle to the RoomViewerUI object
	
	// score for the game
	private long gameScore = 0;
	
	// credits won by the player
	private long creditsWon = 0;
	
	public InternalGame()
	{
		GAME_ID = "game";
		GAME_TITLE = "game";
		MAX_ROUNDS_PER_LEVEL = 0;
		MAX_LEVELS = 0;
		
		// allow this component to be focusable so keys can be processed
		setFocusable(true);
	}
	
	// initialize some constants
	public InternalGame(String GAME_ID, String GAME_TITLE, int MAX_ROUNDS_PER_LEVEL, int MAX_LEVELS)
	{
		this.GAME_ID = GAME_ID;
		this.GAME_TITLE = GAME_TITLE;
		this.MAX_ROUNDS_PER_LEVEL = MAX_ROUNDS_PER_LEVEL;
		this.MAX_LEVELS = MAX_LEVELS;
		
		// allow this component to be focusable so keys can be processed
		setFocusable(true);
	}

	public String getGameID() {
		return GAME_ID;
	}

	public String getGameTitle() {
		return GAME_TITLE;
	}

	public int getGraphicsDelay() {
		return GRAPHICS_DELAY;
	}

	public int getMaxRoundsPerLevel() {
		return MAX_ROUNDS_PER_LEVEL;
	}

	public int getMaxLevels() {
		return MAX_LEVELS;
	}

	public int getRoundNum() {
		return roundNum;
	}
	
	public void incrementRoundNum() {
		roundNum++;
	}

	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	public int getLevelNum() {
		return levelNum;
	}
	
	public void incrementLevelNum() {
		levelNum++;
	}

	public void setLevelNum(int levelNum) {
		this.levelNum = levelNum;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setUIObject(RoomViewerUI uiObject) {
		this.uiObject = uiObject;
	}
	
	public RoomViewerUI getUIObject() {
		return uiObject;
	}
	
	// set the ID of the current game room
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	// get the ID of the current game room
	public String getRoomID() {
		return roomID;
	}

	public long getCreditsWon() {
		return creditsWon;
	}

	public void setCreditsWon(long creditsWon) {
		this.creditsWon = creditsWon;
	}
	
	// set the current player's score
	public void setPlayerScore(long score)
	{
		this.gameScore = score;
	}
	
	// increment the current player's score
	public void incrementPlayerScore(long amount)
	{
		gameScore += amount;
	}
	
	// get the current player's score
	public long getPlayerScore() {
		return gameScore;
	}
}
