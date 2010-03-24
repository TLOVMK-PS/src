// WindowInventory.java by Matt Fritz
// November 29, 2009
// Handles the "Stuff" (Inventory) window

package ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
	
	// structure to hold a player's inventory items
	private ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
	
	private int inventoryRows = 4;
	private int inventoryColumns = 6;
	private final int INVENTORY_SQUARE_SPACING = 2;
	private final int PIN_INV_OFFSET_LEFT = 24;
	private final int PIN_INV_OFFSET_TOP = 98;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowInventory messagesWindow;
	private Rectangle titleRectangle = new Rectangle(52, 10, 366, 35);
	private Rectangle exitRectangle = new Rectangle(403, 11, 14, 14);
	
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
		
		// add the inventory squares
		for(int row = 0; row < inventoryRows; row++)
		{
			for(int col = 0; col < inventoryColumns; col++)
			{
				// add a pin
				InventoryPinSquare invPin = new InventoryPinSquare(row, col, "magic_pin_0");
				invPin.setIcon(invPin.getImage());
				invPin.setBounds(PIN_INV_OFFSET_LEFT + (INVENTORY_SQUARE_SPACING * col) + (42 * col), PIN_INV_OFFSET_TOP + (INVENTORY_SQUARE_SPACING * row) + (42 * row), 42, 42);
				invPin.setHorizontalAlignment(JLabel.CENTER);
				add(invPin);
				
				// add the pin backing
				JLabel invSquare = new JLabel(inventorySquare);
				invSquare.setHorizontalAlignment(JLabel.CENTER);
				invSquare.setBounds(PIN_INV_OFFSET_LEFT + (INVENTORY_SQUARE_SPACING * col) + (42 * col), PIN_INV_OFFSET_TOP + (INVENTORY_SQUARE_SPACING * row) + (42 * row), 42, 42);
				add(invSquare);
			}
		}

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
	public void setInventoryItems(ArrayList<InventoryItem> inventoryItems)
	{
		this.inventoryItems = inventoryItems;
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

// internal class that handles displaying the inventory items
class InventoryPinSquare extends JLabel
{
	private int row = 0;
	private int col = 0;
	private String pinName = "";
	private ImageIcon image = null;
	
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
		if(!StaticAppletData.getPinInfo(pinID).getID().equals(""))
		{
			this.pinName = StaticAppletData.getPinInfo(pinID).getName();
			this.image = AppletResourceLoader.getImageFromJar(StaticAppletData.getPinInfo(pinID).getPath());
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
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
}
