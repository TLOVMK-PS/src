// WindowShop.java by Matt Fritz
// November 29, 2009
// Handles the "Shop" window

package ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.AppletResourceLoader;

public class WindowShop extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 559;
	private int height = 405;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_inactive.png");
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowShop messagesWindow;
	private Rectangle titleRectangle = new Rectangle(53, 11, 491, 38);
	private Rectangle exitRectangle = new Rectangle(529, 14, 16, 14);
	
	public WindowShop(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowInventory();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	public void update(Graphics g)
	{
		paintComponent(g);
	}
	
	private void loadWindowInventory()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);

		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(exitRectangle.contains(e.getPoint()))
				{
					// close the window
					setVisible(false);
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("X: " + e.getX() + "; Y: " + e.getY());
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e)
			{
				// check if the mouse is inside the "title bar"
				if(titleRectangle.contains(e.getPoint()))
				{
					int mouseX = e.getXOnScreen() - (getBounds().width / 2);//(e.getXOnScreen() - getBounds().x);
					int mouseY = e.getYOnScreen() - 75;
					messagesWindow.setLocation(mouseX, mouseY);
					repaint();
				}
			}
		});
		
		messagesWindow = this;
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}
}
