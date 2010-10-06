// WindowClothing.java by Matt Fritz
// November 29, 2009
// Handles the "Clothing" window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import comparators.InventoryItemNameComparator;

import roomviewer.RoomViewerGrid;
import sockets.messages.MessageAddChatToRoom;
import util.AppletResourceLoader;
import util.InventoryInfo;
import util.InventoryItem;
import util.StaticAppletData;

public class WindowClothing extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 338;
	private int height = 483;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/preferences.png");
	
	int signatureCharacterLimit = 74;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowClothing messagesWindow;
	private Rectangle titleRectangle = new Rectangle(57, 14, 266, 36);
	private Rectangle exitRectangle = new Rectangle(306, 15, 16, 16);
	private Rectangle okRectangle = new Rectangle(35, 446, 199, 14);
	
	private JTextArea signatureBox = new JTextArea("");
	
	private String shirtID = "";
	private String shoesID = "";
	private String pantsID = "";
	private String hatID = "";
	private ArrayList<InventoryItem> shirts = new ArrayList<InventoryItem>();
	private int selectedShirtIndex = 0;
	private ArrayList<InventoryItem> shoes = new ArrayList<InventoryItem>();
	private int selectedShoesIndex = 0;
	private ArrayList<InventoryItem> pants = new ArrayList<InventoryItem>();
	private int selectedPantsIndex = 0;
	private ArrayList<InventoryItem> hats = new ArrayList<InventoryItem>();
	private int selectedHatIndex = 0;
	private ArrayList<InventoryItem> bases = new ArrayList<InventoryItem>();
	private int selectedBaseIndex = 0;
	
	Rectangle hatImageBounds = new Rectangle(52,99,41,41);
	private JLabel hatLabel = new JLabel();
	Rectangle shirtImageBounds = new Rectangle(52,230,41,41);
	private JLabel shirtLabel = new JLabel();
	Rectangle shoesImageBounds = new Rectangle(52,275,41,41);
	private JLabel shoesLabel = new JLabel();
	Rectangle pantsImageBounds = new Rectangle(52,319,41,41);
	private JLabel pantsLabel = new JLabel();
	
	Rectangle prevHatRect = new Rectangle(32,107,17,18);
	Rectangle nextHatRect = new Rectangle(95,107,17,18);
	Rectangle prevShirtRect = new Rectangle(32,241,17,18);
	Rectangle nextShirtRect = new Rectangle(95,241,17,18);
	Rectangle prevPantsRect = new Rectangle(32,285,17,18);
	Rectangle nextPantsRect = new Rectangle(95,285,17,18);
	Rectangle prevShoesRect = new Rectangle(32,329,17,18);
	Rectangle nextShoesRect = new Rectangle(95,329,17,18);
	
	private JLabel basePreviewLabel = new JLabel();
	private JLabel shirtPreviewLabel = new JLabel();
	private JLabel shoesPreviewLabel = new JLabel();
	private JLabel pantsPreviewLabel = new JLabel();
	private JLabel hatPreviewLabel = new JLabel();
	Rectangle characterPreviewRect = new Rectangle(107,111,122,254);
	
	private boolean setCurrentlySelectedClothingFirstTime = false; // TRUE when the selected clothing has been set for the first time
	
	private String username = "";
	private RoomViewerGrid gridObject;
	
	public WindowClothing(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowInventory();
	}
	
	public void setUsername(String username) {this.username = username;}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
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
		
		// selected hat
		hatLabel.setBounds(hatImageBounds);
		hatLabel.setBackground(new Color(40, 86, 146));
		add(hatLabel);
		
		// selected shirt
		shirtLabel.setBounds(shirtImageBounds);
		shirtLabel.setBackground(new Color(40, 86, 146));
		add(shirtLabel);
		
		// selected shoes
		shoesLabel.setBounds(shoesImageBounds);
		shoesLabel.setBackground(new Color(40, 86, 146));
		add(shoesLabel);
		
		// selected pants
		pantsLabel.setBounds(pantsImageBounds);
		pantsLabel.setBackground(new Color(40, 86, 146));
		add(pantsLabel);
		
		// signature box
		signatureBox.setBounds(38, 386, 263, 31);
		signatureBox.setFont(textFont);
		signatureBox.setBackground(new Color(152, 190, 255));
		signatureBox.setForeground(Color.BLACK);
		signatureBox.setBorder(null);
		signatureBox.setLineWrap(true);
		signatureBox.addKeyListener(new KeyListener()
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
				 if(signatureBox.getText().length() > signatureCharacterLimit)
				 {
					 e.consume();
				 }
	    	 }
	    	 public void keyReleased(KeyEvent e)
	    	 {
	    	 }
	     });
		add(signatureBox);
		
		// add the character preview image labels
		
		hatPreviewLabel.setBounds(characterPreviewRect);
		hatPreviewLabel.setBackground(new Color(40, 86, 146));
		add(hatPreviewLabel);
		
		shirtPreviewLabel.setBounds(characterPreviewRect);
		shirtPreviewLabel.setBackground(new Color(40, 86, 146));
		add(shirtPreviewLabel);
		
		shoesPreviewLabel.setBounds(characterPreviewRect);
		shoesPreviewLabel.setBackground(new Color(40, 86, 146));
		add(shoesPreviewLabel);
		
		pantsPreviewLabel.setBounds(characterPreviewRect);
		pantsPreviewLabel.setBackground(new Color(40, 86, 146));
		add(pantsPreviewLabel);
		
		basePreviewLabel.setBounds(characterPreviewRect);
		basePreviewLabel.setBackground(new Color(40, 86, 146));
		add(basePreviewLabel);
		
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
				else if(okRectangle.contains(e.getPoint()))
				{
					// set the shirt information
					if(shirts.size() > 0) {shirtID = shirts.get(selectedShirtIndex).getId();}
					
					// set the shoes information
					if(shoes.size() > 0) {shoesID = shoes.get(selectedShoesIndex).getId();}
					
					// set the pants information
					if(pants.size() > 0) {pantsID = pants.get(selectedPantsIndex).getId();}
					
					// set the hat information
					if(hats.size() > 0) {hatID = hats.get(selectedHatIndex).getId();}
					
					// apply the signature and clothing
					gridObject.updateUserSignatureAndClothing(signatureBox.getText(), shirtID, shoesID, pantsID, hatID);
					
					// close the window
					setVisible(false);
				}
				else if(nextShirtRect.contains(e.getPoint()))
				{
					// show the next shirt
					showClothingItem("shirts","next");
				}
				else if(prevShirtRect.contains(e.getPoint()))
				{
					// show the previous shirt
					showClothingItem("shirts","prev");
				}
				else if(nextShoesRect.contains(e.getPoint()))
				{
					// show the next shoes
					showClothingItem("shoes","next");
				}
				else if(prevShoesRect.contains(e.getPoint()))
				{
					// show the previous shoes
					showClothingItem("shoes","prev");
				}
				else if(nextPantsRect.contains(e.getPoint()))
				{
					// show the next pants
					showClothingItem("pants","next");
				}
				else if(prevPantsRect.contains(e.getPoint()))
				{
					// show the previous pants
					showClothingItem("pants","prev");
				}
				else if(nextHatRect.contains(e.getPoint()))
				{
					// show the next hat
					showClothingItem("hats","next");
				}
				else if(prevHatRect.contains(e.getPoint()))
				{
					// show the previous hat
					showClothingItem("hats","prev");
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
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
		
		// add a blank InventoryItem entry to the hats structure since a hat isn't required
		hats.add(new InventoryItem("","",InventoryItem.CLOTHING));
		
		// add a new base
		bases.add(new InventoryItem("base_0","base_0",InventoryItem.CLOTHING));
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}
	
	public void setSignatureText(String text)
	{
		signatureBox.setText(text);
	}
	
	public String getSignatureText() {return signatureBox.getText();}
	
	// show the desired clothing item from the given collection in the given direction
	private void showClothingItem(String collection, String direction)
	{
		// show a shirt
		if(collection.equals("shirts"))
		{
			// figure out which direction we need to traverse in
			if(direction.equals("prev")) {selectedShirtIndex -= 1;}
			if(direction.equals("next")) {selectedShirtIndex += 1;}
			
			// re-set the selected index if we go either too low or too high
			if(selectedShirtIndex < 0) {selectedShirtIndex = shirts.size() - 1;}
			if(selectedShirtIndex >= shirts.size()) {selectedShirtIndex = 0;}
			
			// set the selected shirt's icon
			shirtLabel.setIcon(getClothingItemIcon(shirts.get(selectedShirtIndex)));
			
			// update the shirt preview
			updateShirtPreview();
		}
		else if(collection.equals("shoes")) // show shoes
		{
			// figure out which direction we need to traverse in
			if(direction.equals("prev")) {selectedShoesIndex -= 1;}
			if(direction.equals("next")) {selectedShoesIndex += 1;}
			
			// re-set the selected index if we go either too low or too high
			if(selectedShoesIndex < 0) {selectedShoesIndex = shoes.size() - 1;}
			if(selectedShoesIndex >= shoes.size()) {selectedShoesIndex = 0;}
			
			// set the selected shoes' icon
			shoesLabel.setIcon(getClothingItemIcon(shoes.get(selectedShoesIndex)));
			
			// update the shoes preview
			updateShoesPreview();
		}
		else if(collection.equals("pants")) // show pants
		{
			// figure out which direction we need to traverse in
			if(direction.equals("prev")) {selectedPantsIndex -= 1;}
			if(direction.equals("next")) {selectedPantsIndex += 1;}
			
			// re-set the selected index if we go either too low or too high
			if(selectedPantsIndex < 0) {selectedPantsIndex = pants.size() - 1;}
			if(selectedPantsIndex >= pants.size()) {selectedPantsIndex = 0;}
			
			// set the selected pants' icon
			pantsLabel.setIcon(getClothingItemIcon(pants.get(selectedPantsIndex)));
			
			// update the pants preview
			updatePantsPreview();
		}
		else if(collection.equals("hats")) // show hats
		{
			// figure out the direction we need to traverse in
			if(direction.equals("prev")) {selectedHatIndex -= 1;}
			if(direction.equals("next")) {selectedHatIndex += 1;}
			
			// re-set the selected index if we go either too low or too high
			if(selectedHatIndex < 0) {selectedHatIndex = hats.size() - 1;}
			if(selectedHatIndex >= hats.size()) {selectedHatIndex = 0;}
			
			// set the selected hat's icon
			hatLabel.setIcon(getClothingItemIcon(hats.get(selectedHatIndex)));
			
			// update the hat preview
			updateHatPreview();
		}
	}
	
	// set the currently-selected clothing images
	public void setCurrentlySelectedClothing()
	{
		// only set the currently-selected clothing if there is nothing there already
		if(setCurrentlySelectedClothingFirstTime) {return;}

		InventoryItem item = null;
		int i = 0;
		
		for(i = 0; i < shirts.size(); i++)
		{
			item = shirts.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getShirtID()))
			{
				shirtLabel.setIcon(getClothingItemIcon(item));
				selectedShirtIndex = i;
				break;
			}
		}
		
		for(i = 0; i < shoes.size(); i++)
		{
			item = shoes.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getShoesID()))
			{
				shoesLabel.setIcon(getClothingItemIcon(item));
				selectedShoesIndex = i;
				break;
			}
		}
		
		for(i = 0; i < pants.size(); i++)
		{
			item = pants.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getPantsID()))
			{
				pantsLabel.setIcon(getClothingItemIcon(item));
				selectedPantsIndex = i;
				break;
			}
		}
		
		for(i = 0; i < hats.size(); i++)
		{
			item = hats.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getHatID()))
			{
				hatLabel.setIcon(getClothingItemIcon(item));
				selectedHatIndex = i;
				break;
			}
		}
		
		// set the selected skin base
		selectedBaseIndex = 0;
		
		setCharacterPreviewImages();
		
		setCurrentlySelectedClothingFirstTime = true;
	}
	
	// set the preview images for the first time
	public void setCharacterPreviewImages()
	{
		updateBasePreview();
		updateShirtPreview();
		updateShoesPreview();
		updatePantsPreview();
		updateHatPreview();
	}
	
	// update the shirt preview image
	private void updateShirtPreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(shirts.get(selectedShirtIndex).getId());
		String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
		
		// set the shirt preview image
		shirtPreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
	}
	
	// update the base preview image
	private void updateBasePreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(bases.get(selectedBaseIndex).getId());
		String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
		
		// set the base preview image
		basePreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
	}
	
	// update the pants preview image
	private void updatePantsPreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(pants.get(selectedPantsIndex).getId());
		String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
		
		// set the pants preview image
		pantsPreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
	}
	
	// update the shoes preview image
	private void updateShoesPreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(shoes.get(selectedShoesIndex).getId());
		String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
		
		// set the hat preview image
		shoesPreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
	}
	
	// update the hat preview image
	private void updateHatPreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(hats.get(selectedHatIndex).getId());
		String path = "";
		
		// check to make sure there's actually an available image
		if(!itemInfo.getPath().equals(""))
		{
			path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
			
			// set the hat preview image
			hatPreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
		}
		else
		{
			// clear the icon
			hatPreviewLabel.setIcon(null);
		}
	}
	
	// add a clothing item to one of the ArrayList structures if it doesn't already contain the item
	public void addClothingItem(InventoryItem item, boolean sortCollections)
	{
		// check to see what type of clothing this item is
		if(item.getId().startsWith("shirt_") && !collectionContains(shirts,item))
		{
			// shirt
			shirts.add(item);
		}
		else if(item.getId().startsWith("shoes_") && !collectionContains(shoes,item))
		{
			// shoes
			shoes.add(item);
		}
		else if(item.getId().startsWith("pants_") && !collectionContains(pants,item))
		{
			// pants
			pants.add(item);
		}
		else if(item.getId().startsWith("hat_") && !collectionContains(hats,item))
		{
			// hat
			hats.add(item);
		}
		
		// check to see if this method was called outside of this window
		if(sortCollections)
		{
			// alphabetize all of the clothing items
			sortClothingItems();
		}
	}
	
	// alphabetize all of the clothing items
	private void sortClothingItems()
	{
		Collections.sort(shirts, new InventoryItemNameComparator());
		Collections.sort(shoes, new InventoryItemNameComparator());
		Collections.sort(pants, new InventoryItemNameComparator());
		Collections.sort(hats, new InventoryItemNameComparator());
	}
	
	// check to see if a given collection contains a specific item
	private boolean collectionContains(ArrayList<InventoryItem> collection, InventoryItem item)
	{
		for(InventoryItem it : collection)
		{
			if(it.getId().equals(item.getId())) {return true;}
		}
		return false;
	}
	
	// get the icon image for an inventory item
	private ImageIcon getClothingItemIcon(InventoryItem item)
	{
		return AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(item.getId()).getIconPath());
	}
}
