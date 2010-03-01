// ChatBubble.java by Matt Fritz
// November 11, 2009
// Handles a single chat bubble

package chat;

public class ChatBubble
{
	private String username = "";
	private String text = "";
	private int x = 100;
	private int y = 0;
	
	public ChatBubble(int index, String username, String text)
	{
		this.username = username;
		this.text = text;
		
		// 96 = 6 rows
		//this.y = 96 - (index * 16);
		this.y = 96; // + (index * 16);
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getText() {
		return text;
	}
	
	public void moveUpOnce() {y -= 2;}
	
	public void moveUp()
	{
		if(y > -16) // stop at a y-value of -16
		{
			// move up one bubble space
			if(!(y % 16 == 0))
			{
				y -= 2;
			}
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
