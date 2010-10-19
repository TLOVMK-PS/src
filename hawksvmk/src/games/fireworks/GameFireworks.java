// GameFireworks.java by Matt Fritz
// October 14, 2010
// Handles the display of the Castle Fireworks game

package games.fireworks;

import games.GameScore;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JPanel;

import roomviewer.RoomViewerUI;

import util.AppletResourceLoader;
import util.FileOperations;

public class GameFireworks extends JPanel implements Runnable
{
	private final String GAME_ID = "fireworks";
	private final String GAME_TITLE = "Castle Fireworks";
	private final int GRAPHICS_DELAY = 40; // approx. 25 frames-per-second
	
	private String roomID = "fireworks_0"; // ID of the current Fireworks game room
	
	private final int MAX_ROUNDS_PER_LEVEL = 3; // maximum number of rounds-per-level
	private final int MAX_LEVELS = 3; // maximum number of levels
	private int roundNum = 1; // the number of the current round
	private int levelNum = 1; // the number of the current level
	
	private Thread gameThread = null; // thread to handle the running of the game
	private FireworkEntryPollingThread pollingThread = null; // thread to handle adding fireworks to the game
	
	private int width = 800; // width of this game's area
	private int height = 572; // height of this game's area
	
	private int mouseX = -20;
	private int mouseY = -20;
	
	private Image offscreen = null; // offscreen buffer
	private Graphics bufferGraphics = null; // graphics object for the offscreen buffer
	
	private RoomViewerUI uiObject = null;
	
	private BufferedImage scene1_background = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_1.jpg");
	private BufferedImage scene2_background = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_2.jpg");
	private BufferedImage scene3_background = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_3.jpg");
	private BufferedImage scene4_background = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_4.jpg");
	private BufferedImage scene5_background = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_5.jpg");
	private BufferedImage backgroundImage = scene1_background;
	private long gameScore = 0;
	
	// the entries for the fireworks that will eventually be displayed
	private ArrayList<FireworkEntry> fireworkEntries = new ArrayList<FireworkEntry>();
	
	// the actual fireworks that are currently displayed
	private ArrayList<Firework> fireworks = new ArrayList<Firework>();
	
	// reticle images and variables
	private final int LEVEL1_RETICLES = 2; // level 1-1
	private final int LEVEL2_RETICLES = 3; // level 1-3
	private final int LEVEL3_RETICLES = 4; // level 2-3
	private final int LEVEL4_RETICLES = 5; // level 3-3
	private final int LEVEL5_RETICLES = 6; // level 4-3
	private final int LEVEL6_RETICLES = 7; // level 5-3
	private int reticleNumber = 1; // the number of the currently-displayed reticle
	private int maxReticles = LEVEL1_RETICLES; // how many reticles are currently available
	private BufferedImage reticle1 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_1.png");
	private BufferedImage reticle2 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_2.png");
	private BufferedImage reticle3 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_3.png");
	private BufferedImage reticle4 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_4.png");
	private BufferedImage reticle5 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_5.png");
	private BufferedImage reticleImage = reticle1;
	
	private BufferedImage fireworkImage1 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_1.png");
	private BufferedImage fireworkImage2 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_2.png");
	private BufferedImage fireworkImage3 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_3.png");
	private BufferedImage fireworkImage4 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_4.png");
	private BufferedImage fireworkImage5 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_5.png");
	private BufferedImage flawlessStarImage = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/flawless_star.png");
	
	// the actual explosions that are displayed when a firework is burst
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private BufferedImage firework1_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework1_explo.png");
	private BufferedImage firework1_explo[] = new BufferedImage[10]; // array of explosion images
	
	private BufferedImage firework2_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework2_explo.png");
	private BufferedImage firework2_explo[] = new BufferedImage[10];
	
	private BufferedImage firework3_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework3_explo.png");
	private BufferedImage firework3_explo[] = new BufferedImage[10];
	
	private BufferedImage firework4_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework4_explo.png");
	private BufferedImage firework4_explo[] = new BufferedImage[10];
	
