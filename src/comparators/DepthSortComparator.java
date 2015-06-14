// DepthSortComparator.java by Matt Fritz
// November 14, 2010
// Handle depth sorting a collection of GridSortable objects

package comparators;

import interfaces.GridSortable;

import java.util.Comparator;

public class DepthSortComparator implements Comparator<GridSortable>
{
	// GridSortable objects with a lower base Y value (e.g. lower on screen) need to be drawn first
	public int compare(GridSortable g1, GridSortable g2)
	{
		if(g1.getBaseY() > g2.getBaseY()) {return 1;}
		if(g2.getBaseY() < g2.getBaseY()) {return -1;}
		
		return 0;
	}
}
