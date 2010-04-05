// WindowDesignModeItem.java by Matt Fritz
// April 4, 2010
// Handles the window that displays when you click on an item in a Guest Room while
// the user is in Design Mode

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;

public class WindowDesignModeItem extends JPanel
{
	private int x = 0;
	private int y = 0;
	
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/design_mode_item.png");
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private JLabel itemNameLabel = new JLabel();
	
	private Font textFont;
	
	private int width = 259;
	private int height = 67;
	
	private WindowDesignModeItem designModeWindow;
	private RoomViewerGrid gridObject;
	
	private Rectangle rotateRectangle = new Rectangle(49, 24, 78, 16);
	private Rectangle moveRectangle = new Rectangle(138, 23, 78, 18);
	private Rectangle takeAwayRectangle = new Rectangle(91, 46, 80, 18);
	private Rectangle titleRectangle = new Rectangle(0, 0, 238, 19);
	private Rectangle exitRectangle = new Rectangle(239, 2, 16, 15);
	
	public WindowDesignModeItem(Font textFont, int x, int y)
	{
		this.textFont = textFont;
		this.x = x;
		this.y = y;
		
		loadWindowDesignModeItem();
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
	
	private void loadWindowDesignModeItem()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		// Item name label
		itemNameLabel.setBounds(5, 0, 233, 18);
		itemNameLabel.setFont(textFont);
		itemNameLabel.setForeground(Color.white);
		itemNameLabel.setBackground(new Color(2, 43, 98));
		itemNameLabel.setHorizontalAlignment(JLabel.LEFT);
		itemNameLabel.setVerticalAlignment(JLabel.CENTER);
		add(itemNameLabel);

		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);

		this.setBounds(x,y,width,height); // set the bounds

		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();

				if(rotateRectangle.contains(e.getPoint()))
				{
					// rotate the item
					gridObject.getSelectedRoomItem().rotate();
				}
				else if(moveRectangle.contains(e.getPoint()))
				{
					// move the item
					gridObject.setDesignMoveMode(true);
				}
				else if(takeAwayRectangle.contains(e.getPoint()))
				{
					// take away the item
					
					// hide the window
					setVisible(false);
				}
				else if(exitRectangle.contains(e.getPoint()))
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
					Point p = new Point(e.getXOnScreen(), e.getYOnScreen());
					SwingUtilities.convertPointFromScreen(p, gridObject);
					int mouseX = p.x - (getBounds().width / 2); //gridObject.getBounds().x + getBounds().x;//e.getXOnScreen() - gridObject.getBounds().x - (getBounds().width / 2);
					int mouseY = p.y - (titleRectangle.height / 2); //e.getYOnScreen() - gridObject.getBounds().y - 75;
					designModeWindow.setLocation(mouseX, mouseY);
					//repaint();
				}
			}
		});

		designModeWindow = this;
	}

	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}

	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setItemName(String name)
	{
		itemNameLabel.setText(name);
	}
}
