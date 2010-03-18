// WindowMessages.java by Matt Fritz
// November 28, 2009
// Handles the "Messages" and "Friends" window

package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.FriendsList;

public class WindowMessages extends JPanel
{
	private RoomViewerGrid gridObject;
	
	private Font textFont;
	private Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 351;
	private int height = 427;
	private ImageIcon messagesWindowImage = AppletResourceLoader.getImageFromJar("img/ui/messages.png");
	private ImageIcon friendsWindowImage = AppletResourceLoader.getImageFromJar("img/ui/friends.png");
	
	// images/strings for confirming/denying friend requests
	private ArrayList<String> friendsItems = new ArrayList<String>();
	private ArrayList<String> friendRequests = new ArrayList<String>();
	private String noNewFriends = "You have no new friend requests.";
	private ImageIcon friendsWindowHeaderOffImage = AppletResourceLoader.getImageFromJar("img/ui/friends_window_header_off.png");
	private ImageIcon friendsWindowHeaderOnImage = AppletResourceLoader.getImageFromJar("img/ui/friends_window_header_on.png");
	private ImageIcon friendsWindowConfirmButtonsImage = AppletResourceLoader.getImageFromJar("img/ui/friends_window_confirm_buttons.png");
	
	private JLabel friendsRequestNotification = new JLabel(noNewFriends);
	private JLabel friendsRequestHeader = new JLabel(friendsWindowHeaderOffImage);
	private JLabel friendsRequestInformation = new JLabel("");
	private JLabel friendsRequestConfirmationButtons = new JLabel(friendsWindowConfirmButtonsImage);
	
	private JList friendsListBox = new JList();
	private JScrollPane friendsScrollPane;
	private JLabel backgroundLabel = new JLabel(messagesWindowImage);
	
	private WindowMessages messagesWindow;
	private Rectangle titleRectangle = new Rectangle(37, 7, 310, 42);
	private Rectangle exitRectangle = new Rectangle(321, 15, 16, 16);
	private Rectangle messagesTabRectangle = new Rectangle(43, 58, 50, 11);
	private Rectangle friendsTabRectangle = new Rectangle(117, 58, 38, 11);
	private Rectangle showFriendRequestRectangle = new Rectangle(238, 85, 67, 17);
	private Rectangle acceptFriendRequestRectangle = new Rectangle(35, 379, 72, 16);
	private Rectangle cancelFriendRequestRectangle = new Rectangle(117, 379, 71, 16);
	private Rectangle ignoreFriendRequestRectangle = new Rectangle(240, 379, 71, 16);
	
