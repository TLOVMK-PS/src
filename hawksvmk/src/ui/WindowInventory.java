// WindowInventory.java by Matt Fritz
// November 29, 2009
// Handles the "Stuff" (Inventory) window

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.InventoryItem;
import util.StaticAppletData;

public class WindowInventory extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 439;
	private int height = 397;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/inventory.png");
	private ImageIcon inventorySquare = AppletResourceLoader.getImageFromJar("img/ui/inventory_square.png");
	private ImageIcon inventorySquareHighlight = AppletResourceLoader.getImageFromJar("img/ui/inventory_square_highlight.png");
	private ImageIcon inventoryCardImage = AppletResourceLoader.getImageFromJar("img/furniture/card_template.png");
	
	// structure to hold a player's inventory items
	private ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
	private int furnitureCount = 0;
	private int pinCount = 0;
	private int posterCount = 0;
	
	private JLabel inventoryNameLabel = new JLabel("");
	private JLabel inventorySquareHighlightLabel = new JLabel(inventorySquareHighlight);
	private JPanel inventoryPinsPanel = new JPanel();
	private JLabel inventoryCardDisplayLabel = new JLabel(inventoryCardImage);
	private JScrollPane inventoryPinsScrollPane;
	
	private final int INVENTORY_PANEL_WIDTH = 262;
	
	private final int ITEMS_PER_ROW = 6;
	private final int INVENTORY_SQUARE_SPACING = 2;
	private final int PIN_INV_OFFSET_LEFT = 24;
	private final int PIN_INV_OFFSET_TOP = 98;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowInventory messagesWindow;
	private Rectangle titleRectangle = new Rectangle(52, 10, 366, 35);
	private Rectangle exitRectangle = new Rectangle(407, 9, 15, 16);
	
	public WindowInventory(Font textFont, Font textFontBold, int x, int y)
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
		
		this.setLayout(null);
		
		// add the inventory name bar
		inventoryNameLabel.setBounds(45, 72, 345, 16);
		inventoryNameLabel.setBackground(new Color(40, 84, 146));
		inventoryNameLabel.setForeground(Color.white);
		inventoryNameLabel.setFont(textFont);
		inventoryNameLabel.setVerticalAlignment(JLabel.TOP);
		inventoryNameLabel.setHorizontalAlignment(JLabel.CENTER);
		add(inventoryNameLabel);
		
		// add the card display label
		inventoryCardDisplayLabel.setBounds(307, 98, 109, 133);
		add(inventoryCardDisplayLabel);
		
		// panel that holds the pin inventory
		inventoryPinsPanel.setLayout(null);
		inventoryPinsPanel.setBackground(new Color(40, 84, 146));
		
		// add the panel to the scroll pane
		inventoryPinsScrollPane = new JScrollPane(inventoryPinsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inventoryPinsScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		inventoryPinsScrollPane.setBorder(null);
		inventoryPinsScrollPane.setBounds(PIN_INV_OFFSET_LEFT, PIN_INV_OFFSET_TOP, INVENTORY_PANEL_WIDTH + 18, 175);
		add(inventoryPinsScrollPane);
		
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
	
	// set a player's inventory
	public void setInventory(ArrayList<InventoryItem> inventoryItems)
	{
		furnitureCount = 0;
		pinCount = 0;
		posterCount = 0;
		this.inventoryItems = inventoryItems;
		
		int row = 0;
		int col = 0;
		
		int pinPanelHeight = 0;
		
		// add the highlight square
		inventorySquareHighlightLabel.setBounds(0, 0, 42, 42);
		inventorySquareHighlightLabel.setVisible(false);
		inventoryPinsPanel.add(inventorySquareHighlightLabel);
		
		// add the inventory squares
		for(int i = 0; i < inventoryItems.size(); i++)
		{
			// get the inventory item
			InventoryItem invItem = inventoryItems.get(i);
			
			if(invItem.getType() == InventoryItem.FURNITURE)
			{
				col = furnitureCount % ITEMS_PER_ROW; // figure out the column
				row = (int)(furnitureCount / ITEMS_PER_ROW); // figure out the row
				
				// add a furniture item
				
				// increase the furniture count
				furnitureCount++;
			}
			else if(invItem.getType() == InventoryItem.PIN)
			{
				col = pinCount % ITEMS_PER_ROW; // figure out the column
				row = (int)(pinCount / ITEMS_PER_ROW); // figure out the row
				
				// add a pin
				InventoryPinSquare invPin = new InventoryPinSquare(row, col, invItem.getId());
				invPin.setIcon(invPin.getImage());
				invPin.setBounds((INVENTORY_SQUARE_SPACING * col) + (42 * col), (INVENTORY_SQUARE_SPACING * row) + (42 * row), 42, 42);
				invPin.setHorizontalAlignment(JLabel.CENTER);
				invPin.addMouseListener(new MouseListener()
				{
					public void mouseExited(MouseEvent e) {}
					public void mouseReleased(MouseEvent e)
					{
					}
					public void mouseEntered(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseClicked(MouseEvent e)
					{
						// move the inventory highlight square here
						inventorySquareHighlightLabel.setVisible(true);
						inventorySquareHighlightLabel.setLocation(e.getComponent().getLocation());
						
						InventoryPinSquare invSquare = (InventoryPinSquare)e.getComponent();
						
						// update the inventory name bar
						inventoryNameLabel.setText(invSquare.getPinName());
						
						// update the card display label
						inventoryCardDisplayLabel.setIcon(invSquare.getCardImage());
					}
				});
				inventoryPinsPanel.add(invPin);
	
				// add the pin backing
				JLabel invSquare = new JLabel(inventorySquare);
				invSquare.setHorizontalAlignment(JLabel.CENTER);
				invSquare.setBounds((INVENTORY_SQUARE_SPACING * col) + (42 * col), (INVENTORY_SQUARE_SPACING * row) + (42 * row), 42, 42);
				inventoryPinsPanel.add(invSquare);
				
				// figure out the pin panel height
				pinPanelHeight = (INVENTORY_SQUARE_SPACING * row) + (42 * row) + 42;
				
				// increase the pin count
				pinCount++;
			}
			else if(invItem.getType() == InventoryItem.POSTER)
			{
				col = posterCount % ITEMS_PER_ROW; // figure out the column
				row = (int)(posterCount / ITEMS_PER_ROW); // figure out the row
				
				// add a poster item
				
				// increase the poster count
				posterCount++;
			}
		}
		
		// set the size of the pins panel
		inventoryPinsPanel.setPreferredSize(new Dimension(INVENTORY_PANEL_WIDTH, pinPanelHeight));
		inventoryPinsPanel.setBounds(PIN_INV_OFFSET_LEFT, PIN_INV_OFFSET_TOP, INVENTORY_PANEL_WIDTH, pinPanelHeight);
		repaint();
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
}

// internal class that handles displaying the inventory items
class InventoryPinSquare extends JLabel
{
	private int row = 0;
	private int col = 0;
	private String pinName = "";
	private ImageIcon image = null;
	private ImageIcon cardImage = null;
	
	public InventoryPinSquare()
	{
		super();
	}
	
	public InventoryPinSquare(int row, int col, String pinID)
	{
		super();
		
		this.row = row;
		this.col = col;
		
		// get the pin information
		if(!StaticAppletData.getInvInfo(pinID).getID().equals(""))
		{
			this.pinName = StaticAppletData.getInvInfo(pinID).getName();
			this.image = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(pinID).getPath());
			this.cardImage = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(pinID).getCardPath());
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
	
	public ImageIcon getCardImage() {
		return cardImage;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
}
