// InventoryItemNameComparator.java by Matt Fritz
// April 25, 2010
// Compare two InventoryItem classes based upon their names

package comparators;

import java.util.Comparator;

import util.InventoryItem;

public class InventoryItemNameComparator implements Comparator<InventoryItem>
{
	// compare the items so the they are listed alphabetically
	public int compare(InventoryItem i1, InventoryItem i2)
	{
		return i1.getName().compareToIgnoreCase(i2.getName());
	}
}
