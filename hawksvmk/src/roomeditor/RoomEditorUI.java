// RoomEditorUI.java by Matt Fritz
// March 25, 2009
// Class for the Room Editor window

package roomeditor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import util.FileOperations;
import util.AppletResourceLoader;

public class RoomEditorUI extends JFrame
{
	private AppletResourceLoader resourceLoader = new AppletResourceLoader(); // JAR resource loader
	
	RoomEditorUI myRoomEditorWindow;
	private String filename = "";
	private String currentDirectory = System.getProperty("user.dir");
	
	private JLabel tileInfo = new JLabel();
	private JComboBox tileDest = new JComboBox();
	private JComboBox tileSize = new JComboBox();
	
	public RoomEditorUI() {}
	
	public void loadRoomEditorUI()
	{
		// set general properties of the main window frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit the entire application
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLayout(null);
        
        // create the grid
        final GridView theGridView = new GridView();
        theGridView.setBounds(new Rectangle(0,0,800,600));
        theGridView.setUIObject(this);
        this.getContentPane().add(theGridView);
        
        JLabel titleLabel = new JLabel("<html><center>Room Editor v.2<br>by Matt Fritz</center></html>");
        titleLabel.setBounds(new Rectangle(860, 10, 100, 40));
        this.getContentPane().add(titleLabel);
        
        JLabel tilesComboBoxLabel = new JLabel("Tile Type");
        tilesComboBoxLabel.setBounds(new Rectangle(875, 80, 100, 20));
        this.getContentPane().add(tilesComboBoxLabel);
        
        // tile selector ComboBox
        String tiles[] = {"exit","nogo","walk"};
        final JComboBox tilesComboBox = new JComboBox(tiles);
        tilesComboBox.setSelectedItem("walk");
        tilesComboBox.addItemListener(new ItemListener()
        {
        	public void itemStateChanged(ItemEvent e)
        	{
        		theGridView.setCurrentTileType((String)tilesComboBox.getSelectedItem());
        	}
        });
        tilesComboBox.setBounds(new Rectangle(850, 100, 100, 20));
        this.getContentPane().add(tilesComboBox);
        
        // Show Grid button
        final JCheckBox showGridButton = new JCheckBox("Show Grid");
        showGridButton.setSelected(true);
        showGridButton.setBounds(new Rectangle(850, 150, 100, 32));
        showGridButton.addChangeListener(new ChangeListener()
        {
        	public void stateChanged(ChangeEvent e)
        	{
        		// toggle the grid
        		theGridView.showGrid(showGridButton.isSelected());
        	}
        });
        this.getContentPane().add(showGridButton);
        
        // Show Exit Tiles button
        final JCheckBox showExitTilesButton = new JCheckBox("Show Exit Tiles");
        showExitTilesButton.setSelected(true);
        showExitTilesButton.setBounds(new Rectangle(850, 200, 132, 32));
        showExitTilesButton.addChangeListener(new ChangeListener()
        {
        	public void stateChanged(ChangeEvent e)
        	{
        		// show/hide the exit tiles
        		theGridView.showExitTiles(showExitTilesButton.isSelected());
        	}
        });
        this.getContentPane().add(showExitTilesButton);
        
        // Show Nogo Tiles button
        final JCheckBox showNogoTilesButton = new JCheckBox("Show Nogo Tiles");
        showNogoTilesButton.setSelected(true);
        showNogoTilesButton.setBounds(new Rectangle(850, 226, 132, 32));
        showNogoTilesButton.addChangeListener(new ChangeListener()
        {
        	public void stateChanged(ChangeEvent e)
        	{
        		// show/hide the nogo tiles
        		theGridView.showNogoTiles(showNogoTilesButton.isSelected());
        	}
        });
        this.getContentPane().add(showNogoTilesButton);
        
        // Show Walk Tiles button
        final JCheckBox showWalkTilesButton = new JCheckBox("Show Walk Tiles");
        showWalkTilesButton.setSelected(true);
        showWalkTilesButton.setBounds(new Rectangle(850, 252, 132, 32));
        showWalkTilesButton.addChangeListener(new ChangeListener()
        {
        	public void stateChanged(ChangeEvent e)
        	{
        		// show/hide the walk tiles
        		theGridView.showWalkTiles(showWalkTilesButton.isSelected());
        	}
        });
        this.getContentPane().add(showWalkTilesButton);
        
        // "Change Background" button
        final JButton changeBackgroundButton = new JButton("Change Image");
        changeBackgroundButton.setBounds(new Rectangle(835, 300, 132, 32));
        changeBackgroundButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent ae)
        	{
        		// open the background image
        	    JFileChooser chooser = new JFileChooser();
        	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        	        "Image Files (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        	    chooser.setFileFilter(filter);
        	    chooser.setCurrentDirectory(new File(currentDirectory));
        	    int returnVal = chooser.showOpenDialog(null);
        	    if(returnVal == JFileChooser.APPROVE_OPTION)
        	    {
        	    	// load the background image
        	    	theGridView.setBackgroundImage("file:///" + chooser.getSelectedFile().getPath());
        	    }
        	}
        });
        this.getContentPane().add(changeBackgroundButton);
        
        // "Load Room" button
        final JButton loadRoomButton = new JButton("Load Room");
        loadRoomButton.setBounds(new Rectangle(835, 348, 132, 32));
        loadRoomButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent ae)
        	{
        		// open the file
        	    JFileChooser chooser = new JFileChooser();
        	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        	        "Room Files (*.room)", "room");
        	    chooser.setFileFilter(filter);
        	    chooser.setCurrentDirectory(new File(currentDirectory));
        	    int returnVal = chooser.showOpenDialog(myRoomEditorWindow);
        	    if(returnVal == JFileChooser.APPROVE_OPTION)
        	    {
        	    	filename = "file:///" + chooser.getSelectedFile().getPath();
        	    	
        	    	System.out.println("You chose to load this file: " + filename);
        	    	
        	    	// load the room
        	    	FileOperations.loadFile(AppletResourceLoader.getFileFromJar(filename), theGridView);
        	    }
        	}
        });
        this.getContentPane().add(loadRoomButton);
        
        // "Save Room" button
        final JButton saveRoomButton = new JButton("Save Room As...");
        saveRoomButton.setBounds(new Rectangle(835, 396, 132, 32));
        saveRoomButton.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent ae)
        	{
        		// save the file
        	    JFileChooser chooser = new JFileChooser();
        	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        	        "Room Files (*.room)", "room");
        	    chooser.setFileFilter(filter);
        	    chooser.setCurrentDirectory(new File(currentDirectory));
        	    int returnVal = chooser.showSaveDialog(myRoomEditorWindow);
        	    if(returnVal == JFileChooser.APPROVE_OPTION)
        	    {
        	    	filename = chooser.getSelectedFile().getPath();
        	    	
        	    	if(!filename.endsWith(".room"))
        	    	{
        	    		// add the file extension to the filename
        	    		filename += ".room";
        	    	}
        	    	
        	    	System.out.println("You chose to save this file: " + filename);
        	       
        	    	// write the file out
        	    	FileOperations.saveFile(filename, theGridView.getBackgroundImagePath(), theGridView.getRoomInfo(), theGridView.getTilesMap(), theGridView.getAnimations(), theGridView.getSounds(), theGridView.getRoomItems(), theGridView.getClickableAreas(), theGridView.getTileSize());
        	    	
        	    	// show a notification that the file has been saved
        	    	JOptionPane.showMessageDialog(myRoomEditorWindow, "The file " + filename + " has been saved", "File Saved", JOptionPane.INFORMATION_MESSAGE);
        	    }
        	}
        });
        this.getContentPane().add(saveRoomButton);
        
        // Tile info label
        tileInfo.setText("<html>Row:<br />Col:<br />Dest:</html>");
        tileInfo.setHorizontalAlignment(JLabel.LEFT);
        tileInfo.setVerticalAlignment(JLabel.TOP);
        tileInfo.setBounds(835, 434, 132, 48);
        this.getContentPane().add(tileInfo);
        
        JLabel tileDestLabel = new JLabel("Tile Destination");
        tileDestLabel.setHorizontalAlignment(JLabel.CENTER);
        tileDestLabel.setVerticalAlignment(JLabel.TOP);
        tileDestLabel.setBounds(835, 488, 132, 16);
        this.getContentPane().add(tileDestLabel);
        
        // Tile size selector
        String tileDests[] = FileOperations.loadEditorTileDestinations();
        tileDest = new JComboBox(tileDests);
        tileDest.setSelectedItem("");
        tileDest.addItemListener(new ItemListener()
        {
        	public void itemStateChanged(ItemEvent e)
        	{
        		String selectedDest = (String)tileDest.getSelectedItem();
        		
        		// change the tile size
        		theGridView.changeSelectedDest(selectedDest);
        	}
        });
        tileDest.setBounds(new Rectangle(835, 504, 132, 20));
        this.getContentPane().add(tileDest);
        
        JLabel tileSizeLabel = new JLabel("Tile Size");
        tileSizeLabel.setHorizontalAlignment(JLabel.CENTER);
        tileSizeLabel.setVerticalAlignment(JLabel.TOP);
        tileSizeLabel.setBounds(835, 534, 132, 16);
        this.getContentPane().add(tileSizeLabel);
        
        // Tile size selector
        String tileSizes[] = {"64x32","48x24","32x16"};
        tileSize = new JComboBox(tileSizes);
        tileSize.setSelectedItem("64x32");
        tileSize.addItemListener(new ItemListener()
        {
        	public void itemStateChanged(ItemEvent e)
        	{
        		String selectedSize = (String)tileSize.getSelectedItem();
        		
        		// change the tile size
        		if(selectedSize.equals("64x32"))
        		{
        			// large tiles
        			theGridView.changeTileSize(64, 32);
        		}
        		else if(selectedSize.equals("48x24"))
        		{
        			// medium tiles
        			theGridView.changeTileSize(48, 24);
        		}
        		else if(selectedSize.equals("32x16"))
        		{
        			// small tiles
        			theGridView.changeTileSize(32, 16);
        		}
        		else
        		{
        			// default to medium tiles
        			theGridView.changeTileSize(48, 24);
        		}
        	}
        });
        tileSize.setBounds(new Rectangle(835, 550, 132, 20));
        this.getContentPane().add(tileSize);
        
        // pack the window and display it
        this.setName("Hawk's VMK Room Editor v2.0");
        this.setTitle("Hawk's VMK Room Editor v2.0");
        this.pack();
        this.setVisible(true);
        
        // set-up the double-buffering objects and start the grid graphics loop
        theGridView.setOffscreenImage(createImage(800, 600));
        theGridView.loadGridView();
        
        myRoomEditorWindow = this;
	}
	
	// change the tile information
	public void changeTileInfo(int row, int col, String dest)
	{
		String infoString = "<html>";
		infoString += "Row: " + row + "<br />";
		infoString += "Col: " + col + "<br />";
		infoString += "Dest: " + dest;
		infoString += "</html>";
		tileInfo.setText(infoString);
	}
	
	// change the tile size
	public void changeTileSize(String size)
	{
		tileSize.setSelectedItem((String)size);
	}
}