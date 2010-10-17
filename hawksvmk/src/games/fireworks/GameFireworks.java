// GameFireworks.java by Matt Fritz
// October 14, 2010
// Handles the display of the Castle Fireworks game

package games.fireworks;

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

import javax.swing.JPanel;

import roomviewer.RoomViewerUI;

import util.AppletResourceLoader;
import util.FileOperations;

public class GameFireworks extends JPanel implements Runnable
{
	private final String GAME_ID = "fireworks";
	private final String GAME_TITLE = "Castle Fireworks";
	private final int GRAPHICS_DELAY = 40; // approx. 30 frames-per-second
	
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
	
	private BufferedImage backgroundImage = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/scene_1.jpg");
	private long gameScore = 0;
	
	// the entries for the fireworks that will eventually be displayed
	private ArrayList<FireworkEntry> fireworkEntries = new ArrayList<FireworkEntry>();
	
	// the actual fireworks that are currently displayed
	private ArrayList<Firework> fireworks = new ArrayList<Firework>();
	
	private int reticleNumber = 1; // the number of the currently-displayed reticle
	private int maxReticles = 3; // how many reticles are currently available
	private BufferedImage reticle1 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_1.png");
	private BufferedImage reticle2 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_2.png");
	private BufferedImage reticle3 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/reticle_3.png");
	private BufferedImage reticleImage = reticle1;
	
	private BufferedImage fireworkImage1 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_1.png");
	private BufferedImage fireworkImage2 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_2.png");
	private BufferedImage fireworkImage3 = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework_3.png");
	private BufferedImage flawlessStarImage = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/flawless_star.png");
	
	// the actual explosions that are displayed when a firework is burst
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private BufferedImage firework1_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework1_explo.gif");
	private BufferedImage firework1_explo[] = new BufferedImage[10]; // array of explosion images
	
	private BufferedImage firework2_e = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/firework2_explo.gif");
	private BufferedImage firework2_explo[] = new BufferedImage[10];
	
	private BufferedImage level1_reticles = AppletResourceLoader.getBufferedImageFromJar("img/games/fireworks/level1_reticles.jpg");
	private BufferedImage reticles_chooser = level1_reticles;
	
	public GameFireworks()
	{
		// allow this component to be focusable so keys can be processed
		setFocusable(true);
		
		// create the explosion image arrays for the firework explosions
		createFireworkExplosionImages(firework1_e,firework1_explo);
		createFireworkExplosionImages(firework2_e,firework2_explo);
		
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
								
								// remove the firework
								fireworks.remove(firework);
								
								// create a new firework explosion where the click occurred
								Explosion explosion = new Explosion(firework.getX(), firework.getY());
								
								System.out.println("Firework number: " + firework.getFireworkNumber());
								
								// set the explosion images
								if(firework.getFireworkNumber() == 1)
								{
									explosion.setExplosionImages(firework1_explo);
								}
								else if(firework.getFireworkNumber() == 2)
								{
									explosion.setExplosionImages(firework2_explo);
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
		
		// reset the reticle
		maxReticles = 3;
		reticleNumber = 1;
		reticleImage = reticle1;
		
		// reset the level number
		levelNum = 1;
		
		// create the fireworks
		createFireworks();
		
		gameThread = new Thread(this, GAME_TITLE);
		gameThread.start();
	}
	
	public void stop()
	{
		fireworks.clear();
		fireworkEntries.clear();
		
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
	}
	
	// create the fireworks
	public void createFireworks()
	{
		// create the firework entries
		fireworkEntries = FileOperations.loadFireworksEntries(levelNum);
		
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
			bufferGraphics.drawImage(reticles_chooser, (width / 2) - (reticles_chooser.getWidth() / 2), 512, this);
			bufferGraphics.setColor(Color.YELLOW);
			bufferGraphics.drawRect((width / 2) - (reticles_chooser.getWidth() / 2) + (60 * (reticleNumber - 1)), 512, 60, 59);
			
			// check to see if the level has been completed (the polling thread is interrupted and there are no more fireworks)
			if(isGameOver())
			{
				bufferGraphics.setColor(Color.WHITE);
				bufferGraphics.drawString("FINISHED",400,200);
			}
			
			// check to make sure the internal graphics object exists
			if(g != null)
			{
				// swap the buffer to the screen
				g.drawImage(offscreen, 0, 0, this);
			}
		}
	}
	
	// check to see if the game has ended
	private boolean isGameOver()
	{
		if(pollingThread != null)
		{
			if(pollingThread.isInterrupted() && fireworks.size() == 0)
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
			
			// add the firework to the structure
			fireworks.add(firework);
			
			// spit back the delay so the polling thread can sleep
			return en.getDelay();
		}
		else
		{
			// stop the polling thread since there are no more entries
			pollingThread.stop();
		}
		
		// return a default delay of NOTHING
		return 0;
	}
	
	public int countEntries()
	{
		return fireworkEntries.size();
	}
}

class FireworkEntryPollingThread implements Runnable
{
	private Thread pollingThread = null;
	private GameFireworks fireworksGame = null;
	
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
				// add the next firework to the game and then sleep for the returned interval
				Thread.sleep(fireworksGame.addNextFirework());
			}
			catch(Exception e) {}
		}
	}
}