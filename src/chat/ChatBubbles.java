// ChatBubbles.java by Matt Fritz
// November 11, 2009
// Handles the various chat bubbles

package chat;

import java.util.ArrayList;

public class ChatBubbles implements Runnable
{
	private ArrayList<ChatBubble> bubbles = new ArrayList<ChatBubble>();
	
	private Thread chatThread; // thread for the chat bubbles
	
	public ChatBubbles() {}
	
	public void start() // start the Thread
	{
		if(chatThread == null)
		{
			chatThread = new Thread(this, "Chat Bubbles");
			chatThread.start();
		}
	}
	
	public void stop() // stop the Thread
	{
		chatThread.interrupt();
		chatThread = null;
	}
	
	public void run()
	{
		while(chatThread != null)
		{
			try
			{
				Thread.sleep(5000); // sleep for a specified amount of time
				
				moveUpAll(); // move all the bubbles up
			}
			catch(Exception e) {}
		}
	}
	
	// add a chat bubble to the ArrayList
	public synchronized void addChatBubble(String username, String text)
	{
		ChatBubble newBubble = new ChatBubble(bubbles.size(), username, text);
		bubbles.add(newBubble);
	}
	
	// add a chat bubble to the ArrayList
	public synchronized void addChatBubble(String username, String text, int x)
	{
		ChatBubble newBubble = new ChatBubble(bubbles.size(), username, text);
		newBubble.setX(x);
		bubbles.add(newBubble);
	}
	
	public ArrayList<ChatBubble> getChatBubbles() {return bubbles;}
	
	public void clearAll() {bubbles.clear();}
	
	public synchronized void moveUpAll()
	{
		for(int i = 0; i < 8; i++)
		{
			for(int j = 0; j < bubbles.size(); j++)
			{
				ChatBubble c = bubbles.get(j);
				c.moveUpOnce();
				
				if(c.getY() <= -16) // the bubble has passed the threshold
				{
					// remove the top-most bubble
					bubbles.remove(0);
				}
			}
			
			try
			{
				Thread.sleep(10);
			}
			catch(Exception e) {}
		}
	}
}