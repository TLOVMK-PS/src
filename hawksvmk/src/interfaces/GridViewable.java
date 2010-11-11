// GridViewable.java by Matt Fritz
// November 10, 2009
// Interface to classify an object as a viewable grid

package interfaces;


import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import clickable.ClickableArea;

import animations.Animation;
import animations.StationaryAnimation;

import roomobject.RoomItem;
import sounds.SoundPlayable;
import tiles.Tile;
import ui.WindowLoading;

public interface GridViewable
{
	public void loadGridView();
	public void setOffscreenImage(Image offscreen);
	public void setBackgroundImage(String imagePath);
	public void setCurrentTileType(String type);
	
	public void showGrid(boolean showGrid);
	public void showWalkTiles(boolean showWalkTiles);
	public void showNogoTiles(boolean showNogoTiles);
	public void showExitTiles(boolean showExitTiles);
	public void toggleGrid();
	
	public HashMap<String,Tile> getTilesMap();
	public void setTilesMap(HashMap<String,Tile> tilesMap);
	public String getBackgroundImagePath();
	
	public void setAnimations(ArrayList<Animation> animations);
	public void setSounds(ArrayList<SoundPlayable> sounds);
	public void startSounds();
	public void stopSounds();
	
	public void addTextBubble(String username, String text, int x);
	public void setupChatBubbles();
	
	public void changeTileSize(int width, int height);
	public String getTileSize();
	
	public ArrayList<RoomItem> getRoomItems();
	public void setRoomItems(ArrayList<RoomItem> items);
	
	public void setRoomInfo(HashMap<String,String> roomInfo);
	public HashMap<String,String> getRoomInfo();
	public void addRoomInfo(String key, String value);
	
	public void setLoadingDescription(String description);
	
	public void setClickableAreas(ArrayList<ClickableArea> clickableAreas);
	public ArrayList<ClickableArea> getClickableAreas();
}
