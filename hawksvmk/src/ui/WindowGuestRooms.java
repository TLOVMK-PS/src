// WindowHelp.java by Matt Fritz
// November 29, 2009
// Handles the "Help" window

package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.StaticAppletData;
import util.VMKRoom;

public class WindowGuestRooms extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 470;
	private int height = 486;
	private ImageIcon ownRoomsImage = AppletResourceLoader.getImageFromJar("img/ui/guest_rooms_own.png");
	private ImageIcon friendsRoomsImage = AppletResourceLoader.getImageFromJar("img/ui/guest_rooms_friends.png");
	private ImageIcon popularRoomsImage = AppletResourceLoader.getImageFromJar("img/ui/guest_rooms_popular.png");
	private ImageIcon enterRoomLitImage = AppletResourceLoader.getImageFromJar("img/ui/enter_room_lit.png");
	
	private JLabel backgroundLabel = new JLabel(popularRoomsImage);
	
	private WindowGuestRooms guestRoomsWindow;
	private Rectangle titleRectangle = new Rectangle(53, 0, 386, 45);
	private Rectangle exitRectangle = new Rectangle(439, 9, 17, 17);
	
	private JLabel ownerLabel = new JLabel();
	private JLabel descriptionLabel = new JLabel();
	private JLabel enterRoomLabel = new JLabel(enterRoomLitImage);
	
	private Rectangle ownRoomsRectangle = new Rectangle(277, 46, 76, 22);
	private Rectangle friendsRoomsRectangle = new Rectangle(173, 47, 95, 21);
	private Rectangle popularRoomsRectangle = new Rectangle(36, 48, 124, 21);
	private Rectangle enterRoomRectangle = new Rectangle(37, 435, 122, 19);
	
	private ArrayList<VMKRoom> ownRoomsList = new ArrayList<VMKRoom>();
	
	private GuestRoomLabel activeGuestRoom = null;
	private JPanel ownRoomsPanel = new JPanel();
	
	public WindowGuestRooms(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowGuestRooms();
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
	
	private void loadWindowGuestRooms()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);

		// Owner label
		ownerLabel.setBounds(77, 299, 237, 16);
		ownerLabel.setFont(textFont);
		ownerLabel.setForeground(Color.white);
		ownerLabel.setBackground(new Color(0, 31, 86));
		ownerLabel.setHorizontalAlignment(JLabel.LEFT);
		ownerLabel.setVerticalAlignment(JLabel.CENTER);
		add(ownerLabel);
		
		// Description label
		descriptionLabel.setBounds(105, 318, 317, 107);
		descriptionLabel.setFont(textFont);
		descriptionLabel.setForeground(Color.white);
		descriptionLabel.setBackground(new Color(0, 31, 86));
		descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		descriptionLabel.setVerticalAlignment(JLabel.TOP);
		add(descriptionLabel);
		
		// Enter Room label (lit)
		enterRoomLabel.setBounds(36, 437, 123, 20);
		enterRoomLabel.setVisible(false);
		add(enterRoomLabel);
		
		// JPanel for "Own Rooms" section
		ownRoomsPanel.setBounds(29, 138, 390, 139);
		ownRoomsPanel.setBackground(new Color(0, 31, 86));
		ownRoomsPanel.setLayout(null);
		add(ownRoomsPanel);
		
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
					if(activeGuestRoom != null)
					{
						// de-activate the selected room
						ownerLabel.setText("");
						descriptionLabel.setText("");
						activeGuestRoom.deactivate();
						activeGuestRoom = null;
						enterRoomLabel.setVisible(false);
					}
					
					// close the window
					setVisible(false);
				}
				else if(enterRoomRectangle.contains(e.getPoint()) && enterRoomLabel.isVisible())
				{
					// check if there is a selected room
					if(activeGuestRoom != null)
					{
						// hide the map
						gridObject.hideMap();
						
						// a room exists, so change to the new room
						setVisible(false);
						gridObject.changeRoom(activeGuestRoom.getID());
						
						// de-activate the selected room
						ownerLabel.setText("");
						descriptionLabel.setText("");
						activeGuestRoom.deactivate();
						activeGuestRoom = null;
						enterRoomLabel.setVisible(false);
					}
					
					System.out.println("Hey dickhead, you clicked Enter Room.");
				}
				else if(ownRoomsRectangle.contains(e.getPoint()))
				{
					// Own Rooms tab
					backgroundLabel.setIcon(ownRoomsImage);
					
					// get the "Own Rooms" list
					setOwnRoomsList();
					
					// set panel visibility
					activeGuestRoom = null;
					ownRoomsPanel.setVisible(true);
					ownerLabel.setText("");
					descriptionLabel.setText("");
					enterRoomLabel.setVisible(false);
				}
				else if(friendsRoomsRectangle.contains(e.getPoint()))
				{
					// Friends Rooms tab
					backgroundLabel.setIcon(friendsRoomsImage);
					
					// set panel visibility
					activeGuestRoom = null;
					ownRoomsPanel.setVisible(false);
					ownerLabel.setText("");
					descriptionLabel.setText("");
					enterRoomLabel.setVisible(false);
				}
				else if(popularRoomsRectangle.contains(e.getPoint()))
				{
					// Popular Rooms tab
					backgroundLabel.setIcon(popularRoomsImage);
					
					// set panel visibility
					activeGuestRoom = null;
					ownRoomsPanel.setVisible(false);
					ownerLabel.setText("");
					descriptionLabel.setText("");
					enterRoomLabel.setVisible(false);
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
					guestRoomsWindow.setLocation(mouseX, mouseY);
					//repaint();
				}
			}
		});
		
		guestRoomsWindow = this;
	}
	
	// load a list of all rooms owned by the current player
	private void setOwnRoomsList()
	{
		// remove all the current room listings
		ownRoomsPanel.removeAll();
		
		// load up all rooms owned by the current player
		ownRoomsList = StaticAppletData.getRoomMappingsForOwner(gridObject.getMyCharacter().getUsername(), false);
		
		// add the listings to the necessary panel
		for(int i = 0; i < ownRoomsList.size(); i++)
		{
			VMKRoom theRoom = ownRoomsList.get(i);
			final GuestRoomLabel gl = new GuestRoomLabel(theRoom.getRoomID(), theRoom.getRoomName(), theRoom.getRoomOwner(), theRoom.getRoomDescription(), textFont);
			gl.setBounds(0, i * gl.getHeight(), gl.getWidth(), gl.getHeight());
			gl.addMouseListener(new MouseListener()
			{
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e)
				{
					// make the currently-selected guest room listing light up
					if(activeGuestRoom != null)
					{
						activeGuestRoom.deactivate();
					}
					
					activeGuestRoom = gl;
					activeGuestRoom.activate();
					
					// set the owner label text
					ownerLabel.setText(gl.getOwner());
					
					// set the description label text
					descriptionLabel.setText("<html>" + gl.getDescription() + "</html>");
					
					// make the Enter Room button active
					enterRoomLabel.setVisible(true);
				}
				public void mouseEntered(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
			});
			ownRoomsPanel.add(gl);
		}
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

// inner class to represent a Guest Room listing within the window
class GuestRoomLabel extends JLabel
{
	ImageIcon backgroundImage = AppletResourceLoader.getImageFromJar("img/ui/guest_room_listing_background.png");
	ImageIcon backgroundImageLit = AppletResourceLoader.getImageFromJar("img/ui/guest_room_listing_background_lit.png");
	
	JLabel guestRoomText;
	String roomID = "";
	String roomOwner = "";
	String roomDescription = "";
	
	int width = 0;
	int height = 0;
	
	public GuestRoomLabel(String roomID, String roomName, String roomOwner, String roomDescription, Font textFont)
	{
		this.roomID = roomID;
		this.roomOwner = roomOwner;
		this.roomDescription = roomDescription;
		
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		guestRoomText = new JLabel(roomName);
		guestRoomText.setFont(textFont);
		guestRoomText.setForeground(Color.white);
		guestRoomText.setHorizontalAlignment(JLabel.LEFT);
		guestRoomText.setVerticalAlignment(JLabel.CENTER);
		guestRoomText.setBounds(6, 1, 237, 15);
		add(guestRoomText);
		
		setIcon(backgroundImage);
		
		width = backgroundImage.getIconWidth();
		height = backgroundImage.getIconHeight();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getID() {
		return roomID;
	}
	
	public String getOwner() {
		return roomOwner;
	}
	
	public String getDescription() {
		return roomDescription;
	}
	
	public void activate()
	{
		setIcon(backgroundImageLit);
	}
	
	public void deactivate()
	{
		setIcon(backgroundImage);
	}
}
