// WindowShop.java by Matt Fritz
// November 29, 2009
// Handles the "Shop" window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.StaticAppletData;

public class WindowShop extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 559;
	private int height = 405;
	private ImageIcon tabRoomsImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_rooms.png");
	private ImageIcon windowImage = tabRoomsImage;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowShop shopWindow;
	private Rectangle titleRectangle = new Rectangle(53, 11, 491, 38);
	private Rectangle exitRectangle = new Rectangle(529, 14, 16, 14);
	
	private JPanel roomCreatedWindow = new JPanel();
	private JLabel roomCreatedWindowBackground = new JLabel(AppletResourceLoader.getImageFromJar("img/ui/room_created_window.png"));
	private Rectangle continueRectangle = new Rectangle(12, 288, 128, 20);
	private Rectangle enterRoomRectangle = new Rectangle(145, 288, 128, 19);
	
	private HashMap<String,String> roomInfo = new HashMap<String,String>();
	private Rectangle tabRooms = new Rectangle(38, 46, 49, 22);
	private int selectedRoomPreview = -1;
	private JLabel roomPreviewLabel = new JLabel();
	private JLabel roomNameLabel = new JLabel();
	private JLabel roomDescriptionLabel = new JLabel();
	private ArrayList<String> roomTemplates = new ArrayList<String>();
	private Rectangle btnBuyRoom = new Rectangle(406, 365, 71, 21);
	private Rectangle prevRoom = new Rectangle(26, 345, 66, 18);
	private Rectangle nextRoom = new Rectangle(292, 345, 48, 18);
	private JLabel roomCostLabel = new JLabel();
	private JLabel myCreditsLabel = new JLabel();
	
	private Rectangle tabFurnishings = new Rectangle(99, 46, 75, 21);
	private Rectangle tabPins = new Rectangle(190, 46, 33, 21);
	private Rectangle tabClothing = new Rectangle(239, 46, 57, 21);
	private Rectangle tabPosters = new Rectangle(311, 47, 50, 22);
	private Rectangle tabSpecials = new Rectangle(377, 46, 54, 20);
	
	public WindowShop(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowShop();
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
	
	private void loadWindowShop()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		// Room Created window
		roomCreatedWindow.setDoubleBuffered(false);
		roomCreatedWindow.setOpaque(false);
		roomCreatedWindow.setLayout(null);
		roomCreatedWindow.setBounds(150, 40, 283, 323);
		
		roomCreatedWindowBackground.setBounds(0,0,283,323);
		roomCreatedWindow.add(roomCreatedWindowBackground);
		roomCreatedWindow.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(continueRectangle.contains(e.getPoint()))
				{
					roomCreatedWindow.setVisible(false);
				}
				else if(enterRoomRectangle.contains(e.getPoint()))
				{
					// enter the newly-created room
					roomCreatedWindow.setVisible(false);
					shopWindow.setVisible(false);
					gridObject.changeRoom(roomInfo.get("ID")); // the ID is bounced back from the server message
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		roomCreatedWindow.setVisible(false);
		add(roomCreatedWindow);
		
		// get the list of template IDs for the guest rooms
		roomTemplates = StaticAppletData.getGuestRoomTemplates();
		
		// Room preview label
		roomPreviewLabel.setBounds(30, 103, 306, 234);
		add(roomPreviewLabel);
		
		// Room name label
		roomNameLabel.setBounds(373, 119, 132, 36);
		roomNameLabel.setForeground(Color.white);
		roomNameLabel.setBackground(new Color(40, 86, 146));
		roomNameLabel.setFont(textFont);
		roomNameLabel.setHorizontalAlignment(JLabel.CENTER);
		roomNameLabel.setVerticalAlignment(JLabel.BOTTOM);
		add(roomNameLabel);
		
		// Room description label
		roomDescriptionLabel.setBounds(372, 162, 135, 96);
		roomDescriptionLabel.setForeground(Color.white);
		roomDescriptionLabel.setBackground(new Color(40, 86, 146));
		roomDescriptionLabel.setFont(textFont);
		roomDescriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		roomDescriptionLabel.setVerticalAlignment(JLabel.TOP);
		add(roomDescriptionLabel);
		
		// Room cost label
		roomCostLabel.setBounds(72, 367, 76, 14);
		roomCostLabel.setForeground(Color.white);
		roomCostLabel.setBackground(new Color(3, 44, 100));
		roomCostLabel.setFont(textFont);
		roomCostLabel.setHorizontalAlignment(JLabel.CENTER);
		roomCostLabel.setVerticalAlignment(JLabel.CENTER);
		add(roomCostLabel);
		
		// My Credits label
		myCreditsLabel.setBounds(252, 367, 76, 14);
		myCreditsLabel.setForeground(Color.white);
		myCreditsLabel.setBackground(new Color(48, 144, 195));
		myCreditsLabel.setFont(textFont);
		myCreditsLabel.setHorizontalAlignment(JLabel.CENTER);
		myCreditsLabel.setVerticalAlignment(JLabel.CENTER);
		add(myCreditsLabel);
		
		// get the room preview at index 0
		traverseRoomTemplates("next");

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
				else if(nextRoom.contains(e.getPoint()))
				{
					// display the next room
					traverseRoomTemplates("next");
				}
				else if(prevRoom.contains(e.getPoint()))
				{
					// display the previous room
					traverseRoomTemplates("prev");
				}
				else if(btnBuyRoom.contains(e.getPoint()))
				{
					// check to make sure the player can afford the room
					if(gridObject.getMyCredits() >= Long.parseLong(roomCostLabel.getText()))
					{
						// buy the room
						//System.out.println("You chose to buy room template: " + roomTemplates.get(selectedRoomPreview));
						
						// create a new guest room by sending a "create guest room" message with mock data
						roomInfo.put("TEMPLATE", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomPath());
						roomInfo.put("NAME", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomName());
						roomInfo.put("OWNER", gridObject.getMyCharacter().getUsername());
						roomInfo.put("DESCRIPTION", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomDescription());
						roomInfo.put("TIMESTAMP", "" + System.currentTimeMillis()/1000);
						
						// send the message to the server
						gridObject.sendCreateGuestRoomMessage(roomInfo);
						
						// subtract the cost of the room from the player's credits
						gridObject.setMyCredits(gridObject.getMyCredits() - Long.parseLong(roomCostLabel.getText()));
						myCreditsLabel.setText("" + gridObject.getMyCredits());
						
						// pop-up the notification
						roomCreatedWindow.setVisible(true);
						
						// TODO: Display some notification that the player just bought something awesome
					}
					else
					{
						// TODO: Display some notification that the player is poor as cat shit
					}
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
					shopWindow.setLocation(mouseX, mouseY);
					//repaint();
				}
			}
		});
		
		shopWindow = this;
	}
	
	// change the displayed room preview given a direction to traverse
	private void traverseRoomTemplates(String direction)
	{
		if(direction.equals("prev"))
		{
			// previous room
			selectedRoomPreview--;
			
			// wrap back around to the last entry if we go out of bounds
			if(selectedRoomPreview < roomTemplates.size())
			{
				selectedRoomPreview = roomTemplates.size() - 1;
			}
		}
		else if(direction.equals("next"))
		{
			// next room
			selectedRoomPreview++;
			
			// wrap back around to the first entry if we go out of bounds
			if(selectedRoomPreview == roomTemplates.size())
			{
				selectedRoomPreview = 0;
			}
		}
		
		// display the preview image
		roomPreviewLabel.setIcon(AppletResourceLoader.getImageFromJar("img/rooms/" + roomTemplates.get(selectedRoomPreview) + "/" + roomTemplates.get(selectedRoomPreview) + "_small.jpg"));
		
		// set the name and description labels for the room
		roomNameLabel.setText("<html><center>" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomName() + "</center></html>");
		roomDescriptionLabel.setText("<html>" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomDescription() + "</html>");
		
		// set the cost for the room
		roomCostLabel.setText("" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomCost());
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
		
		if(isVisible())
		{
			myCreditsLabel.setText("" + gridObject.getMyCredits());
		}
	}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
	
	public void addRoomInfo(String key, String value)
	{
		roomInfo.put(key, value);
	}
}
