// WindowEditRoomDescription.java by Matt Fritz
// October 26, 2010
// Handles the "Edit Room Description" window that comes up when you press the "I" button on the toolbar in a Guest Room

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;

public class WindowEditRoomDescription extends JPanel
{
	private final int ROOM_NAME_CHARACTER_LIMIT = 42;
	private final int DESCRIPTION_CHARACTER_LIMIT = 164;
	private final int MUSIC_CHARACTER_LIMIT = 300;
	
	private String roomID = "";
	private String roomName = "";
	private String roomDescription = "";
	private String musicURL = "";
	private Font textFont;
	private Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 323;
	private int height = 300;
	
	private Rectangle exitButtonRect = new Rectangle(301, 6, 16, 16); // bounds of the "X" button relative to the image
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/room_description_window_edit.png");

	private JTextField roomNameBox = new JTextField("");
	private JTextArea descriptionBox = new JTextArea("");
	private JTextField musicURLBox = new JTextField("");
	
	private Rectangle okButton = new Rectangle(39, 275, 106, 19);
	private Rectangle cancelButton = new Rectangle(174, 275, 106, 19);
	
	private JLabel ownerLabel = new JLabel("");
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	boolean visible = false;
	
	private RoomViewerGrid gridObject = null;
	
	public WindowEditRoomDescription(Font textFont, Font textFontBold, String roomName, String roomDescription, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		
		this.roomName = roomName;
		this.roomDescription = roomDescription;
		
		this.x = x;
		this.y = y;
		
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				loadEditRoomDescriptionWindow();
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
	
	private void loadEditRoomDescriptionWindow()
	{
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		// owner label
		ownerLabel.setBounds(58, 27, 222, 15);
		ownerLabel.setFont(textFont);
		ownerLabel.setForeground(Color.white);
		ownerLabel.setHorizontalAlignment(JLabel.LEFT);
		ownerLabel.setVerticalAlignment(JLabel.CENTER);
		ownerLabel.setVisible(true);
		add(ownerLabel);
		
		// room name box
		roomNameBox.setBounds(10, 86, 298, 29);
		roomNameBox.setFont(textFont);
		roomNameBox.setBackground(new Color(152, 190, 255));
		roomNameBox.setForeground(Color.black);
		roomNameBox.setBorder(null);
		roomNameBox.setHorizontalAlignment(JTextField.LEFT);
		roomNameBox.setText(roomName);
		roomNameBox.addKeyListener(new KeyListener()
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
				if(roomNameBox.getText().length() > ROOM_NAME_CHARACTER_LIMIT)
				{
					e.consume();
				}
			}
			public void keyReleased(KeyEvent e)
			{
			}
		});
		add(roomNameBox);
		
		// description box
		descriptionBox.setBounds(10, 143, 298, 66);
		descriptionBox.setFont(textFont);
		descriptionBox.setBackground(new Color(152, 190, 255));
		descriptionBox.setForeground(Color.black);
		descriptionBox.setText(roomDescription);
		descriptionBox.setLineWrap(true);
		descriptionBox.addKeyListener(new KeyListener()
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
				if(descriptionBox.getText().length() > DESCRIPTION_CHARACTER_LIMIT)
				{
					e.consume();
				}
			}
			public void keyReleased(KeyEvent e)
			{
			}
		});
		add(descriptionBox);
		
		// music URL box
		musicURLBox.setBounds(10, 231, 298, 29);
		musicURLBox.setFont(textFont);
		musicURLBox.setBackground(new Color(152, 190, 255));
		musicURLBox.setForeground(Color.black);
		musicURLBox.setBorder(null);
		musicURLBox.setHorizontalAlignment(JTextField.LEFT);
		musicURLBox.setText(musicURL);
		musicURLBox.addKeyListener(new KeyListener()
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
				if(musicURLBox.getText().length() > MUSIC_CHARACTER_LIMIT)
				{
					e.consume();
				}
			}
			public void keyReleased(KeyEvent e)
			{
			}
		});
		add(musicURLBox);
		
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
				else if(cancelButton.contains(e.getPoint()))
				{
					// close the window
					setVisible(false);
				}
				else if(okButton.contains(e.getPoint()))
				{
					// save the Guest Room information
					gridObject.addRoomInfo("NAME", roomNameBox.getText());
					gridObject.addRoomInfo("DESCRIPTION", descriptionBox.getText());
					gridObject.addRoomInfo("MUSIC", musicURLBox.getText());
					
					// close the window
					setVisible(false);
					
					// send the Save Guest Room message with a call to saveRoomItems()
					gridObject.saveRoomItems();
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
		roomNameBox.setText(roomName);
	}

	public String getRoomDescription() {
		return roomDescription;
	}

	public void setRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
		descriptionBox.setText(roomDescription);
	}
	
	public void setMusicURL(String musicURL)
	{
		if(musicURL != null)
		{
			this.musicURL = musicURL;
			musicURLBox.setText(musicURL);
		}
		else
		{
			this.musicURL = "";
			musicURLBox.setText("");
		}
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
	}
	
	public void setRoomOwner(String roomOwner)
	{
		ownerLabel.setText(roomOwner);
	}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
}
