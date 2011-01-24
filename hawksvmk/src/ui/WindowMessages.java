// WindowMessages.java by Matt Fritz
// November 28, 2009
// Handles the "Messages" and "Friends" window

package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.Dictionary;
import util.FriendsList;
import util.GameConstants;
import util.MailMessage;

public class WindowMessages extends JPanel
{
	private RoomViewerGrid gridObject;
	
	private Font textFont;
	private Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 351;
	private int height = 427;
	private ImageIcon messagesWindowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "messages.png");
	private ImageIcon noMessagesWindowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "no_messages.png");
	private ImageIcon sendMessageWindowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "send_message.png");
	private ImageIcon friendsWindowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "friends.png");
	
	// images/strings for viewing and replying to messages
	private String noNewMessages = "You have no new messages.";
	private JLabel messagesNotification = new JLabel(noNewMessages);
	private ArrayList<MailMessage> messages = new ArrayList<MailMessage>();
	private JLabel messageSender = new JLabel("");
	private JLabel messageDate = new JLabel("");
	private JLabel messageText = new JLabel("");
	
	private JLabel messageRecipient = new JLabel("");
	private JTextArea messageReplyText = new JTextArea("");
	private final int maxMessageCharacters = 350; // maximum number of characters in a message reply
	
	// images/strings for confirming/denying friend requests
	private ArrayList<String> friendsItems = new ArrayList<String>();
	private ArrayList<String> onlineFriends = new ArrayList<String>();
	private ArrayList<String> friendRequests = new ArrayList<String>();
	private String noNewFriends = "You have no new friend requests.";
	private ImageIcon friendsWindowHeaderOffImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "friends_window_header_off.png");
	private ImageIcon friendsWindowHeaderOnImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "friends_window_header_on.png");
	private ImageIcon friendsWindowConfirmButtonsImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "friends_window_confirm_buttons.png");
	
	private boolean deleteMode = false; // TRUE if we're deleting a friend
	
	private JLabel friendsRequestNotification = new JLabel(noNewFriends);
	private JLabel friendsRequestHeader = new JLabel(friendsWindowHeaderOffImage);
	private JLabel friendsRequestInformation = new JLabel("");
	private JLabel friendsRequestConfirmationButtons = new JLabel(friendsWindowConfirmButtonsImage);
	
	private JList friendsListBox = new JList();
	private JScrollPane friendsScrollPane;
	private JLabel backgroundLabel = new JLabel(noMessagesWindowImage);
	
	private WindowMessages messagesWindow;
	private Rectangle titleRectangle = new Rectangle(37, 7, 310, 42);
	private Rectangle exitRectangle = new Rectangle(321, 15, 16, 16);
	
	private Rectangle messagesTabRectangle = new Rectangle(43, 58, 50, 11);
	private Rectangle replyRectangle = new Rectangle(38, 376, 115, 17);
	private Rectangle reportRectangle = new Rectangle(178, 350, 116, 16);
	private Rectangle deleteMessageRectangle = new Rectangle(179, 376, 115, 16);
	
	private Rectangle sendReplyRectangle = new Rectangle(47, 375, 114, 17);
	private Rectangle cancelMessageRectangle = new Rectangle(185, 375, 115, 17);
	
	private Rectangle friendsTabRectangle = new Rectangle(117, 58, 38, 11);
	private Rectangle showFriendRequestRectangle = new Rectangle(238, 85, 67, 17);
	private Rectangle acceptFriendRequestRectangle = new Rectangle(35, 379, 72, 16);
	private Rectangle cancelFriendRequestRectangle = new Rectangle(117, 379, 71, 16);
	private Rectangle ignoreFriendRequestRectangle = new Rectangle(240, 379, 71, 16);
	
	private Rectangle deleteFriendRectangle = new Rectangle(37, 377, 49, 17);
	private Rectangle enterSameRoomRectangle = new Rectangle(199, 302, 115, 17);
	private Rectangle sendMessageRectangle = new Rectangle(198, 377, 116, 17);
	
	public WindowMessages(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				loadWindowMessages();
			}
		});
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
		
		// message sender
		messageSender.setBounds(80, 113, 269, 16);
		messageSender.setBackground(new Color(6, 33, 86));
		messageSender.setForeground(Color.WHITE);
		messageSender.setFont(textFont);
		add(messageSender);
		
		// message date
		messageDate.setBounds(80, 127, 269, 16);
		messageDate.setBackground(new Color(6, 33, 86));
		messageDate.setForeground(Color.WHITE);
		messageDate.setFont(textFont);
		add(messageDate);
		
		// message text
		messageText.setBounds(35, 152, 260, 187);
		messageText.setBackground(new Color(6, 33, 86));
		messageText.setForeground(Color.WHITE);
		messageText.setFont(textFont);
		messageText.setHorizontalAlignment(JLabel.LEFT);
		messageText.setVerticalAlignment(JLabel.TOP);
		add(messageText);
		
		// add the message notification
		messagesNotification.setBounds(48, 83, 238, 16);
		messagesNotification.setBackground(new Color(41, 85, 149));
		messagesNotification.setForeground(Color.white);
		messagesNotification.setFont(textFont);
		messagesNotification.setVerticalAlignment(JLabel.TOP);
		messagesNotification.setHorizontalAlignment(JLabel.CENTER);
		add(messagesNotification);
		
		// add the recipient
		messageRecipient.setBounds(72, 118, 238, 16);
		messageRecipient.setBackground(new Color(152, 190, 255));
		messageRecipient.setForeground(Color.black);
		messageRecipient.setFont(textFont);
		messageRecipient.setHorizontalAlignment(JLabel.LEFT);
		messageRecipient.setVerticalAlignment(JLabel.TOP);
		messageRecipient.setVisible(false);
		add(messageRecipient);
		
		// add the reply text section
		messageReplyText.setBounds(51, 160, 247, 182);
		messageReplyText.setBackground(new Color(152, 190, 255));
		messageReplyText.setForeground(Color.BLACK);
		messageReplyText.setFont(textFont);
		messageReplyText.setBorder(null);
		messageReplyText.setLineWrap(true);
		messageReplyText.addKeyListener(new KeyListener()
	     {
	    	 public void keyPressed(KeyEvent e)
	    	 {
	    		 if(e.getKeyCode() == KeyEvent.VK_ENTER)
				 {
					 // don't allow ENTER
					 e.consume();
				 }
	    	 }
	    	 public void keyTyped(KeyEvent e)
	    	 {
				 // only allow a certain number of characters
				 if(messageReplyText.getText().length() > maxMessageCharacters)
				 {
					 e.consume();
				 }
	    	 }
	    	 public void keyReleased(KeyEvent e)
	    	 {
	    	 }
	     });
		messageReplyText.setVisible(false);
		add(messageReplyText);
		
		// friends list
		friendsListBox.setBounds(23, 111, 289, 169);
		friendsListBox.setBackground(new Color(6, 33, 86));
		friendsListBox.setForeground(Color.WHITE);
		friendsListBox.setFont(textFont);
		friendsListBox.setCellRenderer(new FriendsListBoxRenderer());
		friendsListBox.setListData(friendsItems.toArray());
		friendsScrollPane = new JScrollPane(friendsListBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		friendsScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		friendsScrollPane.setBorder(null);
		friendsScrollPane.setBounds(31, 114, 289, 169);
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
					
					// check if we actually have messages to display
					if(messages.size() > 0)
					{
						messageSender.setVisible(true);
						messageDate.setVisible(true);
						messagesNotification.setVisible(true);
						messageText.setVisible(true);
						messageRecipient.setVisible(false);
						messageReplyText.setVisible(false);
						
						backgroundLabel.setIcon(messagesWindowImage);
					}
					else
					{
						messageSender.setVisible(false);
						messageDate.setVisible(false);
						messagesNotification.setVisible(true);
						messageText.setVisible(false);
						messageRecipient.setVisible(false);
						messageReplyText.setVisible(false);
						
						backgroundLabel.setIcon(noMessagesWindowImage);
					}
				}
				else if(friendsTabRectangle.contains(e.getPoint()))
				{
					// switch to the "Friends" screen
					friendsRequestHeader.setVisible(true);
					friendsRequestNotification.setVisible(true);
					friendsScrollPane.setVisible(true);
					
					messageSender.setVisible(false);
					messageDate.setVisible(false);
					messagesNotification.setVisible(false);
					messageText.setVisible(false);
					messageRecipient.setVisible(false);
					messageReplyText.setVisible(false);
					
					backgroundLabel.setIcon(friendsWindowImage);
				}
				else if(replyRectangle.contains(e.getPoint()) && messageText.isVisible())
				{
					// make sure this isn't an automated message from VMK Staff
					if(!messageSender.getText().equals("VMK Staff"))
					{
						// hide the message information
						messageSender.setVisible(false);
						messageDate.setVisible(false);
						messagesNotification.setVisible(false);
						messageText.setVisible(false);
						
						// show the recipient
						messageRecipient.setText(messageSender.getText());
						messageRecipient.setVisible(true);
						messageReplyText.setVisible(true);
						
						// reply to the message
						backgroundLabel.setIcon(sendMessageWindowImage);
					}
				}
				else if(reportRectangle.contains(e.getPoint()) && messageText.isVisible())
				{
					// make sure this isn't an automated message from VMK Staff
					if(!messageSender.getText().equals("HVMK Staff"))
					{
						// report the message
					}
				}
				else if(deleteMessageRectangle.contains(e.getPoint()) && messageText.isVisible())
				{	
					// delete the message if it's visible
					messages.remove(messages.size() - 1);
					
					// send a "Save Mail" message back to the server
					gridObject.sendSaveMailMessage(messages);
					
					// update the Messages tab
					updateMessagesTab();
					
					// show the next message
					if(messages.size() > 0)
					{
						// get the next message
						MailMessage m = messages.get(messages.size() - 1);
						
						// set the information
						messageSender.setText(m.getSender());
						messageDate.setText(m.getDateSent().toString());
						messageText.setText("<html>" + m.getMessage() + "</html>");
						
						// change the background window image to the "Messages" version
						backgroundLabel.setIcon(messagesWindowImage);
					}
					else
					{
						// clear the information
						messageSender.setText("");
						messageDate.setText("");
						messageText.setText("");
						
						// hide the message controls
						messageSender.setVisible(false);
						messageDate.setVisible(false);
						messageText.setVisible(false);
						
						// change the background window image to the "No Messages" version
						backgroundLabel.setIcon(noMessagesWindowImage);
					}
				}
				else if(sendReplyRectangle.contains(e.getPoint()) && !messageReplyText.getText().equals("") && messageReplyText.isVisible())
				{
					// send the reply message to the server
					gridObject.sendMailMessage(messageRecipient.getText(), messageReplyText.getText());
					
					// clear the message data
					messageRecipient.setText("");
					messageReplyText.setText("");
					
					// hide the reply controls
					messageRecipient.setVisible(false);
					messageReplyText.setVisible(false);
					
					// check to see if we have messages to display
					if(messages.size() > 0)
					{
						// show the messages controls
						messageSender.setVisible(true);
						messageDate.setVisible(true);
						messagesNotification.setVisible(true);
						messageText.setVisible(true);
						
						// set the window image to the "Messages" version
						backgroundLabel.setIcon(messagesWindowImage);
					}
					else
					{
						messagesNotification.setVisible(true);
						backgroundLabel.setIcon(noMessagesWindowImage);
					}
				}
				else if(cancelMessageRectangle.contains(e.getPoint()) && messageReplyText.isVisible())
				{
					// cancel the message
					messageRecipient.setText("");
					messageReplyText.setText("");
					
					// hide the reply controls
					messageRecipient.setVisible(false);
					messageReplyText.setVisible(false);
					
					// check to see if we have messages to display
					if(messages.size() > 0)
					{
						// show the messages controls
						messageSender.setVisible(true);
						messageDate.setVisible(true);
						messagesNotification.setVisible(true);
						messageText.setVisible(true);
						
						// set the window image to the "Messages" version
						backgroundLabel.setIcon(messagesWindowImage);
					}
					else
					{
						messagesNotification.setVisible(true);
						backgroundLabel.setIcon(noMessagesWindowImage);
					}
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
					
					// check if delete mode has been activated
					if(!deleteMode)
					{
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
					else
					{
						// send the friend deletion message to the server
						gridObject.sendDeleteFriendMessage(friendsItems.get(friendsListBox.getSelectedIndex()));
						
						// remove the friend
						friendsItems.remove(friendsListBox.getSelectedIndex());
						
						// update the friends list box
						friendsListBox.setListData(friendsItems.toArray());
						
						// hide the notification(s)
						friendsRequestInformation.setVisible(false);
						friendsRequestConfirmationButtons.setVisible(false);
						
						// show the friends list again
						friendsScrollPane.setVisible(true);
					}
					
					deleteMode = false;
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
					
					// reset delete mode
					deleteMode = false;
				}
				else if(ignoreFriendRequestRectangle.contains(e.getPoint()) && friendsRequestConfirmationButtons.isVisible())
				{
					// ignore the friend request
					System.out.println("Clicked the Ignore button in Friends tab");
					
					// check if delete mode has been activated
					if(!deleteMode)
					{
						// send the confirmation message to the server (Rejected)
						gridObject.sendFriendRequestConfirmation(friendRequests.get(friendRequests.size() - 1), false);
						
						// remove the request
						friendRequests.remove(friendRequests.size() - 1);
						
						// update the friends request notifications
						setRequestInformationMessage();
						updateFriendsRequestTab();
					}
					else
					{
						// cancel the deletion
						// hide the notification(s)
						friendsRequestInformation.setVisible(false);
						friendsRequestConfirmationButtons.setVisible(false);
						
						// show the friends list again
						friendsScrollPane.setVisible(true);
					}
					
					deleteMode = false;
				}
				else if(deleteFriendRectangle.contains(e.getPoint()) && friendsScrollPane.isVisible())
				{
					// make sure we have a selected friend
					if(friendsListBox.getSelectedIndex() != -1)
					{
						// set delete mode
						deleteMode = true;
						
						// delete the selected friend
						System.out.println("Clicked the Delete button in Friends tab");
						
						// change the notification
						setDeletionInformationMessage(friendsItems.get(friendsListBox.getSelectedIndex()));
						
						// hide the controls and show the notification
						friendsRequestInformation.setVisible(true);
						friendsRequestConfirmationButtons.setVisible(true);
						friendsScrollPane.setVisible(false);
					}
				}
				else if(enterSameRoomRectangle.contains(e.getPoint()) && friendsScrollPane.isVisible())
				{
					// enter the same room as the selected friend
					System.out.println("Clicked the Enter Same Room button in Friends tab");
				}
				else if(sendMessageRectangle.contains(e.getPoint()) && friendsScrollPane.isVisible())
				{
					// send a message to the selected friend
					System.out.println("Clicked the Send Message button in Friends tab");
					
					// make sure a friend has been selected
					if(friendsListBox.getSelectedIndex() != -1)
					{
						// hide the friend information
						friendsRequestHeader.setVisible(false);
						friendsRequestNotification.setVisible(false);
						friendsRequestConfirmationButtons.setVisible(false);
						friendsScrollPane.setVisible(false);
						
						// show the recipient
						messageRecipient.setText(friendsItems.get(friendsListBox.getSelectedIndex()));
						messageRecipient.setVisible(true);
						messageReplyText.setVisible(true);
						
						// send him a message
						backgroundLabel.setIcon(sendMessageWindowImage);
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
			public void mouseMoved(MouseEvent e)
			{
			}
			public void mouseDragged(MouseEvent e)
			{
				// check if the mouse is inside the "title bar"
				if(titleRectangle.contains(e.getPoint()))
				{
					Point p = new Point(e.getXOnScreen(), e.getYOnScreen());
					SwingUtilities.convertPointFromScreen(p, gridObject);
					int mouseX = p.x - (getBounds().width / 2); //gridObject.getBounds().x + getBounds().x;//e.getXOnScreen() - gridObject.getBounds().x - (getBounds().width / 2);
					int mouseY = p.y - (titleRectangle.height / 2); //e.getYOnScreen() - gridObject.getBounds().y - 75;
					messagesWindow.setLocation(mouseX, mouseY);
					//repaint();
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
	
	// set the informational message for a friend deletion and handle its visibility
	private void setDeletionInformationMessage(String friend)
	{
		String informationText = "<html><center><b>" + friend + "</b><br>";
		informationText += "Are you sure you want to delete this friend?<br><br>";
		informationText += "If you click OK, " + friend + " will disappear from your friends list and you will disappear from " + friend + "'s list.<br><br>";
		informationText += " Deleting " + friend + " will prevent them from sending you messages and following you into rooms while you're online in the kingdom.";
		informationText += "</center></html>";
		friendsRequestInformation.setText(informationText);
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
	
	// change the status and number of new messages
	private void updateMessagesTab()
	{
		if(messages.size() > 0)
		{
			// new messages
			messagesNotification.setText("You have " + messages.size() + " new message");
			if(messages.size() > 1)
			{
				// pluralize the messages correctly
				messagesNotification.setText(messagesNotification.getText() + "s.");
			}
			else
			{
				// just add the period since it's only one new message
				messagesNotification.setText(messagesNotification.getText() + ".");
			}
			//friendsRequestHeader.setIcon(friendsWindowHeaderOnImage);
		}
		else
		{
			// no new messages
			messagesNotification.setText(noNewMessages);
			//friendsRequestHeader.setIcon(friendsWindowHeaderOffImage);
		}
	}
	
	// add a mail message to the ArrayList
	public void addMailMessage(MailMessage theMessage)
	{
		MailMessage message = theMessage;
		
		// clean the message and remove inappropriate text based on the player's content level
		message.setMessage(Dictionary.cleanInappropriateText(gridObject.getMyCharacter(), message.getMessage()));
		
		messages.add(message);
		
		// show the most recent message in the window
		messageSender.setText(message.getSender());
		messageDate.setText(message.getDateSent().toString());
		messageText.setText("<html>" + message.getMessage() + "</html>");
		
		// make sure we aren't on the Friends tab
		if(!friendsScrollPane.isVisible())
		{
			// make the message information visible
			messageSender.setVisible(true);
			messageDate.setVisible(true);
			messageText.setVisible(true);
			
			// change the window background to the "Messages" version
			backgroundLabel.setIcon(messagesWindowImage);
		}
		
		// update the Messages tab to show new messages
		updateMessagesTab();
	}
	
	// set the user's mail messages in the ArrayList
	public void setMailMessages(ArrayList<MailMessage> messages)
	{
		this.messages = messages;
		
		// make sure we have messages to display
		if(messages.size() > 0)
		{
			// show the most recent message in the window
			messageSender.setText(messages.get(messages.size() - 1).getSender());
			messageDate.setText(messages.get(messages.size() - 1).getDateSent());
			messageText.setText("<html>" + messages.get(messages.size() - 1).getMessage() + "</html>");
			
			// show the message controls
			messageSender.setVisible(true);
			messageDate.setVisible(true);
			messageText.setVisible(true);
			
			// change the window background to the "Messages" version
			backgroundLabel.setIcon(messagesWindowImage);
			
			// update the Messages tab to show new messages
			updateMessagesTab();
		}
	}
	
	// add a friend request to the ArrayList
	public void addFriendRequest(String from)
	{
		// check to make sure a request isn't already pending and that the player isn't already a friend
		if(!friendRequests.contains(from) && !friendsItems.contains(from))
		{
			// add the request
			friendRequests.add(from);
			
			// update the friends request notifications
			updateFriendsRequestTab();
		}
	}
	
	// add a friend to the ArrayList
	public void addFriendToList(String friend)
	{
		// add the friend
		friendsItems.add(friend);
		
		// update the list to reflect the changes
		updateFriendsList();
	}
	
	// remove a friend from the ArrayList
	public void removeFriendFromList(String friend)
	{
		// remove the friend
		friendsItems.remove(friend);
		
		// check and make sure the friend is gone from the online friends
		onlineFriends.remove(friend);
		
		// update the list to reflect the changes
		updateFriendsList();
	}
	
	// set the friends list
	public void setFriendsList(FriendsList friendsList)
	{
		Iterator<String> friendsIterator = friendsList.getFriends().keySet().iterator();
		while(friendsIterator.hasNext())
		{
			// add the friend to the list
			friendsItems.add(friendsIterator.next());
		}
		
		// update the list to reflect the changes
		updateFriendsList();
	}
	
	// update the friends list, ordering online friends above offline friends
	private void updateFriendsList()
	{
		ArrayList<String> displayedFriends = friendsItems;
		displayedFriends.removeAll(onlineFriends); // remove all online friends from the offline friends list
		
		// sort the offline friends
		Collections.sort(displayedFriends);
		
		// sort the online friends
		Collections.sort(onlineFriends);
		
		// re-add the online friends to the top of the list
		displayedFriends.addAll(0, onlineFriends);
		
		friendsListBox.setListData(displayedFriends.toArray());
	}
	
	// set whether a given friend is online or not
	public void setFriendOnline(String friend, boolean online)
	{
		// make sure this user is in the friends list
		if(!friendsItems.contains(friend)) {return;}
		
		// check if the friend is online
		if(online)
		{
			// make sure the friend isn't already in the list of online friends
			if(!onlineFriends.contains(friend))
			{
				onlineFriends.add(friend);
			}
		}
		else
		{
			// remove the friend from the online list
			onlineFriends.remove(friend);
		}
	}
	
	// check to see if the Friends List contains a certain username
	public boolean friendsListContains(String username)
	{
		return friendsItems.contains(username);
	}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
	
	class MyScrollBarUI extends BasicScrollBarUI
	{
		protected void configureScrollBarColors()
		{
			thumbColor = new Color(153, 204, 255);//Color.lightGray;
			//thumbDarkShadowColor = Color.darkGray;
			//thumbHighlightColor = Color.white;
			//thumbLightShadowColor = Color.lightGray;
			trackColor = new Color(0, 153, 204);//Color.gray;
			//trackHighlightColor = Color.gray;
		}

		protected JButton createDecreaseButton(int orientation)
		{
			JButton button = new BasicArrowButton(orientation);
			button.setBackground(new Color(153, 204, 255));
			button.setForeground(new Color(40, 88, 136));
			return button;
		}

		protected JButton createIncreaseButton(int orientation)
		{
			JButton button = new BasicArrowButton(orientation);
			button.setBackground(new Color(153, 204, 255));
			button.setForeground(new Color(40, 88, 136));
			return button;
		}
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
	        }
	        else
	        {
	        	// check whether the friend is online
	        	if(!onlineFriends.contains((String)value))
	        	{
	        		// friend is offline
	        		setBackground(new Color(6, 33, 86));
	        		setForeground(Color.WHITE);
	        	}
	        	else
	        	{
	        		// friend is online
	        		setBackground(new Color(82, 240, 101));
	        		setForeground(Color.WHITE);
	        	}
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
