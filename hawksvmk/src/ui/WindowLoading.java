// WindowLoading.java by Matt Fritz
// March 24, 2010
// Handle showing the "Loading" window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import roomviewer.RoomViewerGrid;
import util.AppletResourceLoader;

public class WindowLoading extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 363;
	private int height = 241;
	
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/logo_loading_background.png");
	private ImageIcon loadingImage = AppletResourceLoader.getImageFromJar("img/ui/loading_bar_anim.gif");
	
	private JLabel loadingBarLabel = new JLabel(loadingImage);
	private JLabel roomTitleLabel = new JLabel("");
	private JLabel descriptionLabel = new JLabel("");
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	WindowLoading loadingWindow;
	
	public WindowLoading(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowLoading();
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
	
	private void loadWindowLoading()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		this.setLayout(null);
		
		// add the room title label
		roomTitleLabel.setBounds(10, 125, 343, 16);
		roomTitleLabel.setBackground(new Color(0, 31, 85));
		roomTitleLabel.setForeground(Color.white);
		roomTitleLabel.setHorizontalAlignment(JLabel.CENTER);
		roomTitleLabel.setVerticalAlignment(JLabel.TOP);
		roomTitleLabel.setFont(textFontBold);
		add(roomTitleLabel);
		
		// add the loading bar
		loadingBarLabel.setBounds(14, 157, 336, 17);
		add(loadingBarLabel);
		
		// add the description label
		descriptionLabel.setBounds(10, 189, 343, 16);
		descriptionLabel.setBackground(new Color(0, 31, 85));
		descriptionLabel.setForeground(Color.white);
		descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		descriptionLabel.setVerticalAlignment(JLabel.TOP);
		descriptionLabel.setFont(textFont);
		add(descriptionLabel);
		
		// add the background image
		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		loadingWindow = this;
	}
	
	public void setRoomTitle(String roomTitle)
	{
		roomTitleLabel.setText(roomTitle);
	}
	
	public void setDescription(String description)
	{
		descriptionLabel.setText(description);
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
