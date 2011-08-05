// WindowClothing.java by Matt Fritz
// November 29, 2009
// Handles the "Clothing" window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
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
import util.GameConstants;
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
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar(GameConstants.PATH_UI_IMAGES + "preferences.png");
	
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
	private String baseID = "";
	private String hairID = "";
	private String eyesID = "";
	private String mouthID = "";
	private String facialhairID = "";
	
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
	private ArrayList<InventoryItem> hair = new ArrayList<InventoryItem>();
	private int selectedHairIndex = 0;
	private ArrayList<InventoryItem> eyes = new ArrayList<InventoryItem>();
	private int selectedEyesIndex = 0;
	private ArrayList<InventoryItem> mouths = new ArrayList<InventoryItem>();
	private int selectedMouthIndex = 0;
	private ArrayList<InventoryItem> facialhair = new ArrayList<InventoryItem>();
	private int selectedFacialhairIndex = 0;
	
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
	Rectangle prevShoesRect = new Rectangle(32,285,17,18);
	Rectangle nextShoesRect = new Rectangle(95,285,17,18);
	Rectangle prevPantsRect = new Rectangle(32,329,17,18);
	Rectangle nextPantsRect = new Rectangle(95,329,17,18);
	
	private JLabel basePreviewLabel = new JLabel();
	private JLabel headPreviewLabel = new JLabel();
	private JLabel hairPreviewLabel = new JLabel();
	private JLabel eyesPreviewLabel = new JLabel();
	private JLabel mouthPreviewLabel = new JLabel();
	private JLabel facialhairPreviewLabel = new JLabel();
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
		
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				loadWindowInventory();
			}
		});
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
		
		// selected pants
		pantsLabel.setBounds(pantsImageBounds);
		pantsLabel.setBackground(new Color(40, 86, 146));
		add(pantsLabel);
		
		// selected shoes
		shoesLabel.setBounds(shoesImageBounds);
		shoesLabel.setBackground(new Color(40, 86, 146));
		add(shoesLabel);
		
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
		
		// add the character preview image labels (layered from top to bottom)
		addClothingPreviewLabel(hatPreviewLabel);
		addClothingPreviewLabel(hairPreviewLabel);
		addClothingPreviewLabel(eyesPreviewLabel);
		addClothingPreviewLabel(facialhairPreviewLabel);
		addClothingPreviewLabel(mouthPreviewLabel);
		addClothingPreviewLabel(headPreviewLabel);
		addClothingPreviewLabel(shirtPreviewLabel);
		addClothingPreviewLabel(pantsPreviewLabel);
		addClothingPreviewLabel(shoesPreviewLabel);
		addClothingPreviewLabel(basePreviewLabel);
		
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
					// set the base information
					if(bases.size() > 0) {baseID = bases.get(selectedBaseIndex).getId();}
					
					// set the hair information
					if(hair.size() > 0) {hairID = hair.get(selectedHairIndex).getId();}
					
					// set the eyes information
					if(eyes.size() > 0) {eyesID = eyes.get(selectedEyesIndex).getId();}
					
					// set the mouth information
					if(mouths.size() > 0) {mouthID = mouths.get(selectedMouthIndex).getId();}
					
					// set the facial-hair information
					if(facialhair.size() > 0) {facialhairID = facialhair.get(selectedFacialhairIndex).getId();}
					
					// set the shirt information
					if(shirts.size() > 0) {shirtID = shirts.get(selectedShirtIndex).getId();}
					
					// set the shoes information
					if(shoes.size() > 0) {shoesID = shoes.get(selectedShoesIndex).getId();}
					
					// set the pants information
					if(pants.size() > 0) {pantsID = pants.get(selectedPantsIndex).getId();}
					
					// set the hat information
					if(hats.size() > 0) {hatID = hats.get(selectedHatIndex).getId();}
					
					// close the window
					setVisible(false);
					
					// apply the signature and clothing
					gridObject.updateUserSignatureAndClothing(signatureBox.getText(), baseID, hairID, eyesID, mouthID, facialhairID, shirtID, shoesID, pantsID, hatID);
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
		
		// add a blank InventoryItem entry to the facialhair structure since a beard isn't required
		facialhair.add(new InventoryItem("","",InventoryItem.CLOTHING));
		
		// add all the base avatar elements
		addBaseAvatarElements(GameConstants.CONST_BASES_TOTAL, GameConstants.CONST_BASE_COLORS_TOTAL, bases, "base");
		addBaseAvatarElements(GameConstants.CONST_HAIR_TOTAL, GameConstants.CONST_HAIR_COLORS_TOTAL, hair, "hair");
		addBaseAvatarElements(GameConstants.CONST_EYES_TOTAL, GameConstants.CONST_EYE_COLORS_TOTAL, eyes, "eyes");
		addBaseAvatarElements(GameConstants.CONST_MOUTHS_TOTAL, GameConstants.CONST_MOUTH_COLORS_TOTAL, mouths, "mouth");
		addBaseAvatarElements(GameConstants.CONST_FACIALHAIR_TOTAL, GameConstants.CONST_FACIALHAIR_COLORS_TOTAL, facialhair, "facialhair");
	}
	
	// add a set of base avatar elements (bases, hair, eyes, mouths, facial-hair) to their corresponding data structure
	// using the total number of elements, total number of element colors, the list structure, and the String type
	private void addBaseAvatarElements(int elementTotal, int elementColorsTotal, ArrayList<InventoryItem> elements, String elementType)
	{
		int i = 0;
		int j = 0;
		for(i = 0; i < elementTotal; i++)
		{
			for(j = 0; j < elementColorsTotal; j++)
			{
				elements.add(new InventoryItem(elementType + "_" + i + "_" + j, elementType + "_" + i + "_" + j, InventoryItem.CLOTHING));
			}
		}
	}
	
	// add the clothing preview image label to the content area
	private void addClothingPreviewLabel(JLabel previewLabel)
	{
		previewLabel.setBounds(characterPreviewRect);
		previewLabel.setBackground(new Color(40, 86, 146));
		add(previewLabel);
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
			updatePreviewImage(shirts, selectedShirtIndex, shirtPreviewLabel);
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
			updatePreviewImage(shoes, selectedShoesIndex, shoesPreviewLabel);
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
			updatePreviewImage(pants, selectedPantsIndex, pantsPreviewLabel);
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
			updatePreviewImage(hats, selectedHatIndex, hatPreviewLabel);
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
		
		for(i = 0; i < bases.size(); i++)
		{
			item = bases.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getBaseAvatarID()))
			{
				//baseLabel.setIcon(getClothingItemIcon(item));
				selectedBaseIndex = i;
				break;
			}
		}
		
		for(i = 0; i < hair.size(); i++)
		{
			item = hair.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getHairID()))
			{
				//hairLabel.setIcon(getClothingItemIcon(item));
				selectedHairIndex = i;
				break;
			}
		}
		
		for(i = 0; i < eyes.size(); i++)
		{
			item = eyes.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getEyesID()))
			{
				//eyesLabel.setIcon(getClothingItemIcon(item));
				selectedEyesIndex = i;
				break;
			}
		}
		
		for(i = 0; i < mouths.size(); i++)
		{
			item = mouths.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getMouthID()))
			{
				//mouthLabel.setIcon(getClothingItemIcon(item));
				selectedMouthIndex = i;
				break;
			}
		}
		
		for(i = 0; i < facialhair.size(); i++)
		{
			item = facialhair.get(i);
			if(item.getId().equals(gridObject.getMyCharacter().getFacialhairID()))
			{
				//facialhairLabel.setIcon(getClothingItemIcon(item));
				selectedFacialhairIndex = i;
				break;
			}
		}
		
		// set the preview images for the resolved elements
		setCharacterPreviewImages();
		
		setCurrentlySelectedClothingFirstTime = true;
	}
	
	// set the preview images for the first time
	public void setCharacterPreviewImages()
	{
		// a base-image update is a special case since the image of the head relies on
		// the ID of the base body image
		updateBasePreview();
		
		// update the rest of the avatar's previews
		updatePreviewImage(hair, selectedHairIndex, hairPreviewLabel);
		updatePreviewImage(eyes, selectedEyesIndex, eyesPreviewLabel);
		updatePreviewImage(mouths, selectedMouthIndex, mouthPreviewLabel);
		updatePreviewImage(facialhair, selectedFacialhairIndex, facialhairPreviewLabel);
		updatePreviewImage(shirts, selectedShirtIndex, shirtPreviewLabel);
		updatePreviewImage(shoes, selectedShoesIndex, shoesPreviewLabel);
		updatePreviewImage(pants, selectedPantsIndex, pantsPreviewLabel);
		updatePreviewImage(hats, selectedHatIndex, hatPreviewLabel);
	}
	
	// update the preview image for a clothing item given the list of items to use, the selected item's
	// index in the list, and the preview label that will be used to show the image
	private void updatePreviewImage(ArrayList<InventoryItem> items, int itemIndex, JLabel previewLabel)
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(items.get(itemIndex).getId());
		
		// make sure the item should actually be shown
		if(!itemInfo.getPath().equals(""))
		{
			String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
			
			// set the preview image on the label
			previewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
		}
		else
		{
			// clear the preview image
			previewLabel.setIcon(null);
		}
	}

	// update the base preview image
	private void updateBasePreview()
	{
		InventoryInfo itemInfo = StaticAppletData.getInvInfo(bases.get(selectedBaseIndex).getId());
		String path = itemInfo.getPath() + itemInfo.getID() + "_se_64.png";
		
		// set the base preview image
		basePreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
		
		// set the head preview image from the base image for consistency
		path = path.replaceFirst("base", "heads");
		path = path.replaceAll("base", "head");
		headPreviewLabel.setIcon(new ImageIcon(AppletResourceLoader.getBufferedImageFromJar(path).getScaledInstance(characterPreviewRect.width, characterPreviewRect.height, Image.SCALE_DEFAULT)));
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