	public WindowMessages(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowMessages();
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
	
	private void loadWindowMessages()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		this.setLayout(null);
		
		/*friendRequests.add("Dillhole");
		friendRequests.add("Buttknocker");
		friendRequests.add("Assmunch");
		friendRequests.add("Fartknocker");
		
		// Friends list box
		for(int i = 0; i < 50; i++)
		{
			friendsItems.add("Item " + i);
		}*/
		friendsListBox.setBounds(23, 111, 269, 169);
		friendsListBox.setBackground(new Color(6, 33, 86));
		friendsListBox.setForeground(Color.WHITE);
		friendsListBox.setFont(textFont);
		friendsListBox.setCellRenderer(new FriendsListBoxRenderer());
		friendsListBox.setListData(friendsItems.toArray());
		friendsScrollPane = new JScrollPane(friendsListBox);
		friendsScrollPane.setBorder(null);
		friendsScrollPane.setBounds(31, 114, 269, 169);
		friendsScrollPane.setVisible(false);
		add(friendsScrollPane);
		
		// add the request notification
		friendsRequestNotification.setBounds(33, 81, 238, 16);
		friendsRequestNotification.setBackground(new Color(41, 85, 149));
		friendsRequestNotification.setForeground(Color.white);
		friendsRequestNotification.setFont(textFont);
		friendsRequestNotification.setVisible(false);
		add(friendsRequestNotification);
		
		// add the header
		friendsRequestHeader.setBounds(33, 81, 278, 25);
		friendsRequestHeader.setVisible(false);
		add(friendsRequestHeader);
		
		// add the request information
		friendsRequestInformation.setBounds(33, 116, 282, 267);
		friendsRequestInformation.setVerticalAlignment(JLabel.TOP);
		friendsRequestInformation.setHorizontalAlignment(JLabel.CENTER);
		friendsRequestInformation.setBackground(new Color(6, 33, 86));
		friendsRequestInformation.setForeground(Color.white);
		friendsRequestInformation.setFont(textFont);
		friendsRequestInformation.setVisible(false);
		add(friendsRequestInformation);
		
		// add the confirmation buttons
		friendsRequestConfirmationButtons.setBounds(23, 111, 302, 297);
		friendsRequestConfirmationButtons.setVisible(false);
		add(friendsRequestConfirmationButtons);
		
		// update the friend requests if we have any when the window loads
		updateFriendsRequestTab();
		
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
				else if(messagesTabRectangle.contains(e.getPoint()))
				{
					// switch to the "Messages" screen
					friendsRequestHeader.setVisible(false);
					friendsRequestNotification.setVisible(false);
					friendsRequestConfirmationButtons.setVisible(false);
					friendsScrollPane.setVisible(false);
					backgroundLabel.setIcon(messagesWindowImage);
				}
				else if(friendsTabRectangle.contains(e.getPoint()))
				{
					// switch to the "Friends" screen
					friendsRequestHeader.setVisible(true);
					friendsRequestNotification.setVisible(true);
					friendsScrollPane.setVisible(true);
					backgroundLabel.setIcon(friendsWindowImage);
				}
				else if(showFriendRequestRectangle.contains(e.getPoint()) && friendsRequestHeader.isVisible())
				{
					// show the friend request notification
					System.out.println("Clicked the Show button in Friends tab");
					
					// make sure we have new friend requests
					if(friendRequests.size() > 0)
					{
						// hide the friends list
						friendsScrollPane.setVisible(false);
						
						// show the notification(s)
						setRequestInformationMessage();
						friendsRequestInformation.setVisible(true);
						friendsRequestConfirmationButtons.setVisible(true);
					}
				}
				else if(acceptFriendRequestRectangle.contains(e.getPoint()) && friendsRequestConfirmationButtons.isVisible())
				{
					// accept the friend request
					System.out.println("Clicked the OK button in Friends tab");
					
					// add the user to the friends list
					friendsItems.add(friendRequests.get(friendRequests.size() - 1));
					friendsListBox.setListData(friendsItems.toArray());
					
					// send the confirmation message to the server (Accepted)
					gridObject.sendFriendRequestConfirmation(friendRequests.get(friendRequests.size() - 1), true);
					
					// remove the request
					friendRequests.remove(friendRequests.size() - 1);
					
					// update the friends request notifications
					setRequestInformationMessage();
					updateFriendsRequestTab();
				}
				else if(cancelFriendRequestRectangle.contains(e.getPoint()) && friendsRequestConfirmationButtons.isVisible())
				{
					// cancel the viewing of the friend request
					System.out.println("Clicked the Cancel button in Friends tab");
					
					// hide the notification(s)
					friendsRequestInformation.setVisible(false);
					friendsRequestConfirmationButtons.setVisible(false);
					
					// show the friends list again
					friendsScrollPane.setVisible(true);
				}
				else if(ignoreFriendRequestRectangle.contains(e.getPoint()) && friendsRequestConfirmationButtons.isVisible())
				{
					// ignore the friend request
					System.out.println("Clicked the Ignore button in Friends tab");
					
					// send the confirmation message to the server (Rejected)
					gridObject.sendFriendRequestConfirmation(friendRequests.get(friendRequests.size() - 1), false);
					
					// remove the request
					friendRequests.remove(friendRequests.size() - 1);
					
					// update the friends request notifications
					setRequestInformationMessage();
					updateFriendsRequestTab();
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
	
	// set the informational message for a friend request and handle its visibility
	private void setRequestInformationMessage()
	{
		// make sure we have an active friend request
		if(friendRequests.size() > 0)
		{
			String name = friendRequests.get(friendRequests.size() - 1);
			String informationText = "<html><center><b>" + name + "</b><br>";
			informationText += "is asking to become your friend<br><br>";
			informationText += "If you click OK, " + name + " will appear on your friend list and you will be added to " + name + "'s list.";
			informationText += " Adding " + name + " to your list will allow them to send you messages and find you when you're online in the kingdom.<br><br>";
			informationText += "You can remove " + name + " from your friend list at any time, which will also make you disappear from " + name + "'s list.";
			informationText += " Clicking Ignore will prevent " + name + " from asking you for a while.";
			informationText += "</center></html>";
			friendsRequestInformation.setText(informationText);
		}
		else
		{
			friendsRequestInformation.setText("<html><center><br>You have no new friend requests.</center></html>");
			friendsRequestInformation.setVisible(false);
			friendsRequestConfirmationButtons.setVisible(false);
			friendsScrollPane.setVisible(true);
		}
	}
	
	// change the status and number of friend requests
	private void updateFriendsRequestTab()
	{
		if(friendRequests.size() > 0)
		{
			// new friend requests
			friendsRequestNotification.setText("You have " + friendRequests.size() + " new friend request");
			if(friendRequests.size() > 1)
			{
				// pluralize the requests correctly
				friendsRequestNotification.setText(friendsRequestNotification.getText() + "s.");
			}
			else
			{
				// just add the period since it's only one new request
				friendsRequestNotification.setText(friendsRequestNotification.getText() + ".");
			}
			friendsRequestHeader.setIcon(friendsWindowHeaderOnImage);
		}
		else
		{
			// no new friend requests
			friendsRequestNotification.setText(noNewFriends);
			friendsRequestHeader.setIcon(friendsWindowHeaderOffImage);
		}
	}
	
	// add a friend request to the ArrayList
	public void addFriendRequest(String from)
	{
		friendRequests.add(from);
		
		// update the friends request notifications
		updateFriendsRequestTab();
	}
	
	// add a friend to the ArrayList
	public void addFriendToList(String friend)
	{
		// add the friend
		friendsItems.add(friend);
		
		// update the list to reflect the changes
		friendsListBox.setListData(friendsItems.toArray());
	}
	
	// set the friends list
	public void setFriendsList(FriendsList friendsList)
	{
		for(int i = 0; i < friendsList.getFriends().size(); i++)
		{
			// add the friend to the list
			friendsItems.add(friendsList.getFriends().get(i));
		}
		
		// update the list to reflect the changes
		friendsListBox.setListData(friendsItems.toArray());
	}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
	
	// inner class for the rendering of the Friends List
	class FriendsListBoxRenderer extends JLabel implements ListCellRenderer
	{
		public FriendsListBoxRenderer()
		{
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}
		
	    /*
	     * This method finds the image and text corresponding
	     * to the selected value and returns the label, set up
	     * to display the text and image.
	     */
	    public Component getListCellRendererComponent(
	                                       JList list,
	                                       Object value,
	                                       int index,
	                                       boolean isSelected,
	                                       boolean cellHasFocus) {
	        //Get the selected index. (The index param isn't
	        //always valid, so just use the value.)
	        //int selectedIndex = ((Integer)value).intValue();

	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

	        setText((String)value); // set the text
	        setFont(list.getFont());
	        //Set the icon and text.  If icon was null, say so.
	        /*ImageIcon icon = images[selectedIndex];
	        String pet = petStrings[selectedIndex];
	        setIcon(icon);
	        if (icon != null) {
	            setText(pet);
	            setFont(list.getFont());
	        } else {
	            setUhOhText(pet + " (no image available)",
	                        list.getFont());
	        }*/

	        return this;
	    }
	}
}
