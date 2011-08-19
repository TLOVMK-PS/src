// LoginWindow.java by Matt Fritz
// November 17, 2009
// Handle the loading of the login window and the log-in for the game

package login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import astar.AStarCharacterBasicData;

import roomviewer.RoomViewerUI;
import util.AppletResourceLoader;

public class LoginWindow extends JFrame
{	
	private String logoImagePath = "img/ui/hawkstersvmk_100px.png";
	private String emailImagePath = "img/ui/email.png";
	private String passwordImagePath = "img/ui/password.png";
	private String loginImagePath = "img/ui/login.png";
	
	private LoginModule loginModule = new LoginModule();
	RoomViewerUI roomViewerUI = new RoomViewerUI(); // Room Viewer window
	
	public void loadLoginWindow()
	{
		// set general properties of the main window frame
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // exit the entire application
		this.setPreferredSize(new Dimension(400, 300));
		this.setResizable(false);
		this.setLayout(null);
		
		// center the window in the middle of the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width / 3); // - (this.getWidth() * 2); //- (this.getWidth()); // center horizontally
		int y = (screen.height / 3); // - (this.getHeight() * 2); //- (this.getHeight()); // center vertically
		this.setBounds(x, y, this.getWidth(), this.getHeight());
		
		// logo label
		final JLabel logoLabel = new JLabel(AppletResourceLoader.getImageFromJar(logoImagePath));
		logoLabel.setBounds(new Rectangle(10, 10, 100, 128));
		this.getContentPane().add(logoLabel);
		
		// Email label
		final JLabel emailLabel = new JLabel(AppletResourceLoader.getImageFromJar(emailImagePath));
		emailLabel.setBounds(new Rectangle(125, 100, 75, 25));
		this.getContentPane().add(emailLabel);
		
		// Password label
		final JLabel passwordLabel = new JLabel(AppletResourceLoader.getImageFromJar(passwordImagePath));
		passwordLabel.setBounds(new Rectangle(125, 130, 75, 25));
		this.getContentPane().add(passwordLabel);
		
		// Email text box
		final JTextField emailTextBox = new JTextField();
		emailTextBox.setBorder(null);
		emailTextBox.setBounds(new Rectangle(205, 100, 150, 25));
		this.getContentPane().add(emailTextBox);
		
		// Password text box
		final JPasswordField passwordTextBox = new JPasswordField();
		passwordTextBox.setBorder(null);
		passwordTextBox.setBounds(new Rectangle(205, 130, 150, 25));
		this.getContentPane().add(passwordTextBox);
		
		// "Error" label
		final JLabel errorLabel = new JLabel("* Invalid email/password combination *", JLabel.CENTER);
		errorLabel.setForeground(Color.ORANGE);
		errorLabel.setBackground(new Color(0, 128, 0));
		errorLabel.setBounds(new Rectangle(0, 165, 400, 25));
		errorLabel.setVisible(false);
		this.getContentPane().add(errorLabel);
		
		// Login button
		final JButton loginButton = new JButton(AppletResourceLoader.getImageFromJar(loginImagePath));
		loginButton.setBackground(new Color(0, 128, 0));
		loginButton.setBounds(new Rectangle(150, 200, 75, 25));
		loginButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// check the login credentials
				if(loginModule.login(emailTextBox.getText(), new String(passwordTextBox.getPassword())))
				{
					// correct credentials and authentication
					errorLabel.setVisible(false);
					
					// close the login window
					dispose();
					
					try
					{
						Thread.sleep(1000);
					}
					catch(Exception ex) {}
					
					// load up the Room Viewer window
					roomViewerUI.setAvatarBasicData(new AStarCharacterBasicData(loginModule.getUsername(), emailTextBox.getText(), loginModule.getGender(), loginModule.getContentRating(), loginModule.getCredits()));
					roomViewerUI.loadRoomViewerUI();
				}
				else
				{
					// incorrect credentials
					errorLabel.setVisible(true);
				}
			}
		});
		this.getContentPane().add(loginButton);
		
		// set a dark green background color
		this.getContentPane().setBackground(new Color(0,128,0));
		
	    // pack the window and display it
	    this.setName("Hawk's Virtual Magic Kingdom");
	    this.setTitle("Hawk's Virtual Magic Kingdom");
	    this.pack();
	    this.setVisible(true);
	}
}
