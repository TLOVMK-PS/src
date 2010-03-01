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

import util.AppletResourceLoader;

public class WindowMessages extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 351;
	private int height = 427;
	private ImageIcon messagesWindowImage = AppletResourceLoader.getImageFromJar("img/ui/messages.png");
	private ImageIcon friendsWindowImage = AppletResourceLoader.getImageFromJar("img/ui/friends.png");
	
	private JList friendsListBox = new JList();
	private JScrollPane friendsScrollPane;
	private JLabel backgroundLabel = new JLabel(messagesWindowImage);
	
	private WindowMessages messagesWindow;
	private Rectangle titleRectangle = new Rectangle(37, 7, 310, 42);
	private Rectangle exitRectangle = new Rectangle(321, 15, 16, 16);
	private Rectangle messagesTabRectangle = new Rectangle(43, 58, 50, 11);
	private Rectangle friendsTabRectangle = new Rectangle(117, 58, 38, 11);
	
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

		// Friends list box
		ArrayList<String> items = new ArrayList<String>();
		for(int i = 0; i < 50; i++)
		{
			items.add("Item " + i);
		}
		friendsListBox.setBounds(23, 111, 269, 169);
		friendsListBox.setBackground(new Color(6, 33, 86));
		friendsListBox.setForeground(Color.WHITE);
		friendsListBox.setFont(textFont);
		friendsListBox.setCellRenderer(new FriendsListBoxRenderer());
		friendsListBox.setListData(items.toArray());
		friendsScrollPane = new JScrollPane(friendsListBox);
		friendsScrollPane.setBorder(null);
		friendsScrollPane.setBounds(31, 114, 269, 169);
		friendsScrollPane.setVisible(false);
		add(friendsScrollPane);
		
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
					friendsScrollPane.setVisible(false);
					backgroundLabel.setIcon(messagesWindowImage);
				}
				else if(friendsTabRectangle.contains(e.getPoint()))
				{
					// switch to the "Friends" screen
					friendsScrollPane.setVisible(true);
					backgroundLabel.setIcon(friendsWindowImage);
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
