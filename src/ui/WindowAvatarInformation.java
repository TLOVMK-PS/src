// WindowAvatarInformation.java by Matt Fritz
// November 29, 2009
// Handles the "Avatar Information" pop-up window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.GameConstants;
import util.InventoryInfo;
import util.RatingSystem;
import util.StaticAppletData;

public class WindowAvatarInformation extends JPanel
{
	private RoomViewerGrid gridObject;
	
	private Font textFont;
	private Font textFontBold;
	
	private String username = "";
	private boolean inactive = false; // TRUE if this is the current user's window
	
	private int x = 0;
	private int y = 0;
	
	private int width = 185;
	private int height = 268;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "avatar_info_window.png");
	private ImageIcon windowImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "avatar_info_window_inactive.png");
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	private JLabel usernameLabel = new JLabel("");
	private JLabel signatureLabel = new JLabel("<html><p>Line 1 Line 1 Line 1 Line 2 Line 2 Line 2 Line 3 Line 3 Line 3 Line 4 Line 4 Line 4 Line 4</p></html>");
	private JLabel pinDescriptionLabel = new JLabel("");
	
	private int maxBadges = StaticAppletData.MAX_DISPLAYABLE_BADGES; // maximum amount of displayable badges
	PinSquare badges[] = new PinSquare[maxBadges]; // badges
	
	private int maxPins = StaticAppletData.MAX_DISPLAYABLE_PINS; // maximum amount of displayable pins
	PinSquare pins[] = new PinSquare[maxPins]; // pins
	
	private WindowAvatarInformation messagesWindow;
	private Rectangle exitRectangle = new Rectangle(164, 10, 12, 13);
	private Rectangle askFriendRectangle = new Rectangle(20, 199, 144, 15);
	private Rectangle tradeRectangle = new Rectangle(20, 223, 67, 14);
	private Rectangle reportRectangle = new Rectangle(97, 222, 66, 15);
	private Rectangle ignoreRectangle = new Rectangle(19, 245, 67, 14);
	private Rectangle bootRectangle = new Rectangle(97, 246, 65, 13);
	
	private ImageIcon askFriendImageActive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_ask_active.png");
	private ImageIcon askFriendImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_ask_inactive.png");
	private ImageIcon tradeImageActive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_trade_active.png");
	private ImageIcon tradeImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_trade_inactive.png");
	private ImageIcon reportImageActive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_report_active.png");
	private ImageIcon reportImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_report_inactive.png");
	private ImageIcon ignoreImageActive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_ignore_active.png");
	private ImageIcon ignoreImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_ignore_inactive.png");
	private ImageIcon bootImageActive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_boot_active.png");
	private ImageIcon bootImageInactive = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "av_info_boot_inactive.png");
	
	private JLabel askFriendLabel = new JLabel(askFriendImageActive);
	private JLabel tradeLabel = new JLabel(tradeImageActive);
	private JLabel reportLabel = new JLabel(reportImageActive);
	private JLabel ignoreLabel = new JLabel(ignoreImageActive);
	private JLabel bootLabel = new JLabel(bootImageActive);
	
	private boolean canRequestFriends = true; // can ask to be a friend?
	private boolean canTrade = true; // can trade this user?
	private boolean canReport = true; // can report this user?
	private boolean canIgnore = true; // can ignore this user?
	private boolean canBoot = false; // can boot this user?
	
	public WindowAvatarInformation(Font textFont, Font textFontBold, int x, int y)
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
	
	public void setInactive(boolean inactive)
	{
		this.inactive = inactive;
		
		if(inactive)
		{
			// hide all the button images
			askFriendLabel.setVisible(false);
			tradeLabel.setVisible(false);
			reportLabel.setVisible(false);
			ignoreLabel.setVisible(false);
			bootLabel.setVisible(false);
			
			backgroundLabel.setIcon(windowImageInactive);
			repaint();
		}
		else
		{
			// show all the button images
			askFriendLabel.setVisible(true);
			tradeLabel.setVisible(true);
			reportLabel.setVisible(true);
			ignoreLabel.setVisible(true);
			bootLabel.setVisible(true);
			
			backgroundLabel.setIcon(windowImage);
			repaint();
		}
	}
	
	private void loadWindowInventory()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);

		this.setLayout(null);
		this.setBounds(x,y,width,height); // set the bounds
		
		// username display
		System.out.println("Avatar Info Text: " + usernameLabel.getText());
		usernameLabel.setFont(textFontBold);
		usernameLabel.setForeground(Color.white);
		usernameLabel.setBackground(new Color(41, 85, 150));
		usernameLabel.setBounds(24, 5, 133, 15);
		usernameLabel.setHorizontalAlignment(JLabel.CENTER); // center the username
		usernameLabel.setVisible(true);
		add(usernameLabel);
		
		// signature display
		signatureLabel.setFont(textFont);
		signatureLabel.setForeground(Color.white);
		signatureLabel.setBackground(new Color(41, 85, 150));
		signatureLabel.setBounds(10, 55, 153, 64);
		signatureLabel.setVerticalAlignment(JLabel.TOP); // push the signature up to the top of the label
		signatureLabel.setVisible(true);
		add(signatureLabel);
		
		// set up the badge squares
		//badges[0] = new PinSquare("VMK Staff", AppletResourceLoader.getImageFromJar("img/badges/staff_badge.png"));
		//badges[1] = new PinSquare("Development Team", AppletResourceLoader.getImageFromJar("img/badges/dev_team_badge_small.png"));
		//badges[2] = new PinSquare("Here From Day One", AppletResourceLoader.getImageFromJar("img/badges/day_one_badge.png"));
		//badges[3] = new PinSquare("VIP Member", AppletResourceLoader.getImageFromJar("img/badges/vip_badge.png"));
		
		for(int i = 0; i < badges.length; i++)
		{
			badges[i] = new PinSquare("");
			badges[i].setIcon(badges[i].getImage());
			badges[i].setBackground(new Color(41, 85, 150));
			badges[i].setHorizontalAlignment(JLabel.CENTER);
			
			badges[i].setBounds((i * 36) + 10, 25, 36, 32);
			badges[i].addMouseListener(new MouseListener()
			{
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e)
				{
					pinDescriptionLabel.setText(((PinSquare)e.getSource()).getPinName());
					pinDescriptionLabel.setVisible(true);
				}
				public void mouseEntered(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
			});
			
			add(badges[i]);
		}
		
		// set up the pin squares
		for(int i = 0; i < pins.length; i++)
		{
			// set general properties for the pin square
			//pins[i] = new PinSquare("Dancing Inferno Magic Pin", AppletResourceLoader.getImageFromJar("img/pins/magic_pin_dancing_inferno.png"));
			pins[i] = new PinSquare("");
			pins[i].setIcon(pins[i].getImage());
			pins[i].setBackground(new Color(41, 85, 150));
			pins[i].setHorizontalAlignment(JLabel.CENTER);
			
			if(i < (maxPins / 2))
			{
				// top row
				pins[i].setBounds((i * 40) + 10, 110, 40, 34);
				System.out.println("Adding pins... top row");
			}
			else
			{
				// bottom row
				pins[i].setBounds((Math.abs(i - (maxPins / 2)) * 40) + 10, 146, 40, 34);
				System.out.println("Adding pins... bottom row");
			}
			
			pins[i].addMouseListener(new MouseListener()
			{
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e)
				{
					pinDescriptionLabel.setText(((PinSquare)e.getSource()).getPinName());
					pinDescriptionLabel.setVisible(true);
				}
				public void mouseEntered(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
			});
			add(pins[i]);
		}
		
		// pin description
		pinDescriptionLabel.setFont(textFont);
		pinDescriptionLabel.setForeground(Color.white);
		pinDescriptionLabel.setBackground(new Color(41, 85, 150));
		pinDescriptionLabel.setBounds(10, 179, 153, 17);
		pinDescriptionLabel.setVisible(false);
		add(pinDescriptionLabel);
		
		askFriendLabel.setBounds(16, 196, 150, 21);
		add(askFriendLabel);
		
		tradeLabel.setBounds(16, 219, 74, 20);
		add(tradeLabel);
		
		reportLabel.setBounds(92, 219, 74, 20);
		add(reportLabel);
		
		ignoreLabel.setBounds(15, 242, 75, 21);
		add(ignoreLabel);
		
		bootLabel.setBounds(93, 243, 73, 20);
		add(bootLabel);
		
		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		
		System.out.println(usernameLabel.getBounds());
		
		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				if(exitRectangle.contains(e.getPoint()))
				{
					// hide the pin description
					pinDescriptionLabel.setVisible(false);
					
					// close the window
					setVisible(false);
				}
				else if(!inactive && askFriendRectangle.contains(e.getPoint()) && canRequestFriends)
				{
					// clicked inside "Add To Friends List" rectangle
					
					// send a friend request message to the user
					gridObject.sendFriendRequest(username);
					
					// close the window
					setVisible(false);
				}
				else if(!inactive && tradeRectangle.contains(e.getPoint()) && canTrade)
				{
					// clicked inside the "Trade" rectangle
				}
				else if(!inactive && reportRectangle.contains(e.getPoint()) && canReport)
				{
					// clicked inside the "Report" rectangle
				}
				else if(!inactive && ignoreRectangle.contains(e.getPoint()) && canIgnore)
				{
					// clicked inside the "Ignore" rectangle
				}
				else if(!inactive && bootRectangle.contains(e.getPoint()) && canBoot)
				{
					// clicked inside the "Boot" rectangle
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
				// don't allow window movement
			}
		});
		
		messagesWindow = this;
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}
	
	public String getUsername() {return username;}
	
	public void setUsername(String username)
	{
		this.username = username;
		usernameLabel.setText(username);
		//usernameLabel.setLocation(usernameX + (int)(0.75 * usernameLabel.getText().length()), usernameLabel.getY());
		
		// TODO: Grey-out the Ask to Be a Friend button if myCharacter is friends with that character
		// TODO: Check the Friends List component on the Messages window to do that
		if(gridObject.isFriendsWith(username) && !inactive)
		{
			canRequestFriends = false;
			askFriendLabel.setIcon(askFriendImageInactive);
		}
	}
	
	public void setSignature(String signature)
	{
		signatureLabel.setText("<html><p>" + signature + "</p></html>");
	}
	
	// set the badges
	public void setBadges(InventoryInfo[] badges)
	{
		clearPinSquareProperties(this.badges);
		
		for(int i = 0; i < badges.length; i++)
		{
			// make sure the badge content is allowed to be displayed
			if(RatingSystem.isContentAllowed(badges[i].getRatingIndex(), gridObject.getMyCharacter().getContentRatingIndex()))
			{
				this.badges[i].setPinName(badges[i].getName());
				this.badges[i].setImage(badges[i].getPath());
				this.badges[i].setIcon(this.badges[i].getImage());
			}
		}
	}
	
	// set the pins
	public void setPins(InventoryInfo[] pins)
	{
		clearPinSquareProperties(this.pins);
		
		for(int i = 0; i < pins.length; i++)
		{
			// make sure the pin content is allowed to be displayed
			if(RatingSystem.isContentAllowed(pins[i].getRatingIndex(), gridObject.getMyCharacter().getContentRatingIndex()))
			{
				this.pins[i].setPinName(pins[i].getName());
				this.pins[i].setImage(pins[i].getPath());
				this.pins[i].setIcon(this.pins[i].getImage());
			}
		}
	}
	
	// clear the general properties of a set of pin squares
	private void clearPinSquareProperties(PinSquare[] squares)
	{
		for(int i = 0; i < squares.length; i++)
		{
			squares[i].setPinName("");
			squares[i].setImage("");
			squares[i].setIcon(null);
		}
	}
	
	// set how the player can interact with this user
	public void setPermissions(boolean canRequestFriends, boolean canTrade, boolean canReport, boolean canIgnore, boolean canBoot)
	{
		this.canRequestFriends = canRequestFriends;
		this.canTrade = canTrade;
		this.canReport = canReport;
		this.canIgnore = canIgnore;
		this.canBoot = canBoot;
		
		// can request friends?
		if(canRequestFriends)
		{
			askFriendLabel.setIcon(askFriendImageActive);
		}
		else
		{
			askFriendLabel.setIcon(askFriendImageInactive);
		}
		
		// can trade?
		if(canTrade)
		{
			tradeLabel.setIcon(tradeImageActive);
		}
		else
		{
			tradeLabel.setIcon(tradeImageInactive);
		}
		
		// can report?
		if(canReport)
		{
			reportLabel.setIcon(reportImageActive);
		}
		else
		{
			reportLabel.setIcon(reportImageInactive);
		}
		
		// can ignore?
		if(canIgnore)
		{
			ignoreLabel.setIcon(ignoreImageActive);
		}
		else
		{
			ignoreLabel.setIcon(ignoreImageInactive);
		}
		
		// can boot?
		if(canBoot)
		{
			bootLabel.setIcon(bootImageActive);
		}
		else
		{
			bootLabel.setIcon(bootImageInactive);
		}
	}
	
	public void setGridObject(RoomViewerGrid gridObject) {
		this.gridObject = gridObject;
	}
}

// represents a single pin in the window
class PinSquare extends JLabel
{
	private String pinName = "";
	private ImageIcon image = null;
	
	public PinSquare()
	{
		super();
	}
	
	public PinSquare(String pinID)
	{
		super();
		
		// get the pin information
		if(!StaticAppletData.getInvInfo(pinID).getID().equals(""))
		{
			this.pinName = StaticAppletData.getInvInfo(pinID).getName();
			this.image = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(pinID).getPath());
		}
	}
	
	public void setPinName(String pinName) {
		this.pinName = pinName;
	}
	
	public String getPinName() {
		return pinName;
	}
	
	public void setImage(String path)
	{
		if(!path.equals(""))
		{
			this.image = AppletResourceLoader.getImageFromJar(path);
		}
		else
		{
			this.image = null;
		}
	}
	
	public ImageIcon getImage() {
		return image;
	}
}