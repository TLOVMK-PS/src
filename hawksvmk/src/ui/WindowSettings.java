// WindowSettings.java by Matt Fritz
// November 29, 2009
// Handles the "Settings" window

package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;

public class WindowSettings extends JPanel implements ActionListener
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 334;
	private int height = 434;
	
	private ImageIcon settingsImage = AppletResourceLoader.getImageFromJar("img/ui/settings.png");
	private ImageIcon ratingImage = AppletResourceLoader.getImageFromJar("img/ui/rating.png");
	private ImageIcon windowImage = settingsImage;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowSettings messagesWindow;
	private Rectangle titleRectangle = new Rectangle(39, 7, 291, 34);
	private Rectangle exitRectangle = new Rectangle(309, 11, 16, 16);
	private Rectangle settingsTab = new Rectangle(30, 37, 69, 23);
	private Rectangle ratingTab = new Rectangle(96, 37, 69, 23);
	private Rectangle okRectangle = new Rectangle(32, 391, 106, 20);
	private Rectangle cancelRectangle = new Rectangle(198, 391, 106, 20);
	
	// rating selectors
	private String selectedRating = "G";
	private JRadioButton ratingGBtn = new JRadioButton("General");
	private JRadioButton ratingPGBtn = new JRadioButton("PG");
	private JRadioButton ratingPG13Btn = new JRadioButton("PG-13");
	private JRadioButton ratingMBtn = new JRadioButton("Mature");
	private ButtonGroup ratingGroup = new ButtonGroup();
	
	public WindowSettings(Font textFont, Font textFontBold, int x, int y)
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
		
		setLayout(null);
		
		// rating selectors
		ratingGBtn.setBounds(120, 200, 100, 16);
		ratingGBtn.setFont(textFont);
		ratingGBtn.setBackground(new Color(41, 86, 145));
		ratingGBtn.setForeground(Color.white);
		ratingGBtn.setVisible(false);
		ratingGBtn.setActionCommand("G");
		ratingGBtn.addActionListener(this);
		add(ratingGBtn);
		
		ratingPGBtn.setBounds(120, 230, 100, 16);
		ratingPGBtn.setFont(textFont);
		ratingPGBtn.setBackground(new Color(41, 86, 145));
		ratingPGBtn.setForeground(Color.white);
		ratingPGBtn.setVisible(false);
		ratingPGBtn.setActionCommand("PG");
		ratingPGBtn.addActionListener(this);
		add(ratingPGBtn);
		
		ratingPG13Btn.setBounds(120, 260, 100, 16);
		ratingPG13Btn.setFont(textFont);
		ratingPG13Btn.setBackground(new Color(41, 86, 145));
		ratingPG13Btn.setForeground(Color.white);
		ratingPG13Btn.setVisible(false);
		ratingPG13Btn.setActionCommand("PG-13");
		ratingPG13Btn.addActionListener(this);
		add(ratingPG13Btn);
		
		ratingMBtn.setBounds(120, 290, 100, 16);
		ratingMBtn.setFont(textFont);
		ratingMBtn.setBackground(new Color(41, 86, 145));
		ratingMBtn.setForeground(Color.white);
		ratingMBtn.setVisible(false);
		ratingMBtn.setActionCommand("M");
		ratingMBtn.addActionListener(this);
		add(ratingMBtn);
		
		ratingGroup.add(ratingGBtn);
		ratingGroup.add(ratingPGBtn);
		ratingGroup.add(ratingPG13Btn);
		ratingGroup.add(ratingMBtn);

		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(settingsTab.contains(e.getPoint()))
				{
					// change the tab to Settings
					changeTab("settings");
				}
				else if(ratingTab.contains(e.getPoint()))
				{
					// change the tab to Rating
					changeTab("rating");
				}
				else if(okRectangle.contains(e.getPoint()))
				{
					// OK button
					
					// close the window
					setVisible(false);
					
					// set the player rating and send the Update message to the server
					gridObject.getMyCharacter().setContentRating(selectedRating);
					gridObject.sendUpdateCharacterMessage(gridObject.getMyCharacter());
				}
				else if(cancelRectangle.contains(e.getPoint()))
				{
					// Cancel button
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
					messagesWindow.setLocation(mouseX, mouseY);
					//repaint();
				}
			}
		});
		
		messagesWindow = this;
	}
	
	// change the window components based upon the tab that was clicked
	private void changeTab(String tab)
	{
		if(tab.equals("settings"))
		{
			// show the settings window
			ratingGBtn.setVisible(false);
			ratingPGBtn.setVisible(false);
			ratingPG13Btn.setVisible(false);
			ratingMBtn.setVisible(false);
			backgroundLabel.setIcon(settingsImage);
		}
		else if(tab.equals("rating"))
		{
			// show the rating window
			ratingGBtn.setVisible(true);
			ratingPGBtn.setVisible(true);
			ratingPG13Btn.setVisible(true);
			ratingMBtn.setVisible(true);
			backgroundLabel.setIcon(ratingImage);
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		// set the player's content rating based upon the action command of the radio button
		selectedRating = e.getActionCommand();
	}
	
	// figure out which radio button to select given a String rating
	public void setSelectedRating(String rating)
	{
		for(Component c : getComponents())
		{
			if(c instanceof JRadioButton)
			{
				JRadioButton btn = (JRadioButton)c;
				if(btn.getActionCommand().equals(rating))
				{
					btn.setSelected(true);
				}
			}
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
