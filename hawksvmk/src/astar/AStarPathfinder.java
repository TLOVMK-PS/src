package astar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import tiles.Tile;

public class AStarPathfinder implements Serializable
{
	private ArrayList<Tile> openList = new ArrayList<Tile>(); // store tiles to be searched
	private ArrayList<Tile> closedList = new ArrayList<Tile>(); // store tiles in the final path
	
	private HashMap<String,Tile> tiles = new HashMap<String,Tile>(); // "row-col"
	private Tile targetTile;
	
	public AStarPathfinder()
	{
	}
	
	public void setTiles(HashMap<String,Tile> tiles) {this.tiles = tiles;}
	
	// get a path from one tile to another
	public ArrayList<Tile> getPath(Tile tileStart, Tile tileEnd)
	{
		openList.clear();
		closedList.clear();
		
		targetTile = tileEnd;
		
		/*if(tileStart == null)
		{
			System.out.println("tileStart is NULL in getPath()");
		}
		
		if(tileEnd == null)
		{
			System.out.println("tileEnd is NULL in getPath()");
		}*/
		
		// 1. Add starting point to list of open tiles
		openList.add(tileStart);
		
		// 2. Look for reachable adjacent squares from here
		addAdjacentTilesToOpenList(tileStart);
		
		// 3. Drop the starting tile and add it to the closed list
		openList.remove(tileStart);
		closedList.add(tileStart);
		
		// 4a. Get the tile with the lowest F score
		Tile lowestCostTile;
		
		do
		{
			lowestCostTile = getLowestCostTile();
			
			if(lowestCostTile == null)
			{
				//System.out.println("lowestCostTile is NULL in getPath()");
				return null;
			}
			/*else
			{
				System.out.println("Tile: " + lowestCostTile.toString());
			}*/
			
			// 4b. Drop it from the open list and add to the closed list
			openList.remove(lowestCostTile);
			closedList.add(lowestCostTile);
			
			openList.clear();
			
			// 5. Check all the adjacent squares
			addAdjacentTilesToOpenList(lowestCostTile);
		}
		while(!lowestCostTile.toString().equals(targetTile.toString()));
		
		return closedList;
	}
	
	// get the tile with the lowest cost in the open list
	private Tile getLowestCostTile()
	{
		if(openList.size() == 0)
		{
			//System.out.println("Open list is size 0 in getLowestCostTile()");
			return null;
		}
		
		Tile lowestTile = openList.get(0);
		for(Tile t : openList)
		{
			if(t.getF() < lowestTile.getF())
			{
				lowestTile = t;
			}
		}
		
		return lowestTile;
	}
	
	// add tiles adjacent to a given tile to the open list
	public void addAdjacentTilesToOpenList(Tile theTile)
	{
		if(theTile == null) {return;}
		
		int row = theTile.getRow();
		int col = theTile.getColumn();
		
		addTile(tiles.get((row-1) + "-" + col), theTile, 10); // north
		addTile(tiles.get((row-1) + "-" + (col-1)), theTile, 10); // north-west
		addTile(tiles.get(row + "-" + (col-1)), theTile, 10); // west
		addTile(tiles.get((row+1) + "-" + (col-1)), theTile, 10); // south-west
		addTile(tiles.get((row+1) + "-" + col), theTile, 10); // south
		addTile(tiles.get((row+1) + "-" + (col+1)), theTile, 10); // south-east
		addTile(tiles.get(row + "-" + (col+1)), theTile, 10); // east
		addTile(tiles.get((row-1) + "-" + (col+1)), theTile, 10); // north-east
	}
	
	// add a tile to the open list
	private void addTile(Tile theTile, Tile parentTile, int gScore)
	{
		if(theTile != null && theTile.getType() != Tile.TILE_NOGO && !closedList.contains(theTile))
		{
			if(!openList.contains(theTile)) // check to see if the open list already contains the tile
			{
				theTile.setG(parentTile.getG() + gScore); // set the orthogonal/diagonal movement cost
				theTile.setH(Math.abs(theTile.getRow() - targetTile.getRow()) + Math.abs(theTile.getColumn() - targetTile.getColumn()));
				theTile.setParent(parentTile);
				openList.add(theTile);
			}
			else
			{
				// open list already contains tile, so check G values
				int index = openList.indexOf(theTile);
				Tile existingTile = openList.get(index);
				if(gScore < existingTile.getG())
				{
					// change the tile to point at the last tile in the closed list
					existingTile.setParent(closedList.get(closedList.size() - 1));
					
					openList.set(index, existingTile);
				}
			}
		}
	}
}
