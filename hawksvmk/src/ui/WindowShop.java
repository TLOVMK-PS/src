// WindowShop.java by Matt Fritz
// November 29, 2009
// Handles the "Shop" window

package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import roomviewer.RoomViewerGrid;

import util.AppletResourceLoader;
import util.FileOperations;
import util.InventoryItem;
import util.StaticAppletData;

public class WindowShop extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 559;
	private int height = 405;
	private ImageIcon tabRoomsImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_rooms.png");
	private ImageIcon windowImage = tabRoomsImage;
	
	private ImageIcon inventorySquare = AppletResourceLoader.getImageFromJar("img/ui/inventory_square.png");
	private ImageIcon shopSquareHighlight = AppletResourceLoader.getImageFromJar("img/ui/inventory_square_highlight.png");
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowShop shopWindow;
	private Rectangle titleRectangle = new Rectangle(53, 11, 491, 38);
	private Rectangle exitRectangle = new Rectangle(529, 14, 16, 14);
	
	private JPanel roomCreatedWindow = new JPanel();
	private JLabel roomCreatedName = new JLabel("");
	private JLabel roomCreatedOwner = new JLabel("");
	private JLabel roomCreatedDescription = new JLabel("");
	private JLabel roomCreatedWindowBackground = new JLabel(AppletResourceLoader.getImageFromJar("img/ui/room_created_window.png"));
	private Rectangle continueRectangle = new Rectangle(12, 288, 128, 20);
	private Rectangle enterRoomRectangle = new Rectangle(145, 288, 128, 19);
	
	private HashMap<String,String> roomInfo = new HashMap<String,String>();
	private Rectangle tabRooms = new Rectangle(38, 46, 49, 22);
	private int selectedRoomPreview = -1;
	private JLabel roomPreviewLabel = new JLabel();
	private JLabel roomNameLabel = new JLabel();
	private JLabel roomDescriptionLabel = new JLabel();
	private ArrayList<String> roomTemplates = new ArrayList<String>();
	private Rectangle btnBuyRoom = new Rectangle(406, 365, 71, 21);
	private Rectangle prevRoom = new Rectangle(26, 345, 66, 18);
	private Rectangle nextRoom = new Rectangle(292, 345, 48, 18);
	private JLabel roomCostLabel = new JLabel();
	private JLabel myCreditsLabel = new JLabel();
	
	private ImageIcon tabFurnishingsImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_furnishings.png");
	private ImageIcon tabPinsImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_pins.png");
	private ImageIcon tabClothingImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_clothing.png");
	private ImageIcon tabPostersImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_posters.png");
	private ImageIcon tabSpecialsImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_specials.png");
	
	private Rectangle tabFurnishings = new Rectangle(99, 46, 75, 21);
	private Rectangle tabPins = new Rectangle(190, 46, 33, 21);
	private Rectangle tabClothing = new Rectangle(239, 46, 57, 21);
	private Rectangle tabPosters = new Rectangle(311, 47, 50, 22);
	private Rectangle tabSpecials = new Rectangle(377, 46, 54, 20);
	
	private ImageIcon shopSmallWorldImg = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_small_world.jpg");
	private ImageIcon shopEmporiumImg = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_emporium.jpg");
	private ImageIcon shopShrunkenNedImg = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_shrunken_ned.jpg");
	private ImageIcon shopGoldenHorseshoeImg = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_golden_horseshoe.jpg");
	private ImageIcon shopStarTradersImg = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_inner_space.jpg");
	private ImageIcon shopSmallWorldImgLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_small_world_lit.jpg");
	private ImageIcon shopEmporiumImgLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_emporium_lit.jpg");
	private ImageIcon shopShrunkenNedImgLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_shrunken_ned_lit.jpg");
	private ImageIcon shopGoldenHorseshoeImgLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_golden_horseshoe_lit.jpg");
	private ImageIcon shopStarTradersImgLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_category_inner_space_lit.jpg");
	
	private JLabel shopSmallWorld = new JLabel(shopSmallWorldImg);
	private JLabel shopEmporium = new JLabel(shopEmporiumImg);
	private JLabel shopShrunkenNed = new JLabel(shopShrunkenNedImg);
	private JLabel shopGoldenHorseshoe = new JLabel(shopGoldenHorseshoeImg);
	private JLabel shopStarTraders = new JLabel(shopStarTradersImg);
	
	private ImageIcon shopCardImage = AppletResourceLoader.getImageFromJar("img/furniture/card_template.png");
	private JLabel shopCardLabel = new JLabel(shopCardImage);
	
	private JLabel shopSquareHighlightLabel = new JLabel(shopSquareHighlight);
	private JPanel shopItemsPanel = new JPanel();
	private JScrollPane shopItemsScrollPane;
	
	private ImageIcon shopAdventurelandItems = AppletResourceLoader.getImageFromJar("img/ui/shopping_adventureland_items.jpg");
	private ImageIcon shopFantasylandItems = AppletResourceLoader.getImageFromJar("img/ui/shopping_fantasyland_items.jpg");
	private ImageIcon shopFrontierlandItems = AppletResourceLoader.getImageFromJar("img/ui/shopping_frontierland_items.jpg");
	private ImageIcon shopMainStreetItems = AppletResourceLoader.getImageFromJar("img/ui/shopping_main_street_items.jpg");
	private ImageIcon shopTomorrowlandItems = AppletResourceLoader.getImageFromJar("img/ui/shopping_tomorrowland_items.jpg");
	private JLabel shopItemsLand = new JLabel(shopMainStreetItems);
	
	private JLabel shopItemName = new JLabel();
	private JLabel shopItemPrice = new JLabel();
	
	private ImageIcon shopItemBuyImage = AppletResourceLoader.getImageFromJar("img/ui/shopping_buy_button_off.png");
	private ImageIcon shopItemBuyImageLit = AppletResourceLoader.getImageFromJar("img/ui/shopping_buy_button_on.png");
	
	private JLabel shopItemBuyBtn = new JLabel(shopItemBuyImage);
	
	// the respective shop IDs to be used with the creation of the items lists and shop mappings
	private String emporiumID = "Emporium";
	private String smallWorldID = "SmallWorldImports";
	private String shrunkenNedID = "ShrunkenNedsShop";
	private String goldenHorseshoeID = "GoldenHorseshoe";
	private String innerSpaceID = "InnerSpaceShop";
	
	private String selectedShop = emporiumID;
	private String selectedTab = "furniture";
	private ShopItemSquare selectedItem = null;
	
	// TODO: Add the proper room IDs for the existing blank Strings
	// the room IDs of the respective shops (for usage in conjunction with the Specials tab)
	private String emporiumRoomID = "ms10";
	private String smallWorldRoomID = "";
	private String shrunkenNedRoomID = "";
	private String goldenHorseshoeRoomID = "";
	private String innerSpaceRoomID = "";
	
	// structure containing the shops and their items
	private HashMap<String, HashMap<String, ArrayList<InventoryItem>>> shops = new HashMap<String, HashMap<String, ArrayList<InventoryItem>>>();
	
	public WindowShop(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowShop();
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
	
	// create the mappings for all the shops
	private void createShops()
	{
		// Emporium
		shops.put(emporiumID, FileOperations.loadShopMappings(emporiumID));
		
		// Shrunken Ned's Shop
		shops.put(shrunkenNedID, FileOperations.loadShopMappings(shrunkenNedID));
		
		// Inner-Space Shop
		shops.put(innerSpaceID, FileOperations.loadShopMappings(innerSpaceID));
		
		// "it's a small world" Imports
		shops.put(smallWorldID, FileOperations.loadShopMappings(smallWorldID));
		
		// Golden Horseshoe Mercantile
		shops.put(goldenHorseshoeID, FileOperations.loadShopMappings(goldenHorseshoeID));
	}
	
	private void loadWindowShop()
	{
		// create the shop data structures
		createShops();
		
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		setLayout(null);
		
		// Room Created window
		roomCreatedWindow.setDoubleBuffered(false);
		roomCreatedWindow.setOpaque(false);
		roomCreatedWindow.setLayout(null);
		roomCreatedWindow.setBounds(150, 40, 283, 323);
		
		roomCreatedName.setBounds(53, 105, 222, 33);
		roomCreatedName.setForeground(Color.white);
		roomCreatedName.setBackground(new Color(0, 32, 83));
		roomCreatedName.setFont(textFont);
		roomCreatedName.setHorizontalAlignment(JLabel.LEFT);
		roomCreatedName.setVerticalAlignment(JLabel.TOP);
		roomCreatedWindow.add(roomCreatedName);
		
		roomCreatedOwner.setBounds(58, 138, 218, 16);
		roomCreatedOwner.setForeground(Color.white);
		roomCreatedOwner.setBackground(new Color(0, 32, 83));
		roomCreatedOwner.setFont(textFont);
		roomCreatedOwner.setHorizontalAlignment(JLabel.LEFT);
		roomCreatedOwner.setVerticalAlignment(JLabel.TOP);
		roomCreatedWindow.add(roomCreatedOwner);
		
		roomCreatedDescription.setBounds(94, 161, 182, 117);
		roomCreatedDescription.setForeground(Color.white);
		roomCreatedDescription.setBackground(new Color(0, 32, 83));
		roomCreatedDescription.setFont(textFont);
		roomCreatedDescription.setHorizontalAlignment(JLabel.LEFT);
		roomCreatedDescription.setVerticalAlignment(JLabel.TOP);
		roomCreatedWindow.add(roomCreatedDescription);
		
		roomCreatedWindowBackground.setBounds(0,0,283,323);
		roomCreatedWindow.add(roomCreatedWindowBackground);
		
		roomCreatedWindow.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(continueRectangle.contains(e.getPoint()))
				{
					roomCreatedWindow.setVisible(false);
				}
				else if(enterRoomRectangle.contains(e.getPoint()))
				{
					// enter the newly-created room
					roomCreatedWindow.setVisible(false);
					shopWindow.setVisible(false);
					gridObject.changeRoom(roomInfo.get("ID")); // the ID is bounced back from the server message
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		roomCreatedWindow.setVisible(false);
		add(roomCreatedWindow);
		
		// ================================
		// ROOMS TAB
		// ================================
		
		// get the list of template IDs for the guest rooms
		roomTemplates = StaticAppletData.getGuestRoomTemplates();
		
		// Room preview label
		roomPreviewLabel.setBounds(30, 103, 306, 234);
		add(roomPreviewLabel);
		
		// Room name label
		roomNameLabel.setBounds(373, 119, 132, 36);
		roomNameLabel.setForeground(Color.white);
		roomNameLabel.setBackground(new Color(40, 86, 146));
		roomNameLabel.setFont(textFont);
		roomNameLabel.setHorizontalAlignment(JLabel.CENTER);
		roomNameLabel.setVerticalAlignment(JLabel.BOTTOM);
		add(roomNameLabel);
		
		// Room description label
		roomDescriptionLabel.setBounds(372, 162, 135, 96);
		roomDescriptionLabel.setForeground(Color.white);
		roomDescriptionLabel.setBackground(new Color(40, 86, 146));
		roomDescriptionLabel.setFont(textFont);
		roomDescriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		roomDescriptionLabel.setVerticalAlignment(JLabel.TOP);
		add(roomDescriptionLabel);
		
		// Room cost label
		roomCostLabel.setBounds(72, 367, 76, 14);
		roomCostLabel.setForeground(Color.white);
		roomCostLabel.setBackground(new Color(3, 44, 100));
		roomCostLabel.setFont(textFont);
		roomCostLabel.setHorizontalAlignment(JLabel.CENTER);
		roomCostLabel.setVerticalAlignment(JLabel.CENTER);
		add(roomCostLabel);
		
		// My Credits label
		myCreditsLabel.setBounds(252, 367, 76, 14);
		myCreditsLabel.setForeground(Color.white);
		myCreditsLabel.setBackground(new Color(48, 144, 195));
		myCreditsLabel.setFont(textFont);
		myCreditsLabel.setHorizontalAlignment(JLabel.CENTER);
		myCreditsLabel.setVerticalAlignment(JLabel.CENTER);
		add(myCreditsLabel);
		
		// get the room preview at index 0
		traverseRoomTemplates("next");
		
		// ==================================
		// GENERAL SHOP SELECTORS
		// ==================================
		
		// "it's a small world" Imports
		shopSmallWorld.setBounds(30, 85, 187, 21);
		shopSmallWorld.setVisible(false);
		shopSmallWorld.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// dim the shop selectors
				dimShopSelectors();
				
				// highlight this selector
				shopSmallWorld.setIcon(shopSmallWorldImgLit);
				
				// set the land
				shopItemsLand.setIcon(shopFantasylandItems);
				
				// create the items panel
				selectedShop = smallWorldID;
				createItemsPanel(selectedShop,selectedTab);
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopSmallWorld);
		
		// Emporium
		shopEmporium.setBounds(30, 106, 187, 21);
		shopEmporium.setVisible(false);
		shopEmporium.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// dim the shop selectors
				dimShopSelectors();
				
				// highlight this selector
				shopEmporium.setIcon(shopEmporiumImgLit);
				
				// set the land
				shopItemsLand.setIcon(shopMainStreetItems);
				
				// create the items panel
				selectedShop = emporiumID;
				createItemsPanel(selectedShop,selectedTab);
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopEmporium);
		
		// Golden Horseshoe Mercantile
		shopGoldenHorseshoe.setBounds(30, 127, 187, 21);
		shopGoldenHorseshoe.setVisible(false);
		shopGoldenHorseshoe.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// dim the shop selectors
				dimShopSelectors();
				
				// highlight this selector
				shopGoldenHorseshoe.setIcon(shopGoldenHorseshoeImgLit);
				
				// set the land
				shopItemsLand.setIcon(shopFrontierlandItems);
				
				// create the items panel
				selectedShop = goldenHorseshoeID;
				createItemsPanel(selectedShop,selectedTab);
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopGoldenHorseshoe);
		
		// Inner-Space Shop
		shopStarTraders.setBounds(30, 148, 187, 21);
		shopStarTraders.setVisible(false);
		shopStarTraders.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// dim the shop selectors
				dimShopSelectors();
				
				// highlight this selector
				shopStarTraders.setIcon(shopStarTradersImgLit);
				
				// set the land
				shopItemsLand.setIcon(shopTomorrowlandItems);
				
				// create the items panel
				selectedShop = innerSpaceID;
				createItemsPanel(selectedShop,selectedTab);
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopStarTraders);
		
		// Shrunken Ned's Shop
		shopShrunkenNed.setBounds(30, 169, 187, 21);
		shopShrunkenNed.setVisible(false);
		shopShrunkenNed.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// dim the shop selectors
				dimShopSelectors();
				
				// highlight this selector
				shopShrunkenNed.setIcon(shopShrunkenNedImgLit);
				
				// set the land
				shopItemsLand.setIcon(shopAdventurelandItems);
				
				// create the items panel
				selectedShop = shrunkenNedID;
				createItemsPanel(selectedShop,selectedTab);
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopShrunkenNed);
		
		// ==================================
		// GENERAL SHOP INTERFACE CONTROLS
		// ==================================
		
		shopCardLabel.setBounds(256, 148, 109, 133);
		shopCardLabel.setVisible(false);
		add(shopCardLabel);
		
		shopItemsPanel.setBounds(0,0,132,190);
		shopItemsPanel.setPreferredSize(new Dimension(132, 190));
		shopItemsPanel.setBackground(new Color(0, 29, 85));
		shopItemsPanel.setLayout(null);
		shopItemsScrollPane = new JScrollPane(shopItemsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		shopItemsScrollPane.setBackground(new Color(0, 29, 85));
		shopItemsScrollPane.setBorder(null);
		shopItemsScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		shopItemsScrollPane.setBounds(389, 148, 147, 190);
		shopItemsScrollPane.setVisible(false);
		add(shopItemsScrollPane);
		
		shopItemsLand.setBounds(311, 85, 165, 33);
		shopItemsLand.setVisible(false);
		add(shopItemsLand);
		
		// item name
		shopItemName.setBounds(245, 120, 282, 20);
		shopItemName.setText("");
		shopItemName.setFont(textFont);
		shopItemName.setForeground(Color.WHITE);
		shopItemName.setHorizontalAlignment(JLabel.CENTER);
		shopItemName.setVisible(false);
		add(shopItemName);
		
		// item price
		shopItemPrice.setBounds(254, 341, 119, 15);
		shopItemPrice.setBackground(new Color(51, 140, 196));
		shopItemPrice.setText("");
		shopItemPrice.setFont(textFont);
		shopItemPrice.setForeground(Color.WHITE);
		shopItemPrice.setHorizontalAlignment(JLabel.CENTER);
		shopItemPrice.setVisible(false);
		add(shopItemPrice);
		
		shopItemBuyBtn.setBounds(248, 364, 131, 21);
		shopItemBuyBtn.setVisible(false);
		shopItemBuyBtn.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				// make sure the Buy button is lit
				if(shopItemBuyBtn.getIcon().equals(shopItemBuyImageLit))
				{
					// parse out the item price
					long itemPrice = Long.parseLong(shopItemPrice.getText().replaceAll(" Credit", ""));
					
					// check to make sure the player can afford the item
					if(gridObject.getMyCredits() >= itemPrice)
					{
						// create the new item
						InventoryItem item = new InventoryItem(selectedItem.getItemName(), selectedItem.getItemID(), selectedItem.getItemType());
						
						// send the message to the server
						gridObject.sendAddInventoryMessage(item);
						
						// subtract the cost of the room from the player's credits
						gridObject.setMyCredits(gridObject.getMyCredits() - itemPrice);
						myCreditsLabel.setText("" + gridObject.getMyCredits());
						
						// update the character file
						gridObject.sendUpdateCharacterMessage(gridObject.getMyCharacter());
					}
					else
					{
						// TODO: Display some notification that the player is poor as cat shit
					}
				}
			}
			public void mousePressed(MouseEvent e) {}
		});
		add(shopItemBuyBtn);

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
				else if(nextRoom.contains(e.getPoint()) && backgroundLabel.getIcon().equals(tabRoomsImage))
				{
					// display the next room
					traverseRoomTemplates("next");
				}
				else if(prevRoom.contains(e.getPoint()) && backgroundLabel.getIcon().equals(tabRoomsImage))
				{
					// display the previous room
					traverseRoomTemplates("prev");
				}
				else if(btnBuyRoom.contains(e.getPoint()) && backgroundLabel.getIcon().equals(tabRoomsImage))
				{
					// check to make sure the player can afford the room
					if(gridObject.getMyCredits() >= Long.parseLong(roomCostLabel.getText()))
					{
						// buy the room
						//System.out.println("You chose to buy room template: " + roomTemplates.get(selectedRoomPreview));
						
						// create a new guest room by sending a "create guest room" message with mock data
						roomInfo.put("TEMPLATE", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomPath());
						roomInfo.put("NAME", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomName());
						roomInfo.put("OWNER", gridObject.getMyCharacter().getUsername());
						roomInfo.put("DESCRIPTION", StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomDescription());
						roomInfo.put("TIMESTAMP", "" + System.currentTimeMillis()/1000);
						
						// send the message to the server
						gridObject.sendCreateGuestRoomMessage(roomInfo);
						
						// subtract the cost of the room from the player's credits
						gridObject.setMyCredits(gridObject.getMyCredits() - Long.parseLong(roomCostLabel.getText()));
						myCreditsLabel.setText("" + gridObject.getMyCredits());
						
						// pop-up the notification
						changeRoomCreatedInfo(roomInfo.get("NAME"), roomInfo.get("OWNER"), roomInfo.get("DESCRIPTION"));
						roomCreatedWindow.setVisible(true);
					}
					else
					{
						// TODO: Display some notification that the player is poor as cat shit
					}
				}
				else if(tabRooms.contains(e.getPoint()))
				{
					// change to the Rooms tab
					changeTab("rooms");
				}
				else if(tabFurnishings.contains(e.getPoint()))
				{
					// change to the Furnishings tab
					changeTab("furnishings");
				}
				else if(tabPins.contains(e.getPoint()))
				{
					// change to the Pins tab
					changeTab("pins");
				}
				else if(tabClothing.contains(e.getPoint()))
				{
					// change to the Clothing tab
					changeTab("clothing");
				}
				else if(tabPosters.contains(e.getPoint()))
				{
					// change to the Posters tab
					changeTab("posters");
				}
				else if(tabSpecials.contains(e.getPoint()))
				{
					// change to the Specials tab
					changeTab("specials");
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
					shopWindow.setLocation(mouseX, mouseY);
					//repaint();
				}
			}
		});
		
		shopWindow = this;
	}
	
	// change the visible tab in the window
	private void changeTab(String tab)
	{
		// hide all the components
		hideComponents();
		
		// figure out which tab to show
		if(tab.equals("furnishings"))
		{
			// show the Furnishings panel
			backgroundLabel.setIcon(tabFurnishingsImage);
			showShopSelectors();
			myCreditsLabel.setLocation(415, 365);
			myCreditsLabel.setVisible(true);
			
			// create the items panel
			selectedTab = "furniture";
			createItemsPanel(selectedShop,selectedTab);
		}
		else if(tab.equals("pins"))
		{
			// show the Pins panel
			backgroundLabel.setIcon(tabPinsImage);
			showShopSelectors();
			myCreditsLabel.setLocation(415, 365);
			myCreditsLabel.setVisible(true);
			
			// create the items panel
			selectedTab = "pins";
			createItemsPanel(selectedShop,selectedTab);
		}
		else if(tab.equals("clothing"))
		{
			// show the Clothing panel
			backgroundLabel.setIcon(tabClothingImage);
			showShopSelectors();
			myCreditsLabel.setLocation(415, 365);
			myCreditsLabel.setVisible(true);
			
			// create the items panel
			selectedTab = "clothing";
			createItemsPanel(selectedShop,selectedTab);
		}
		else if(tab.equals("posters"))
		{
			// show the Posters panel
			backgroundLabel.setIcon(tabPostersImage);
			showShopSelectors();
			myCreditsLabel.setLocation(415, 365);
			myCreditsLabel.setVisible(true);
			
			// create the items panel
			selectedTab = "posters";
			createItemsPanel(selectedShop,selectedTab);
		}
		else if(tab.equals("specials"))
		{
			// show the Specials panel
			backgroundLabel.setIcon(tabSpecialsImage);
			showShopSelectors();
			myCreditsLabel.setLocation(415, 365);
			myCreditsLabel.setVisible(true);
			
			// create the items panel
			selectedTab = "specials";
			createItemsPanel(selectedShop,selectedTab);
		}
		else if(tab.equals("rooms"))
		{
			// show the Rooms panel
			backgroundLabel.setIcon(tabRoomsImage);
			roomPreviewLabel.setVisible(true);
			roomNameLabel.setVisible(true);
			roomDescriptionLabel.setVisible(true);
			roomCostLabel.setVisible(true);
			myCreditsLabel.setLocation(252, 367);
			myCreditsLabel.setVisible(true);
		}
		
		// show the background
		backgroundLabel.setVisible(true);
	}
	
	// show the shop selectors and related components
	private void showShopSelectors()
	{
		shopSmallWorld.setVisible(true);
		shopEmporium.setVisible(true);
		shopShrunkenNed.setVisible(true);
		shopStarTraders.setVisible(true);
		shopGoldenHorseshoe.setVisible(true);
		
		shopCardLabel.setVisible(true);
		shopItemsScrollPane.setVisible(true);
		shopItemsLand.setVisible(true);
		shopItemName.setVisible(true);
		shopItemPrice.setVisible(true);
		shopItemBuyBtn.setVisible(true);
	}
	
	// dim the shop selector highlights
	private void dimShopSelectors()
	{
		shopSmallWorld.setIcon(shopSmallWorldImg);
		shopEmporium.setIcon(shopEmporiumImg);
		shopShrunkenNed.setIcon(shopShrunkenNedImg);
		shopStarTraders.setIcon(shopStarTradersImg);
		shopGoldenHorseshoe.setIcon(shopGoldenHorseshoeImg);
	}
	
	// apply the shop items to the item panel
	private void createItemsPanel(String shop, String type)
	{
		// get the current room ID
		String currentRoomID = gridObject.getRoomInfo().get("ID");
		
		// clear the items panel
		shopItemsPanel.removeAll();
		
		// clear the selected item
		selectedItem = null;
		shopItemName.setText("");
		shopItemPrice.setText("");
		shopCardLabel.setIcon(shopCardImage);
		shopItemBuyBtn.setIcon(shopItemBuyImage);
		
		// check to see if it's the Specials tab first
		if(type.equals("specials"))
		{
			// check to see if we need to prevent the creation of the items list
			if(shop.equals(emporiumID) && !currentRoomID.equals(emporiumRoomID)) {return;}
			if(shop.equals(smallWorldID) && !currentRoomID.equals(smallWorldRoomID)) {return;}
			if(shop.equals(shrunkenNedID) && !currentRoomID.equals(shrunkenNedRoomID)) {return;}
			if(shop.equals(goldenHorseshoeID) && !currentRoomID.equals(goldenHorseshoeRoomID)) {return;}
			if(shop.equals(innerSpaceID) && !currentRoomID.equals(innerSpaceRoomID)) {return;}
		}
		
		// get the shop items
		ArrayList<InventoryItem> shopItems = shops.get(shop).get(type);
		
		// add the highlight square
		shopSquareHighlightLabel.setBounds(0, 0, 42, 42);
		shopSquareHighlightLabel.setVisible(false);
		shopItemsPanel.add(shopSquareHighlightLabel);
		
		final int ITEMS_PER_ROW = 3;
		final int ITEMS_PER_COLUMN = 3;
		final int ITEM_SPACING = 2;
		final int ITEM_HEIGHT = 42;
		final int ITEM_WIDTH = 42;
		int itemsPanelHeight = 0;
		int itemCount = 0;
		int itemRow = 0;
		int itemCol = 0;
		
		for(int i = 0; i < shopItems.size(); i++)
		{
			itemRow = itemCount / ITEMS_PER_ROW;
			itemCol = itemCount % ITEMS_PER_COLUMN;
			
			// add the item square
			final ShopItemSquare square = new ShopItemSquare(itemRow, itemCol, shopItems.get(i).getId(), shopItems.get(i).getType());
			square.setIcon(square.getIconImage());
			square.setVerticalAlignment(JLabel.CENTER);
			square.setHorizontalAlignment(JLabel.CENTER);
			square.setBounds((ITEM_SPACING * itemCol) + (ITEM_WIDTH * itemCol), (ITEM_SPACING * itemRow) + (ITEM_HEIGHT * itemRow), ITEM_WIDTH, ITEM_HEIGHT);
			square.addMouseListener(new MouseListener()
			{
				public void mouseExited(MouseEvent e) {}
				public void mouseReleased(MouseEvent e)
				{
				}
				public void mouseEntered(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseClicked(MouseEvent e)
				{
					// move the highlight square here
					shopSquareHighlightLabel.setLocation(square.getLocation());
					shopSquareHighlightLabel.setVisible(true);
					
					// show the item's information
					shopItemName.setText(square.getItemName());
					shopCardLabel.setIcon(square.getCardImage());
					shopItemPrice.setText(square.getPrice() + " Credit");
					
					// set this as the selected item
					selectedItem = square;
					
					// light up the Buy button
					shopItemBuyBtn.setIcon(shopItemBuyImageLit);
				}
			});
			shopItemsPanel.add(square);
			
			// add the item backing square
			JLabel invSquare = new JLabel(inventorySquare);
			invSquare.setHorizontalAlignment(JLabel.CENTER);
			invSquare.setBounds((ITEM_SPACING * itemCol) + (ITEM_WIDTH * itemCol), (ITEM_SPACING * itemRow) + (ITEM_HEIGHT * itemRow), ITEM_WIDTH, ITEM_HEIGHT);
			shopItemsPanel.add(invSquare);
			
			itemCount++;
		}
		
		itemsPanelHeight = (itemRow + 1) * 42;
		
		shopItemsPanel.setPreferredSize(new Dimension(132,itemsPanelHeight));
	}
	
	// hide all components
	private void hideComponents()
	{
		for(Component c : getComponents())
		{
			c.setVisible(false);
		}
	}
	
	// change the displayed room preview given a direction to traverse
	private void traverseRoomTemplates(String direction)
	{
		if(direction.equals("prev"))
		{
			// previous room
			selectedRoomPreview--;
			
			// wrap back around to the last entry if we go out of bounds
			if(selectedRoomPreview < roomTemplates.size())
			{
				selectedRoomPreview = roomTemplates.size() - 1;
			}
		}
		else if(direction.equals("next"))
		{
			// next room
			selectedRoomPreview++;
			
			// wrap back around to the first entry if we go out of bounds
			if(selectedRoomPreview == roomTemplates.size())
			{
				selectedRoomPreview = 0;
			}
		}
		
		// display the preview image
		roomPreviewLabel.setIcon(AppletResourceLoader.getImageFromJar("img/rooms/" + roomTemplates.get(selectedRoomPreview) + "/" + roomTemplates.get(selectedRoomPreview) + "_small.jpg"));
		
		// set the name and description labels for the room
		roomNameLabel.setText("<html><center>" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomName() + "</center></html>");
		roomDescriptionLabel.setText("<html>" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomDescription() + "</html>");
		
		// set the cost for the room
		roomCostLabel.setText("" + StaticAppletData.getRoomMapping("template_" + roomTemplates.get(selectedRoomPreview)).getRoomCost());
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
		
		if(isVisible())
		{
			myCreditsLabel.setText("" + gridObject.getMyCredits());
		}
	}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
	}
	
	public void addRoomInfo(String key, String value)
	{
		roomInfo.put(key, value);
	}
	
	private void changeRoomCreatedInfo(String name, String owner, String description)
	{
		roomCreatedName.setText("<html>" + name + "</html>");
		roomCreatedOwner.setText(owner);
		roomCreatedDescription.setText("<html>" + description + "</html>");
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
class ShopItemSquare extends JLabel
{
	private int row = 0;
	private int col = 0;
	private String itemName = "";
	private int itemPrice = -1;
	private ImageIcon image = null;
	private ImageIcon cardImage = null;
	private ImageIcon iconImage = null;
	private String itemID = "";
	private int itemType = 0;
	
	public ShopItemSquare()
	{
		super();
	}
	
	public ShopItemSquare(int row, int col, String itemID, int itemType)
	{
		super();
		
		this.row = row;
		this.col = col;
		this.itemID = itemID;
		this.itemType = itemType;
		
		// get the pin information
		if(!StaticAppletData.getInvInfo(itemID).getID().equals(""))
		{
			this.itemName = StaticAppletData.getInvInfo(itemID).getName();
			this.itemPrice = StaticAppletData.getInvInfo(itemID).getPrice();
			this.image = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(itemID).getPath());
			this.cardImage = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(itemID).getCardPath());
			this.iconImage = AppletResourceLoader.getImageFromJar(StaticAppletData.getInvInfo(itemID).getIconPath());
		}
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getItemName() {
		return itemName;
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
	
	public ImageIcon getIconImage() {
		return iconImage;
	}
	
	public String getItemID() {
		return itemID;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public int getPrice() {
		return itemPrice;
	}
	
	public int getItemType() {
		return itemType;
	}
}
