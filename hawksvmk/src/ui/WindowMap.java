// WindowMap.java by Matt Fritz
// March 25, 2010
// Handles displaying the VMK map

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import roomviewer.RoomViewerGrid;
import util.AppletResourceLoader;

// TODO: Change all the changeRoom() instructions to use room IDs instead of room names
public class WindowMap extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private RoomViewerGrid gridObject;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 800;
	private int height = 600;
	
	private ImageIcon tabVMKImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_vmk.jpg");
	private ImageIcon tabAdventurelandImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_adventureland.jpg");
	private ImageIcon tabFrontierlandImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_frontierland_closed.jpg");
	private ImageIcon tabFantasylandImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_fantasyland.jpg");
	private ImageIcon tabTomorrowlandImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_tomorrowland_closed.jpg");
	private ImageIcon tabMainStreetImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_main_street.jpg");
	private ImageIcon tabNewOrleansSquareImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_new_orleans_square_closed.jpg");
	
	private ImageIcon exitHelpImage = AppletResourceLoader.getImageFromJar("img/ui/map/map_exit_help.jpg");
	
	// VMK tab images
	private ImageIcon litFrontierlandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_frontierland_lit.jpg");
	private ImageIcon litNewOrleansSquareImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_new_orleans_square_lit.jpg");
	private ImageIcon litAdventurelandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_adventureland_lit.jpg");
	private ImageIcon litMainStreetImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_main_street_lit.jpg");
	private ImageIcon litTomorrowlandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_tomorrowland_lit.jpg");
	private ImageIcon litFantasylandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_fantasyland_lit.jpg");
	private ImageIcon litGuestRoomsImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_guest_rooms_lit.jpg");
	
	// Adventureland tab images
	private ImageIcon litPiratesGameImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_pirates_of_the_caribbean_lit.jpg");
	private ImageIcon litTikiTikiIslandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_tiki_tiki_island_lit.jpg");
	private ImageIcon litForbiddenTempleImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_forbidden_temple_lit.jpg");
	private ImageIcon litAdventurelandBazaarImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_adventureland_bazaar_lit.jpg");
	private ImageIcon litShrunkenNedsShopImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_shrunken_neds_shop_lit.jpg");
	private ImageIcon litElephantBathingPoolImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_elephant_bathing_pool_lit.jpg");
	private ImageIcon litExplorersTentImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_explorers_tent_lit.jpg");
	private ImageIcon litDiscoveryIslandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_discovery_island_lit.png");
	private ImageIcon litPirateTreehouseImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_pirate_treehouse_lit.jpg");
	private ImageIcon litJungleCruiseGameImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_jungle_cruise_game_lit.jpg");
	private ImageIcon litLostSafariPartyImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_lost_safari_party_lit.jpg");
	
	// Frontierland tab images (not open yet)
	/*private ImageIcon litSplashMountainImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_splash_mountain_lit.jpg");
	private ImageIcon litMarkTwainImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_mark_twain_lit.jpg");
	private ImageIcon litTomSawyersIslandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_tom_sawyers_island_lit.jpg");
	private ImageIcon litFrontierlandDockImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_frontierland_dock_lit.jpg");
	private ImageIcon litBigThunderMountainImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_big_thunder_mountain_lit.jpg");
	private ImageIcon litGoldenHorseshoeImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_golden_horseshoe_mercantile_shop_lit.jpg");
	private ImageIcon litFrontierlandHubImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_frontierland_hub_lit.jpg");
	*/
	
	// Fantasyland tab images
	private ImageIcon litFireworksGameImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_castle_fireworks_remixed_lit.jpg");
	private ImageIcon litGalleryImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_gallery_lit.jpg");
	private ImageIcon litCastleGardensImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_castle_gardens_lit.jpg");
	private ImageIcon litBanquetHallImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_banquet_hall_lit.jpg");
	private ImageIcon litDungeonImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_dungeon_lit.jpg");
	private ImageIcon litFantasylandCourtyardImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_fantasyland_courtyard_lit.jpg");
	private ImageIcon litSnowWhiteForestImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_snow_white_hide_n_seek_forest_lit.jpg");
	private ImageIcon litSmallWorldImportsImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_its_a_small_world_imports_lit.jpg");
	private ImageIcon litStorybooklandImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_storybookland_lit.jpg");
	private ImageIcon litMatterhornImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_matterhorn_lit.jpg");
	private ImageIcon litFantasylandInTheSkyImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_fantasyland_in_the_sky_lit.jpg");
	private ImageIcon litSpellRoomImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_spell_room_lit.png");
	
	// Tomorrowland images (not open yet)
	
	// Main Street images
	private ImageIcon litCastleForecourtImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_castle_forecourt_lit.jpg");
	private ImageIcon litCentralPlazaImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_central_plaza_lit.jpg");
	private ImageIcon litMagicShopImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_main_street_magic_shop_lit.jpg");
	private ImageIcon litVMKCentralImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_vmk_central_lit.jpg");
	private ImageIcon litMainStreetRoomImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_main_street_room_lit.jpg");
	private ImageIcon litSciFiDineInImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_sci_fi_dine_in_lit.jpg");
	private ImageIcon litPennyArcadeImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_penny_arcade_lit.jpg");
	private ImageIcon litTownSquareImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_town_square_lit.jpg");
	private ImageIcon litEmporiumImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_emporium_lit.jpg");
	private ImageIcon litMusicGameImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_music_game_lit.jpg");
	private ImageIcon litEsplanadeImg = AppletResourceLoader.getImageFromJar("img/ui/map/map_esplanade_lit.jpg");
	
	// New Orleans Square images (not open yet)
	
	private Rectangle exitRectangle = new Rectangle(1, 2, 15, 15);
	private Rectangle vmkTabRectangle = new Rectangle(43, 7, 28, 21);
	private Rectangle adventurelandTabRectangle = new Rectangle(86, 7, 83, 21);
	private Rectangle frontierlandTabRectangle = new Rectangle(189, 7, 73, 21);
	private Rectangle fantasylandTabRectangle = new Rectangle(284, 7, 70, 21);
	private Rectangle tomorrowlandTabRectangle = new Rectangle(375, 7, 82, 21);
	private Rectangle mainStreetTabRectangle = new Rectangle(473, 7, 57, 21);
	private Rectangle newOrleansSquareTabRectangle = new Rectangle(552, 7, 105, 21);
	private Rectangle guestRoomsButtonRectangle = new Rectangle(672, 7, 69, 21);
	
	private JLabel backgroundLabel = new JLabel(tabVMKImage);
	private JLabel exitHelpLabel = new JLabel(exitHelpImage);
	
	// VMK tab
	private JPanel tabVMK = new JPanel();
	private JLabel litFrontierland = new JLabel();
	private JLabel litNewOrleansSquare = new JLabel();
	private JLabel litAdventureland = new JLabel();
	private JLabel litMainStreet = new JLabel();
	private JLabel litTomorrowland = new JLabel();
	private JLabel litFantasyland = new JLabel();
	private JLabel litGuestRooms = new JLabel();
	
	// Adventureland tab
	private JPanel tabAdventureland = new JPanel();
	private JLabel litPiratesGame = new JLabel();
	private JLabel litTikiTikiIsland = new JLabel();
	private JLabel litForbiddenTemple = new JLabel();
	private JLabel litAdventurelandBazaar = new JLabel();
	private JLabel litShrunkenNedsShop = new JLabel();
	private JLabel litElephantBathingPool = new JLabel();
	private JLabel litExplorersTent = new JLabel();
	private JLabel litDiscoveryIsland = new JLabel();
	private JLabel litPirateTreehouse = new JLabel();
	private JLabel litJungleCruiseGame = new JLabel();
	private JLabel litLostSafariParty = new JLabel();
	
	// Frontierland tab
	private JPanel tabFrontierland = new JPanel();
	
	// Fantasyland tab
	private JPanel tabFantasyland = new JPanel();
	private JLabel litFireworksGame = new JLabel();
	private JLabel litGallery = new JLabel();
	private JLabel litCastleGardens = new JLabel();
	private JLabel litBanquetHall = new JLabel();
	private JLabel litDungeon = new JLabel();
	private JLabel litFantasylandCourtyard = new JLabel();
	private JLabel litSnowWhiteForest = new JLabel();
	private JLabel litSmallWorldImports = new JLabel();
	private JLabel litStorybookland = new JLabel();
	private JLabel litMatterhorn = new JLabel();
	private JLabel litFantasylandInTheSky = new JLabel();
	private JLabel litSpellRoom = new JLabel();
	
	// Tomorrowland tab
	private JPanel tabTomorrowland = new JPanel();
	
	// Main Street tab
	private JPanel tabMainStreet = new JPanel();
	private JLabel litCastleForecourt = new JLabel();
	private JLabel litCentralPlaza = new JLabel();
	private JLabel litMagicShop = new JLabel();
	private JLabel litVMKCentral = new JLabel();
	private JLabel litMainStreetRoom = new JLabel();
	private JLabel litSciFiDineIn = new JLabel();
	private JLabel litPennyArcade = new JLabel();
	private JLabel litTownSquare = new JLabel();
	private JLabel litEmporium = new JLabel();
	private JLabel litMusicGame = new JLabel();
	private JLabel litEsplanade = new JLabel();
	
	// New Orleans Square tab
	private JPanel tabNewOrleansSquare = new JPanel();
	
	WindowMap mapWindow;
	
	public WindowMap(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowMap();
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
	
	private void loadWindowMap()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);
		
		this.setLayout(null);
		
		// load the VMK tab
		loadVMKTab();
		
		// load the Adventureland tab
		loadAdventurelandTab();
		
		// load the Frontierland tab
		loadFrontierlandTab();
		
		// load the Fantasyland tab
		loadFantasylandTab();
		
		// load the Tomorrowland tab
		loadTomorrowlandTab();
		
		// load the Main Street tab
		loadMainStreetTab();
		
		// load the New Orleans Square tab
		loadNewOrleansSquareTab();
		
		// add the exit and help image
		exitHelpLabel.setBounds(748, 7, 40, 21);
		exitHelpLabel.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				if(exitRectangle.contains(e.getPoint()))
				{
					// hide the map
					mapWindow.setVisible(false);
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("X: " + e.getX() + " - Y: " + e.getY());
			}
		});
		add(exitHelpLabel);
		
		// add the background image
		backgroundLabel.setBounds(0,0,width,height);
		backgroundLabel.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				if(vmkTabRectangle.contains(e.getPoint()))
				{
					changeTab("VMK"); // change to the VMK tab
				}
				else if(adventurelandTabRectangle.contains(e.getPoint()))
				{
					changeTab("Adventureland"); // change to the Adventureland tab
				}
				else if(frontierlandTabRectangle.contains(e.getPoint()))
				{
					changeTab("Frontierland"); // change to the Frontierland tab
				}
				else if(fantasylandTabRectangle.contains(e.getPoint()))
				{
					changeTab("Fantasyland"); // change to the Fantasyland tab
				}
				else if(tomorrowlandTabRectangle.contains(e.getPoint()))
				{
					changeTab("Tomorrowland"); // change to the Tomorrowland tab
				}
				else if(mainStreetTabRectangle.contains(e.getPoint()))
				{
					changeTab("Main Street"); // change to the Main Street tab
				}
				else if(newOrleansSquareTabRectangle.contains(e.getPoint()))
				{
					changeTab("New Orleans Square"); // change to the New Orleans Square tab
				}
				else if(guestRoomsButtonRectangle.contains(e.getPoint()))
				{
					// show the "Guest Rooms" window
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("X: " + e.getX() + " - Y: " + e.getY());
			}
		});
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		mapWindow = this;
	}
	
	// load the VMK tab
	private void loadVMKTab()
	{
		tabVMK.setDoubleBuffered(false);
		tabVMK.setOpaque(false);
		
		tabVMK.setLayout(null);
		tabVMK.setBounds(x,y,width,height);
		
		// add the lit "Frontierland" image
		litFrontierland.setBounds(163, 141, 158, 35);
		litFrontierland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFrontierland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Frontierland" tab
				changeTab("Frontierland");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFrontierland.setIcon(litFrontierlandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litFrontierland);
		
		// add the lit "New Orleans Square" image
		litNewOrleansSquare.setBounds(37, 216, 208, 42);
		litNewOrleansSquare.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litNewOrleansSquare.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "New Orleans Square" tab
				changeTab("New Orleans Square");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litNewOrleansSquare.setIcon(litNewOrleansSquareImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litNewOrleansSquare);
		
		// add the lit "Adventureland" image
		litAdventureland.setBounds(136, 380, 206, 39);
		litAdventureland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litAdventureland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Adventureland" tab
				changeTab("Adventureland");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litAdventureland.setIcon(litAdventurelandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litAdventureland);
		
		// add the lit "Main Street" image
		litMainStreet.setBounds(332, 488, 162, 31);
		litMainStreet.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMainStreet.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Main Street" tab
				changeTab("Main Street");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMainStreet.setIcon(litMainStreetImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litMainStreet);
		
		// add the lit "Tomorrowland" image
		litTomorrowland.setBounds(516, 359, 194, 36);
		litTomorrowland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litTomorrowland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Tomorrowland" tab
				changeTab("Tomorrowland");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litTomorrowland.setIcon(litTomorrowlandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litTomorrowland);
		
		// add the lit "Fantasyland" image
		litFantasyland.setBounds(561, 166, 184, 37);
		litFantasyland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFantasyland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Fantasyland" tab
				changeTab("Fantasyland");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFantasyland.setIcon(litFantasylandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litFantasyland);
		
		// add the lit "Guest Rooms" image
		litGuestRooms.setBounds(324, 217, 174, 34);
		litGuestRooms.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litGuestRooms.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// show the "Guest Rooms" window
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litGuestRooms.setIcon(litGuestRoomsImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabVMK.add(litGuestRooms);
		
		// add the tab to the map
		tabVMK.setVisible(true);
		add(tabVMK);
	}
	
	// load the Adventureland tab
	private void loadAdventurelandTab()
	{
		tabAdventureland.setDoubleBuffered(false);
		tabAdventureland.setOpaque(false);
		
		tabAdventureland.setLayout(null);
		tabAdventureland.setBounds(x,y,width,height);
		
		// add the lit "Pirates of the Caribbean Game" image
		litPiratesGame.setBounds(28, 175, 160, 70);
		litPiratesGame.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litPiratesGame.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Pirates of the Caribbean Game room
				changeRoom("Pirates of the Caribbean Game");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litPiratesGame.setIcon(litPiratesGameImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litPiratesGame);
		
		// add the lit "Tiki Tiki Island" image
		litTikiTikiIsland.setBounds(31, 336, 204, 40);
		litTikiTikiIsland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litTikiTikiIsland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Tiki Tiki Island room
				changeRoom("Tiki Tiki Island");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litTikiTikiIsland.setIcon(litTikiTikiIslandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litTikiTikiIsland);
		
		// add the lit "Forbidden Temple" image
		litForbiddenTemple.setBounds(61, 493, 190, 47);
		litForbiddenTemple.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litForbiddenTemple.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Forbidden Temple room
				changeRoom("Forbidden Temple");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litForbiddenTemple.setIcon(litForbiddenTempleImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litForbiddenTemple);
		
		// add the lit "Adventureland Bazaar" image
		litAdventurelandBazaar.setBounds(119, 44, 192, 48);
		litAdventurelandBazaar.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litAdventurelandBazaar.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Adventureland Bazaar room
				changeRoom("Adventureland Bazaar");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litAdventurelandBazaar.setIcon(litAdventurelandBazaarImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litAdventurelandBazaar);
		
		// add the lit "Shrunken Ned's Shop" image
		litShrunkenNedsShop.setBounds(293, 140, 210, 41);
		litShrunkenNedsShop.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litShrunkenNedsShop.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Shrunken Ned's Shop room
				changeRoom("Shrunken Ned's Shop");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litShrunkenNedsShop.setIcon(litShrunkenNedsShopImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litShrunkenNedsShop);
		
		// add the lit "Elephant Bathing Pool" image
		litElephantBathingPool.setBounds(295, 326, 156, 48);
		litElephantBathingPool.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litElephantBathingPool.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Elephant Bathing Pool room
				changeRoom("Elephant Bathing Pool");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litElephantBathingPool.setIcon(litElephantBathingPoolImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litElephantBathingPool);
		
		// add the lit "Explorer's Tent" image
		litExplorersTent.setBounds(273, 510, 178, 45);
		litExplorersTent.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litExplorersTent.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Explorer's Tent room
				changeRoom("Explorer's Tent");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litExplorersTent.setIcon(litExplorersTentImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litExplorersTent);
		
		// add the lit "Discovery Island" image
		litDiscoveryIsland.setBounds(433, 84, 179, 35);
		litDiscoveryIsland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litDiscoveryIsland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Discovery Island room
				changeRoom("Discovery Island");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litDiscoveryIsland.setIcon(litDiscoveryIslandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litDiscoveryIsland);
		
		// add the lit "Pirate Treehouse" image
		litPirateTreehouse.setBounds(601, 169, 180, 41);
		litPirateTreehouse.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litPirateTreehouse.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Pirate Treehouse room
				changeRoom("Pirate Treehouse");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litPirateTreehouse.setIcon(litPirateTreehouseImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litPirateTreehouse);
		
		// add the lit "Jungle Cruise Game" image
		litJungleCruiseGame.setBounds(567, 285, 164, 77);
		litJungleCruiseGame.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litJungleCruiseGame.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Pirate Treehouse room
				changeRoom("Jungle Cruise Game");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litJungleCruiseGame.setIcon(litJungleCruiseGameImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litJungleCruiseGame);
		
		// add the lit "Lost Safari Party" image
		litLostSafariParty.setBounds(561, 457, 156, 51);
		litLostSafariParty.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litLostSafariParty.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to Pirate Treehouse room
				changeRoom("Lost Safari Party");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litLostSafariParty.setIcon(litLostSafariPartyImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabAdventureland.add(litLostSafariParty);
		
		// add the tab to the map
		tabAdventureland.setVisible(false);
		add(tabAdventureland);
	}
	
	// load the Frontierland tab
	private void loadFrontierlandTab()
	{
		
	}
	
	// load the Fantasyland tab
	private void loadFantasylandTab()
	{
		tabFantasyland.setDoubleBuffered(false);
		tabFantasyland.setOpaque(false);
		
		tabFantasyland.setLayout(null);
		tabFantasyland.setBounds(x,y,width,height);
		
		// add the lit "Castle Fireworks Game" image
		litFireworksGame.setBounds(28, 143, 148, 60);
		litFireworksGame.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFireworksGame.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Castle Fireworks Game room
				changeRoom("Castle Fireworks Game");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFireworksGame.setIcon(litFireworksGameImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litFireworksGame);
		
		// add the lit "Gallery" image
		litGallery.setBounds(74, 329, 110, 39);
		litGallery.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litGallery.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Gallery room
				changeRoom("Gallery");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litGallery.setIcon(litGalleryImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litGallery);
		
		// add the lit "Castle Gardens" image
		litCastleGardens.setBounds(33, 522, 170, 38);
		litCastleGardens.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litCastleGardens.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Castle Gardens room
				changeRoom("Castle Gardens");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litCastleGardens.setIcon(litCastleGardensImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litCastleGardens);
		
		// add the lit "Banquet Hall" image
		litBanquetHall.setBounds(198, 160, 146, 42);
		litBanquetHall.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litBanquetHall.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Banquet Hall room
				changeRoom("Banquet Hall");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litBanquetHall.setIcon(litBanquetHallImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litBanquetHall);
		
		// add the lit "Dungeon" image
		litDungeon.setBounds(290, 386, 108, 41);
		litDungeon.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litDungeon.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Dungeon room
				changeRoom("Dungeon");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litDungeon.setIcon(litDungeonImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litDungeon);
		
		// add the lit "Fantasyland Courtyard" image
		litFantasylandCourtyard.setBounds(239, 515, 170, 48);
		litFantasylandCourtyard.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFantasylandCourtyard.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Fantasyland Courtyard room
				changeRoom("Fantasyland Courtyard");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFantasylandCourtyard.setIcon(litFantasylandCourtyardImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litFantasylandCourtyard);
		
		// add the lit "Snow White Hide 'n Seek Forest" image
		litSnowWhiteForest.setBounds(351, 202, 202, 52);
		litSnowWhiteForest.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litSnowWhiteForest.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Snow White Hide 'n Seek Forest room
				changeRoom("Snow White Hide 'n Seek Forest");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litSnowWhiteForest.setIcon(litSnowWhiteForestImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litSnowWhiteForest);
		
		// add the lit "It's a Small World Imports" image
		litSmallWorldImports.setBounds(409, 351, 196, 48);
		litSmallWorldImports.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litSmallWorldImports.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the It's a Small World Imports room
				changeRoom("It's a Small World Imports");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litSmallWorldImports.setIcon(litSmallWorldImportsImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litSmallWorldImports);
		
		// add the lit "Storybookland" image
		litStorybookland.setBounds(408, 506, 170, 42);
		litStorybookland.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litStorybookland.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Storybookland room
				changeRoom("Storybookland");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litStorybookland.setIcon(litStorybooklandImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litStorybookland);
		
		// add the lit "Matterhorn" image
		litMatterhorn.setBounds(601, 137, 152, 46);
		litMatterhorn.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMatterhorn.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Matterhorn room
				changeRoom("Matterhorn");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMatterhorn.setIcon(litMatterhornImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litMatterhorn);
		
		// add the lit "Fantasyland In The Sky" image
		litFantasylandInTheSky.setBounds(608, 311, 172, 53);
		litFantasylandInTheSky.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litFantasylandInTheSky.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Fantasyland In The Sky room
				changeRoom("Fantasyland In The Sky");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litFantasylandInTheSky.setIcon(litFantasylandInTheSkyImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litFantasylandInTheSky);
		
		// add the lit "Spell Room" image
		litSpellRoom.setBounds(242, 286, 139, 37);
		litSpellRoom.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litSpellRoom.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Spell Room room
				changeRoom("Spell Room");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litSpellRoom.setIcon(litSpellRoomImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabFantasyland.add(litSpellRoom);
		
		// add the tab to the map
		tabFantasyland.setVisible(false);
		add(tabFantasyland);
	}
	
	// load the Tomorrowland tab
	private void loadTomorrowlandTab()
	{
		
	}
	
	// load the Main Street tab
	private void loadMainStreetTab()
	{
		tabMainStreet.setDoubleBuffered(false);
		tabMainStreet.setOpaque(false);
		
		tabMainStreet.setLayout(null);
		tabMainStreet.setBounds(x,y,width,height);
		
		// add the lit "Castle Forecourt" image
		litCastleForecourt.setBounds(50, 91, 178, 36);
		litCastleForecourt.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litCastleForecourt.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Castle Forecourt room
				changeRoom("ms1");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litCastleForecourt.setIcon(litCastleForecourtImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litCastleForecourt);
		
		// add the lit "Central Plaza" image
		litCentralPlaza.setBounds(88, 193, 160, 36);
		litCentralPlaza.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litCentralPlaza.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Central Plaza room
				changeRoom("ms2");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litCentralPlaza.setIcon(litCentralPlazaImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litCentralPlaza);
		
		// add the lit "Magic Shop" image
		litMagicShop.setBounds(234, 114, 126, 40);
		litMagicShop.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMagicShop.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Magic Shop room
				changeRoom("Magic Shop");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMagicShop.setIcon(litMagicShopImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litMagicShop);
		
		// add the lit "VMK Central" image
		litVMKCentral.setBounds(253, 212, 144, 29);
		litVMKCentral.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litVMKCentral.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the VMK Central room
				changeRoom("ms5");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litVMKCentral.setIcon(litVMKCentralImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litVMKCentral);
		
		// add the lit "Main Street" image
		litMainStreetRoom.setBounds(275, 332, 148, 37);
		litMainStreetRoom.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMainStreetRoom.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Main Street room
				changeRoom("ms6");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMainStreetRoom.setIcon(litMainStreetRoomImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litMainStreetRoom);
		
		// add the lit "Sci-Fi Dine-In" image
		litSciFiDineIn.setBounds(109, 477, 160, 38);
		litSciFiDineIn.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litSciFiDineIn.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Sci-Fi Dine-In room
				changeRoom("Sci-Fi Dine-In");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litSciFiDineIn.setIcon(litSciFiDineInImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litSciFiDineIn);
		
		// add the lit "Penny Arcade" image
		litPennyArcade.setBounds(374, 241, 134, 29);
		litPennyArcade.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litPennyArcade.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Penny Arcade room
				changeRoom("Penny Arcade");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litPennyArcade.setIcon(litPennyArcadeImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litPennyArcade);
		
		// add the lit "Town Square" image
		litTownSquare.setBounds(386, 410, 152, 28);
		litTownSquare.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litTownSquare.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Town Square room
				changeRoom("ms8");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litTownSquare.setIcon(litTownSquareImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litTownSquare);
		
		// add the lit "Emporium" image
		litEmporium.setBounds(548, 305, 128, 35);
		litEmporium.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litEmporium.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Emporium room
				changeRoom("ms10");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litEmporium.setIcon(litEmporiumImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litEmporium);
		
		// add the lit "Main Street Music Game" image
		litMusicGame.setBounds(672, 162, 92, 76);
		litMusicGame.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litMusicGame.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Main Street Music Game room
				changeRoom("Main Street Music Game");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litMusicGame.setIcon(litMusicGameImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litMusicGame);
		
		// add the lit "Esplanade" image
		litEsplanade.setBounds(620, 483, 144, 41);
		litEsplanade.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e)
			{
				// remove the lit portion
				litEsplanade.setIcon(null);
			}
			public void mouseReleased(MouseEvent e)
			{
				// change to the Esplanade room
				changeRoom("ms11");
			}
			public void mouseEntered(MouseEvent e)
			{
				// show the lit portion
				litEsplanade.setIcon(litEsplanadeImg);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		tabMainStreet.add(litEsplanade);
		
		// add the tab to the map
		tabMainStreet.setVisible(false);
		add(tabMainStreet);
	}
	
	// load the New Orleans Square tab
	private void loadNewOrleansSquareTab()
	{
		
	}
	
	// change the tab on the map and show the respective images
	private void changeTab(String tab)
	{
		// hide all the tabs first
		tabVMK.setVisible(false);
		tabAdventureland.setVisible(false);
		tabFrontierland.setVisible(false);
		tabFantasyland.setVisible(false);
		tabTomorrowland.setVisible(false);
		tabMainStreet.setVisible(false);
		tabNewOrleansSquare.setVisible(false);
		
		// figure out which tab to show
		if(tab.equals("VMK"))
		{
			tabVMK.setVisible(true);
			backgroundLabel.setIcon(tabVMKImage);
		}
		else if(tab.equals("Adventureland"))
		{
			tabAdventureland.setVisible(true);
			backgroundLabel.setIcon(tabAdventurelandImage);
		}
		else if(tab.equals("Frontierland"))
		{
			tabFrontierland.setVisible(true);
			backgroundLabel.setIcon(tabFrontierlandImage);
		}
		else if(tab.equals("Fantasyland"))
		{
			tabFantasyland.setVisible(true);
			backgroundLabel.setIcon(tabFantasylandImage);
		}
		else if(tab.equals("Tomorrowland"))
		{
			tabTomorrowland.setVisible(true);
			backgroundLabel.setIcon(tabTomorrowlandImage);
		}
		else if(tab.equals("Main Street"))
		{
			tabMainStreet.setVisible(true);
			backgroundLabel.setIcon(tabMainStreetImage);
		}
		else if(tab.equals("New Orleans Square"))
		{
			tabNewOrleansSquare.setVisible(true);
			backgroundLabel.setIcon(tabNewOrleansSquareImage);
		}
	}
	
	// change to a different room
	public void changeRoom(String roomID)
	{
		gridObject.changeRoom(roomID);
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
