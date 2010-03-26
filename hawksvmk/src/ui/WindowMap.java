// WindowMap.java by Matt Fritz
// March 25, 2010
// Handles displaying the VMK map

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import roomviewer.RoomViewerGrid;
import util.AppletResourceLoader;

public class WindowMap extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 800;
	private int height = 600;
	
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_vmk.jpg");
	private ImageIcon exitHelpImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_exit_help.jpg");
	private ImageIcon litFrontierlandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_frontierland_lit.jpg");
	private ImageIcon litNewOrleansSquareImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_new_orleans_square_lit.jpg");
	private ImageIcon litAdventurelandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_adventureland_lit.jpg");
	private ImageIcon litMainStreetImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_main_street_lit.jpg");
	private ImageIcon litTomorrowlandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_tomorrowland_lit.jpg");
	private ImageIcon litFantasylandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_fantasyland_lit.jpg");
	private ImageIcon litGuestRoomsImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_guest_rooms_lit.jpg");
	
	private Rectangle exitRectangle = new Rectangle(1, 2, 15, 15);
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	private JLabel exitHelpLabel = new JLabel(exitHelpImage);
	private JLabel litFrontierland = new JLabel();
	private JLabel litNewOrleansSquare = new JLabel();
	private JLabel litAdventureland = new JLabel();
	private JLabel litMainStreet = new JLabel();
	private JLabel litTomorrowland = new JLabel();
	private JLabel litFantasyland = new JLabel();
	private JLabel litGuestRooms = new JLabel();
	
	WindowMap mapWindow;
	
	public WindowMap(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowMap();
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
	
	private void loadWindowMap()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		this.setLayout(null);
		
		// add the lit "Frontierland" image
		litFrontierland.setBounds(163, 141, 158, 35);
		litFrontierland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFrontierland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Frontierland" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFrontierland.setIcon(litFrontierlandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litFrontierland);
		
		// add the lit "New Orleans Square" image
		litNewOrleansSquare.setBounds(37, 216, 208, 42);
		litNewOrleansSquare.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litNewOrleansSquare.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "New Orleans Square" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litNewOrleansSquare.setIcon(litNewOrleansSquareImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litNewOrleansSquare);
		
		// add the lit "Adventureland" image
		litAdventureland.setBounds(136, 380, 206, 39);
		litAdventureland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litAdventureland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Adventureland" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litAdventureland.setIcon(litAdventurelandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litAdventureland);
		
		// add the lit "Main Street" image
		litMainStreet.setBounds(332, 488, 162, 31);
		litMainStreet.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMainStreet.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Main Street" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMainStreet.setIcon(litMainStreetImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litMainStreet);
		
		// add the lit "Tomorrowland" image
		litTomorrowland.setBounds(516, 359, 194, 36);
		litTomorrowland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litTomorrowland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Tomorrowland" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litTomorrowland.setIcon(litTomorrowlandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litTomorrowland);
		
		// add the lit "Fantasyland" image
		litFantasyland.setBounds(561, 166, 184, 37);
		litFantasyland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFantasyland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Fantasyland" tab
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFantasyland.setIcon(litFantasylandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litFantasyland);
		
		// add the lit "Guest Rooms" image
		litGuestRooms.setBounds(324, 217, 174, 34);
		litGuestRooms.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litGuestRooms.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Guest Rooms" window
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litGuestRooms.setIcon(litGuestRoomsImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		add(litGuestRooms);
		
		// add the exit and help image
		exitHelpLabel.setBounds(748, 7, 40, 21);
		exitHelpLabel.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				if(exitRectangle.contains(e.getPoint()))
				{
					// hide the map
					mapWindow.setVisible(false);
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("X: " + e.getX() + " - Y: " + e.getY());
			}
		});
		add(exitHelpLabel);
		
		// add the background image
		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		mapWindow = this;
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
}
