// PinInfo.java by Matt Fritz
// December 5, 2009
// Describes a pin

package util;

import java.io.Serializable;

public class PinInfo implements Serializable
{
	private String id = "";
	private String name = "";
	private String path = "";
	
	public PinInfo(String id, String name, String path)
	{
		this.id = id;
		this.name = name;
		this.path = path;
	}
	
	public String getID() {return id;}
	public String getName() {return name;}
	public String getPath() {return path;}
	
	public void setID(String id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setPath(String path) {this.path = path;}
}
