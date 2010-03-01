// StationaryGridObject.java by Matt Fritz
// November 11, 2009
// Handles stationary objects on the room grid

package gridobject;

import java.awt.Image;

import javax.swing.ImageIcon;

public class StationaryGridObject
{
	private int row = 0; // row of the object
	private int col = 0; // column of the object
	
	private int stackLevel = 0; // how high the object is stacked on top of others
	
	private String name = ""; // name of the object (objectName-row-col-stackLevel)
	private String objectName = ""; // name of the object without the full attributes
	
	private ImageIcon image;
	
	public StationaryGridObject() {}
	
	public StationaryGridObject(String objectName, int row, int col, int stackLevel, String imagePath)
	{
		this.name = objectName + "-" + row + "-" + col + "-" + stackLevel;
		
		this.objectName = objectName;
		this.row = row;
		this.col = col;
		this.stackLevel = stackLevel;
		
		this.image = new ImageIcon(imagePath);
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
		
		// set the full object name
		setName();
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
		
		// set the full object name
		setName();
	}

	public int getStackLevel() {
		return stackLevel;
	}

	public void setStackLevel(int stackLevel) {
		this.stackLevel = stackLevel;
		
		// set the full object name
		setName();
	}

	public String getName() {
		return name;
	}

	private void setName() {
		this.name = objectName + "-" + row + "-" + col + "-" + stackLevel;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
		
		// set the full object name
		setName();
	}

	public Image getImage() {
		return image.getImage();
	}
	
	public ImageIcon getImageIcon() {
		return image;
	}

	public void setImageIcon(ImageIcon image) {
		this.image = image;
	}
}
