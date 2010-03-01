// WindowClothing.java by Matt Fritz
// November 29, 2009
// Handles the "Clothing" window

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import roomviewer.RoomViewerGrid;
import sockets.messages.MessageAddChatToRoom;
import util.AppletResourceLoader;

public class WindowClothing extends JPanel
{
	Font textFont;
	Font textFontBold;
	
	private int x = 0;
	private int y = 0;
	
	private int width = 338;
	private int height = 483;
	private ImageIcon windowImage = AppletResourceLoader.getImageFromJar("img/ui/preferences.png");
	
	int signatureCharacterLimit = 74;
	
	private JLabel backgroundLabel = new JLabel(windowImage);
	
	private WindowClothing messagesWindow;
	private Rectangle titleRectangle = new Rectangle(57, 14, 266, 36);
	private Rectangle exitRectangle = new Rectangle(306, 15, 16, 16);
	private Rectangle okRectangle = new Rectangle(35, 446, 199, 14);
	
	private JTextArea signatureBox = new JTextArea("");
	
	private String username = "";
	private RoomViewerGrid gridObject;
	
	public WindowClothing(Font textFont, Font textFontBold, int x, int y)
	{
		this.textFont = textFont;
		this.textFontBold = textFontBold;
		this.x = x;
		this.y = y;
		
		loadWindowInventory();
	}
	
	public void setUsername(String username) {this.username = username;}
	
	public void setGridObject(RoomViewerGrid gridObject)
	{
		this.gridObject = gridObject;
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
	
	private void loadWindowInventory()
	{
		// turn off double-buffering and set the opacity to "false"
		// required for image transparency on the window
		setDoubleBuffered(false);
		setOpaque(false);

		this.setLayout(null);
		
		// signature box
		signatureBox.setBounds(38, 386, 263, 31);
		signatureBox.setFont(textFont);
		signatureBox.setBackground(new Color(152, 190, 255));
		signatureBox.setForeground(Color.BLACK);
		signatureBox.setBorder(null);
		signatureBox.setLineWrap(true);
		signatureBox.addKeyListener(new KeyListener()
	     {
	    	 public void keyPressed(KeyEvent e)
	    	 {
	    		 if(e.getKeyCode() == KeyEvent.VK_ENTER)
				 {
					 // don't allow ENTER
					 e.consume();
				 }
	    	 }
	    	 public void keyTyped(KeyEvent e)
	    	 {
				 // only allow a certain number of characters
				 if(signatureBox.getText().length() > signatureCharacterLimit)
				 {
					 e.consume();
				 }
	    	 }
	    	 public void keyReleased(KeyEvent e)
	    	 {
	    	 }
	     });
		add(signatureBox);
		
		backgroundLabel.setBounds(0,0,width,height);
		add(backgroundLabel);
		
		this.setBounds(x,y,width,height); // set the bounds
		
		this.addMouseListener(new MouseListener()
		{
			public void mouseExited(MouseEvent e) {}
			public void mouseReleased(MouseEvent e)
			{
				repaint();
				
				if(exitRectangle.contains(e.getPoint()))
				{
					// close the window
					setVisible(false);
				}
				else if(okRectangle.contains(e.getPoint()))
				{
					// apply the signature
					gridObject.updateUserSignature(username, signatureBox.getText());
					
					// close the window
					setVisible(false);
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("X: " + e.getX() + "; Y: " + e.getY());
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e)
			{
				// check if the mouse is inside the "title bar"
				if(titleRectangle.contains(e.getPoint()))
				{
					int mouseX = e.getXOnScreen() - (getBounds().width / 2);//(e.getXOnScreen() - getBounds().x);
					int mouseY = e.getYOnScreen() - 75;
					messagesWindow.setLocation(mouseX, mouseY);
					repaint();
				}
			}
		});
		
		messagesWindow = this;
	}
	
	// toggle the visibility of this window
	public void toggleVisibility()
	{
		setVisible(!isVisible());
	}
	
	public void setSignatureText(String text)
	{
		signatureBox.setText(text);
	}
	
	public String getSignatureText() {return signatureBox.getText();}
}