	private BufferedImage firework5_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework5_explo.png");
	private BufferedImage firework5_explo[] = new BufferedImage[10];
	
	private BufferedImage level1_reticles = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/level1_reticles.jpg");
	private BufferedImage level2_reticles = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/level2_reticles.jpg");
	private BufferedImage level3_reticles = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/level3_reticles.jpg");
	private BufferedImage level4_reticles = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/level4_reticles.jpg");
	private BufferedImage reticles_chooser = level1_reticles;
	
	private boolean countdownActive = false; // whether the countdown to the next round/game end is active
	private int nextRoundCountdown = 0; // seconds until the next round
	
	// ordered structure of player's scores
	private ArrayList<GameScore> gameScores = new ArrayList<GameScore>();
	private final int MAX_SCORES_TO_DISPLAY = 20;
	
	// target scores for the rounds
	private HashMap<String,Long> targetScores = new HashMap<String,Long>();
	private long targetScore = 0;
	
	// credits won by the player
	private long creditsWon = 0;
	
	private BufferedImage gameResultsBackground = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/game_results.png");
	
	public GameFireworks()
	{
		// add the target scores
		targetScores.put("1_1",new Long(9000));
		targetScores.put("1_2",new Long(23000));
		targetScores.put("1_3",new Long(36000));
		targetScores.put("2_1",new Long(47000));
		targetScores.put("2_2",new Long(59000));
		targetScores.put("2_3",new Long(80000));
		targetScores.put("3_1",new Long(93000)); //
		targetScores.put("3_2",new Long(112000)); //
		targetScores.put("3_3",new Long(137000)); //
		targetScores.put("4_1",new Long(0));
		targetScores.put("4_2",new Long(0));
		targetScores.put("4_3",new Long(0));
		targetScores.put("5_1",new Long(0));
		targetScores.put("5_2",new Long(0));
		targetScores.put("5_3",new Long(0));
		
		// allow this component to be focusable so keys can be processed
		setFocusable(true);
		
		// create the explosion image arrays for the firework explosions
		createFireworkExplosionImages(firework1_e,firework1_explo);
		createFireworkExplosionImages(firework2_e,firework2_explo);
		createFireworkExplosionImages(firework3_e,firework3_explo);
		createFireworkExplosionImages(firework4_e,firework4_explo);
		createFireworkExplosionImages(firework5_e,firework5_explo);
		
		// add the mouse handlers
		addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent e) {}
			public void mouseMoved(MouseEvent e)
			{
				mouseX = e.getPoint().x;
				mouseY = e.getPoint().y;
			}
		});
		addMouseListener(new MouseListener()
		{
			public void mousePressed(MouseEvent e)
			{
				requestFocusInWindow();
			}
			public void mouseEntered(MouseEvent e)
			{
				requestFocusInWindow();
			}
			public void mouseReleased(MouseEvent e)
			{
				// iterate through the fireworks
				synchronized(this)
				{
					try
					{
						for(int i = 0; i < fireworks.size(); i++)
						{
							Firework firework = fireworks.get(i);
							
							// check to see if we clicked on a firework
							if(firework.getBoundingBox().contains(e.getPoint()))
							{
								// burst the firework
								firework.burstFirework();
								
								// check to see if the reticle matches the firework
								if(reticleNumber == firework.getFireworkNumber())
								{
									// add the score for the burst firework
									gameScore += firework.getScore();
								}
								else
								{
									// only add a portion of the score for the burst firework
									gameScore += (firework.getScore() * 0.1);
								}
								
								// figure out how many credits the player has possibly won (1/100th of the score)
								creditsWon = (gameScore / 100);
								
								// remove the firework
								fireworks.remove(firework);
								
								// create a new firework explosion where the click occurred
								Explosion explosion = new Explosion(firework.getX(), firework.getY());

								// set the explosion images
								if(firework.getFireworkNumber() == 1)
								{
									explosion.setExplosionImages(firework1_explo);
								}
								else if(firework.getFireworkNumber() == 2)
								{
									explosion.setExplosionImages(firework2_explo);
								}
								else if(firework.getFireworkNumber() == 3)
								{
									explosion.setExplosionImages(firework3_explo);
								}
								else if(firework.getFireworkNumber() == 4)
								{
									explosion.setExplosionImages(firework4_explo);
								}
								else if(firework.getFireworkNumber() == 5)
								{
									explosion.setExplosionImages(firework5_explo);
								}
								
								// add the explosion
								explosions.add(explosion);
								
								break;
							}
						}
					}
					catch(Exception ex) {}
				}
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		// add the key listener
		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e)
			{
				// iterate through the reticles if it's one of the arrow keys
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT)
				{
					changeReticle("left");
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT)
				{
					changeReticle("right");
				}
			}
		});
	}
	
	// create the firework explosion images for a given source image and a given array
	private void createFireworkExplosionImages(BufferedImage source, BufferedImage explosionImages[])
	{
		// create the explosion images
		int widthScaleFactor = source.getWidth() / explosionImages.length; // width increase factor
		int heightScaleFactor = source.getHeight() / explosionImages.length; // height increase factor
		for(int i = 0; i < explosionImages.length; i++)
		{
			// figure out the next width and height for the scaled image
			int width = source.getWidth() - (widthScaleFactor * (i + 1));
			int height = source.getHeight() - (heightScaleFactor * (i + 1));
			
			// create a new BufferedImage of the new width and height with alpha support
			BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			// create a Graphics instance, a scaling/rendering hint set, and then scale the original image onto
			// the newly-created scaledImage BufferedImage object
			Graphics2D graphics2D = scaledImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(source, 0, 0, width, height, null);
			
			// set the scaled image
			explosionImages[i] = scaledImage;
		}
	}
	
	public void start()
	{
		// create the offscreen buffer
		offscreen = createImage(width, height);
		
		// create the buffer graphics object
		bufferGraphics = offscreen.getGraphics();
		
		// reset the credits won
		creditsWon = 0;
		
		// reset the score
		gameScore = 0;
		
		// reset the target score
		targetScore = targetScores.get("1_1");
		
		// reset the level and round number
		roundNum = 1;
		levelNum = 1;
		
		// reset the background image
		resetLevelBackgroundImage();
		
		// create the fireworks
		createFireworks();
		
		gameThread = new Thread(this, GAME_TITLE);
		gameThread.start();
	}
	
	public void stop()
	{
		fireworks.clear();
		fireworkEntries.clear();
		
		// clear the scores structure
		gameScores.clear();
		
		gameThread.interrupt();
		gameThread = null;
		
		pollingThread.stop();
		pollingThread = null;
	}
	
	public void run()
	{
		while(gameThread != null)
		{
			// only paint if this grid is visible
			if(isVisible())
			{
				paintComponent(this.getGraphics());
			}
			
			try
			{
				Thread.sleep(GRAPHICS_DELAY);
			}
			catch(Exception e) {}
		}
	}
	
	public void update(Graphics g)
	{
		paintComponent(g);
	}
	
	// reset the possible reticles for the current level
	private void resetReticlesForLevel()
	{
		if(levelNum == 1 && roundNum == 1)
		{
			// reset the reticles
			maxReticles = LEVEL1_RETICLES;
			reticles_chooser = level1_reticles;
		}
		else if(levelNum == 1 && roundNum == 3)
		{
			// reset the reticles
			maxReticles = LEVEL2_RETICLES;
			reticles_chooser = level2_reticles;
		}
		else if(levelNum == 2 && roundNum == 3)
		{
			// reset the reticles
			maxReticles = LEVEL3_RETICLES;
			reticles_chooser = level3_reticles;
		}
		else if(levelNum == 3 && roundNum == 3)
		{
			// reset the reticles
			maxReticles = LEVEL4_RETICLES;
			reticles_chooser = level4_reticles;
		}
		
		// set the reticle to the first reticle
		reticleNumber = 1;
		reticleImage = reticle1;
	}
	
	// change the current fireworks level
	protected void changeFireworksLevel()
	{
		// increment the round number
		roundNum++;
		
		// check to see if we need to increment the level number
		if(roundNum > MAX_ROUNDS_PER_LEVEL)
		{
			roundNum = 1;
			levelNum++;
		}
		
		// check to see if the game has ended
		if(levelNum > MAX_LEVELS || gameScore < targetScore)
		{
			// end the game
			endGame();
		}
		else
		{
			// stop the polling thread
			pollingThread.stop();
			
			// clear the scores
			gameScores.clear();
			
			// reset the reticles for the current level
			resetReticlesForLevel();
			
			// reset the target score for the current level
			targetScore = targetScores.get(levelNum + "_" + roundNum);
			
			// reset the background image
			resetLevelBackgroundImage();
			
			// create the fireworks for the level
			createFireworks();
		}
	}
	
	// reset the background image for the level
	private void resetLevelBackgroundImage()
	{
		if(levelNum == 1) {backgroundImage = scene1_background;}
		if(levelNum == 2) {backgroundImage = scene2_background;}
		if(levelNum == 3) {backgroundImage = scene3_background;}
		if(levelNum == 4) {backgroundImage = scene4_background;}
		if(levelNum == 5) {backgroundImage = scene5_background;}
	}
	
	// end the game
	private void endGame()
	{
		// stop the polling thread
		pollingThread.stop();
		
		// hide the game area
		uiObject.hideGameArea(GAME_ID);
	}
	
	// change the reticle in the given direction
	private void changeReticle(String direction)
	{
		if(direction.equals("left"))
		{
			reticleNumber -= 1;
		}
		else if(direction.equals("right"))
		{
			reticleNumber += 1;
		}
		
		// check to make sure we didn't go outside of the necessary bounds and wrap around if necessary
		if(reticleNumber < 1) {reticleNumber = maxReticles;}
		if(reticleNumber > maxReticles) {reticleNumber = 1;}
		
		if(reticleNumber == 1) {reticleImage = reticle1;}
		if(reticleNumber == 2) {reticleImage = reticle2;}
		if(reticleNumber == 3) {reticleImage = reticle3;}
		if(reticleNumber == 4) {reticleImage = reticle4;}
		if(reticleNumber == 5) {reticleImage = reticle5;}
	}
	
	// create the fireworks
	public void createFireworks()
	{
		// create the firework entries
		fireworkEntries = FileOperations.loadFireworksEntries(levelNum, roundNum);
		
		// countdown no longer active
		countdownActive = false;
		
		// start the polling thread
		pollingThread = new FireworkEntryPollingThread(this);
		pollingThread.start();
	}
	
	public void paintComponent(Graphics g)
	{
		// make sure the buffer exists
		if(bufferGraphics != null)
		{
			// clear the buffered image
			bufferGraphics.clearRect(0, 0, width, height);
			
			// draw the background image onto the buffer
			bufferGraphics.drawImage(backgroundImage, 0, 0, this);
			
			// iterate through the fireworks
			for(int i = 0; i < fireworks.size(); i++)
			{
				Firework firework = fireworks.get(i);
				
				// make sure the firework is still active
				if(firework.isActive() && firework.getFireworkImage() != null)
				{
					firework.moveFirework(-1);
					
					// draw the firework image
					bufferGraphics.drawImage(firework.getFireworkImage(), firework.getX(), firework.getY(), this);
					
					// check if we're in the flawless range
					if(firework.isFlawless())
					{
						// draw a star on top of the firework
						bufferGraphics.drawImage(flawlessStarImage, firework.getX(), firework.getY(), this);
					}
				}
				else
				{
					// kill the firework since it faded out
					fireworks.remove(firework);
				}
				
				// draw a rectangle around the firework's target
				bufferGraphics.setColor(Color.RED);
				bufferGraphics.drawRect(firework.getTargetX(), firework.getTargetY(), 24, 24);
			}
			
			// iterate through the explosions
			for(int i = 0; i < explosions.size(); i++)
			{
				Explosion explosion = explosions.get(i);
				
				explosion.expand();
				
				// make sure the explosion is still active
				if(explosion.isActive())
				{
					// draw the expanding explosion
					if(explosion.getExplosionImage() != null)
					{
						bufferGraphics.drawImage(explosion.getExplosionImage(), explosion.getImageX(), explosion.getImageY(), this);
					}
				}
				else
				{
					// kill the explosion since it faded out
					explosions.remove(explosion);
				}
			}
			
			bufferGraphics.setColor(Color.WHITE);
			bufferGraphics.drawString("SCORE: " + gameScore, 20, 50);
			
			// draw the reticle on-screen
			bufferGraphics.drawImage(reticleImage, mouseX - (reticleImage.getWidth() / 2), mouseY - (reticleImage.getHeight() / 2),this);
			
			// draw the reticles selector centered at the bottom, along with a yellow rectangle on the current reticle
			bufferGraphics.drawImage(reticles_chooser, (width / 2) - (reticles_chooser.getWidth() / 2), 502, this);
			bufferGraphics.setColor(Color.YELLOW);
			bufferGraphics.drawRect((width / 2) - (reticles_chooser.getWidth() / 2) + (60 * (reticleNumber - 1)) + 5, 507, 60, 59);
			
			// check to see if the level has been completed (the polling thread is interrupted and there are no more fireworks)
			if(countdownActive)
			{
				bufferGraphics.setColor(Color.WHITE);
				bufferGraphics.drawImage(gameResultsBackground, 150, 50, this);
				
				// draw the current scores
				for(int i = 0; i < gameScores.size(); i++)
				{
					if(i > MAX_SCORES_TO_DISPLAY) // only show top scores
					{
						break;
					}
					else
					{
						// get the next score and draw the username and score
						GameScore score = gameScores.get(i);
						
						// check to see if the player achieved the target score
						if(score.getScore() >= targetScore)
						{
							bufferGraphics.setColor(Color.WHITE);
						}
						else
						{
							// the player did not achieve the target score
							bufferGraphics.setColor(Color.RED);
						}
						bufferGraphics.drawString(score.getUsername(), 225, 150 + (15 * i));
						bufferGraphics.drawString("" + score.getScore(), 475, 150 + (15 * i));
					}
				}
				
				bufferGraphics.setColor(Color.WHITE);
				
				// display the end game countdown?
				if((roundNum >= MAX_ROUNDS_PER_LEVEL && levelNum >= MAX_LEVELS) || gameScore < targetScore)
				{
					bufferGraphics.drawString("Game ends in " + nextRoundCountdown + " second(s).", 200, 520);
				}
				else
				{
					// display the next round countdown
					bufferGraphics.drawString("Next round begins in " + nextRoundCountdown + " second(s)!", 200, 520);
				}
			}
			
			// check to make sure the internal graphics object exists
			if(g != null)
			{
				// swap the buffer to the screen
				g.drawImage(offscreen, 0, 0, this);
			}
		}
	}
	
	// check to see if the round has ended
	protected boolean isRoundOver()
	{
		if(pollingThread != null)
		{
			// check to see if there are any firework entries and fireworks left
			if(fireworkEntries.size() == 0 && fireworks.size() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	
	public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}
	protected RoomViewerUI getUIObject() {return uiObject;}
	
	// add the next firework to the fireworks structure
	public int addNextFirework()
	{
		// check to make sure there are still fireworks
		if(fireworkEntries.size() > 0)
		{
			// get the next firework entry
			FireworkEntry en = fireworkEntries.remove(0);
			Firework firework = new Firework(en.getX(),en.getY(),en.getTargetX(),en.getTargetY(),en.getXSpeed(),en.getYSpeed(),en.getFireworkNumber());
			
			// check the firework number and set the appropriate image
			if(firework.getFireworkNumber() == 1)
			{
				// set the firework image
				firework.setFireworkImage(fireworkImage1);
			}
			else if(firework.getFireworkNumber() == 2)
			{
				// set the firework image
				firework.setFireworkImage(fireworkImage2);
			}
			else if(firework.getFireworkNumber() == 3)
			{
				// set the firework image
				firework.setFireworkImage(fireworkImage3);
			}
			else if(firework.getFireworkNumber() == 4)
			{
				// set the firework image
				firework.setFireworkImage(fireworkImage4);
			}
			else if(firework.getFireworkNumber() == 5)
			{
				// set the firework image
				firework.setFireworkImage(fireworkImage5);
			}
			
			// add the firework to the structure
			fireworks.add(firework);
			
			// spit back the delay so the polling thread can sleep
			return en.getDelay();
		}
		
		// return a default delay of NOTHING
		return 0;
	}
	
	public int countEntries()
	{
		return fireworkEntries.size();
	}
	
	// get the current player's score
	public long getPlayerScore() {
		return gameScore;
	}
	
	// add a score to the structure and sort them
	public void addGameScore(GameScore score)
	{
		gameScores.add(score);
		Collections.sort(gameScores);
	}
	
	// set the ID of the current Fireworks game room
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
	// get the ID of the current Fireworks game room
	public String getRoomID() {
		return roomID;
	}
	
	protected void startRoundCountdown()
	{
		countdownActive = true;
		nextRoundCountdown = 15; // next round starts in 15 seconds
	}
	
	protected void decreaseRoundCountdown()
	{
		if(nextRoundCountdown > 0)
		{
			nextRoundCountdown--;
		}
		else
		{
			countdownActive = false;
		}
	}
	
	public int getRoundCountdown()
	{
		return nextRoundCountdown;
	}
	
	public long getCreditsWon()
	{
		return creditsWon;
	}
}

class FireworkEntryPollingThread implements Runnable
{
	private Thread pollingThread = null;
	private GameFireworks fireworksGame = null;
	
	private boolean countdownActive = false;
	private boolean roundStartDelaying = true;
	private final int ROUND_COUNTDOWN_DELAY = 1000;
	
	public FireworkEntryPollingThread(GameFireworks fireworksGame)
	{
		this.fireworksGame = fireworksGame;
	}
	
	public boolean isInterrupted()
	{
		if(pollingThread != null)
		{
			return pollingThread.isInterrupted();
		}
		return true;
	}
	
	public void start()
	{
		pollingThread = new Thread(this, "Fireworks Entry Polling Thread");
		pollingThread.start();
	}
	
	public void stop()
	{
		if(pollingThread != null)
		{
			pollingThread.interrupt();
			pollingThread = null;
		}
	}
	
	public void run()
	{
		while(pollingThread != null)
		{
			try
			{
				if(!fireworksGame.isRoundOver())
				{
					// pause the polling thread before the round starts in order to give the player time to collect his bearings
					if(roundStartDelaying)
					{
						Thread.sleep(ROUND_COUNTDOWN_DELAY * 3);
						roundStartDelaying = false;
					}
					
					// add the next firework to the game and then sleep for the returned interval
					Thread.sleep(fireworksGame.addNextFirework());
				}
				else
				{
					if(!countdownActive)
					{
						// send a score message to the server
						fireworksGame.getUIObject().sendAddGameScoreMessage(new GameScore("fireworks",fireworksGame.getUIObject().getUsername(),fireworksGame.getPlayerScore()));
						
						// start the countdown for the next round
						Thread.sleep(ROUND_COUNTDOWN_DELAY * 3);
						fireworksGame.startRoundCountdown();
						countdownActive = true;
					}
					else
					{
						// sleep for the countdown delay
						Thread.sleep(ROUND_COUNTDOWN_DELAY);
					
						if(countdownActive && fireworksGame.getRoundCountdown() == 0)
						{
							// change the level
							fireworksGame.changeFireworksLevel();
							roundStartDelaying = true;
							countdownActive = false;
						}
						else
						{
							// decrease the round countdown
							fireworksGame.decreaseRoundCountdown();
						}
					}
				}
			}
			catch(Exception e) {}
		}
	}
}