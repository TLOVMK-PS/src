// WindowRoomDescription.java by Matt Fritz
// November 12, 2009
// Handles the "Room Description" window that comes up when you press the "I" button on the toolbar

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.AppletResourceLoader;

public class WindowRoomDescription extends JPanel
{
	private String roomID = "";
	private String roomName;
	private String roomDescription;
	private Font textFont;
	private Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 323;
	private int height = 148;
	
	private Rectangle exitButtonRect = new Rectangle(301, 5, 16, 18); // bounds of the "X" button relative to the image
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/room_description_window.png");
	private ImageIcon windowImageGr = AppletResourceLoader.getImageFromJar("img/ui/room_description_window_gr.png");
	
	private JLabel roomNameLabel = new JLabel("");
	private JLabel descriptionLabel = new JLabel("");
	private JLabel instanceLabel = new JLabel("North");
	private JLabel ownerLabel = new JLabel("");
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	boolean visible = false;
	
	public WindowRoomDescription(Font textFont, Font textFontBold, String roomName, String roomDescription, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		
		this.roomName = roomName;
		this.roomDescription = roomDescription;
		
		this.x = x;
		this.y = y;
		
		loadRoomDescriptionWindow();
	}
	
	private void loadRoomDescriptionWindow()
	{
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		// room name label
		roomNameLabel.setBounds(31, 5, 261, 15);
		roomNameLabel.setFont(textFontBold);
		roomNameLabel.setForeground(Color.white);
		roomNameLabel.setHorizontalAlignment(JLabel.CENTER);
		roomNameLabel.setVerticalAlignment(JLabel.CENTER);
		roomNameLabel.setText(roomName);
		add(roomNameLabel);
		
		// instance label
		instanceLabel.setBounds(73, 27, 222, 15);
		instanceLabel.setFont(textFont);
		instanceLabel.setForeground(Color.white);
		instanceLabel.setHorizontalAlignment(JLabel.LEFT);
		instanceLabel.setVerticalAlignment(JLabel.CENTER);
		add(instanceLabel);
		
		// owner label
		ownerLabel.setBounds(58, 27, 222, 15);
		ownerLabel.setFont(textFont);
		ownerLabel.setForeground(Color.white);
		ownerLabel.setHorizontalAlignment(JLabel.LEFT);
		ownerLabel.setVerticalAlignment(JLabel.CENTER);
		ownerLabel.setVisible(false);
		add(ownerLabel);
		
		// description label
		descriptionLabel.setBounds(7, 46, 309, 98);
		descriptionLabel.setFont(textFont);
		descriptionLabel.setForeground(Color.white);
		descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		descriptionLabel.setVerticalAlignment(JLabel.TOP);
		descriptionLabel.setText("<html>" + roomDescription + "</html>");
		add(descriptionLabel);
		
		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(exitButtonRect.contains(e.getPoint()))
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
		
		// background image
		backgroundLabel.setBounds(0, 0, width, height);
		add(backgroundLabel);
		
		this.setBounds(x, y, width, height);
	}

	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
		roomNameLabel.setText(roomName);
	}

	public String getRoomDescription() {
		return roomDescription;
	}

	public void setRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
		descriptionLabel.setText("<html>" + roomDescription + "</html>");
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setRoomID(String roomID)
	{
		this.roomID = roomID;
		
		if(roomID.startsWith("gr"))
		{
			// guest room
			backgroundLabel.setIcon(windowImageGr);
			instanceLabel.setVisible(false);
			ownerLabel.setVisible(true);
		}
		else
		{
			// regular room
			backgroundLabel.setIcon(windowImage);
			instanceLabel.setVisible(true);
			ownerLabel.setVisible(false);
		}
	}
	
	public void setRoomOwner(String roomOwner)
	{
		ownerLabel.setText(roomOwner);
	}
}
