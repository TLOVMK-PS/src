// VMKClient.java by Matt Fritz
// November 20, 2009
// Test class that handles message passing to the server and interprets responses

package mainProgram;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

import roomviewer.RoomViewerUI;
import sockets.VMKClientThread;
import sockets.messages.Message;
import sockets.messages.MessageLogin;
import util.StaticAppletData;

public class VMKClient
{
	private Socket vmkSocket = null;
	private String username = "";
	private VMKClientThread clientThread; // client thread
	private RoomViewerUI uiObject; // room viewer UI object
	
	public VMKClient(String username) {this.username = username;}
	
	public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}
	public RoomViewerUI getUIObject() {return uiObject;}
	
	// get the username of the client
	public String getUsername() {return username;}
	
	// start the VMK client
    public void startClient()
    {
		try
		{
			// set up logging capabilities
			//System.setOut(new PrintStream("clientlog.txt"));
			//System.setErr(new PrintStream("clientlog.txt"));
		}
		catch(Exception e) {}
		
    	String hostname = "";
    	int port = 80;
    	
        vmkSocket = null;
        
		// print out a formatted client title
		String dashes = "";
		String logTitle = "HAWK'S VMK CLIENT";
		String logDate = new Date().toString();
		for(int i = 0; i < logDate.length(); i++)
		{
			dashes += "-";
		}
		System.out.println(dashes);
		System.out.println(logTitle);
		System.out.println(logDate);
		System.out.println(dashes);
		System.out.println();
        
        try
        {
        	if(StaticAppletData.getCodeBase().startsWith("http")) // release environment
        	{
        		hostname = StaticAppletData.getCodeBaseURL().getHost();
        	}
        	else
        	{
        		// production environment
        		InetAddress address = InetAddress.getLocalHost();
        		hostname = address.getHostAddress();
        	}
        	
        	System.out.println("Connecting to VMK server at " + hostname + " on port " + port + "...");
        	
        	// connect to the server
            vmkSocket = new Socket(hostname, port);

            System.out.println("Connected to server");
        }
        catch (UnknownHostException e)
        {
        	// couldn't connect to the host
            JOptionPane.showMessageDialog(null, "Couldn't resolve host: " + hostname + ":" + port, "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (IOException e)
        {
        	// couldn't open up the I/O streams
        	JOptionPane.showMessageDialog(null, "Couldn't get I/O for host: " + hostname + ":" + port, "I/O Error", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        }
        
        // load up the pin mappings
        StaticAppletData.createPinMappings();
        
        // start up the client thread to communicate with the server
        clientThread = new VMKClientThread(vmkSocket);
        clientThread.setUIObject(uiObject);
        clientThread.start();
        
        // send a login message to the server to perform an authentication handshake
        System.out.println("Sending login message to server for email " + uiObject.getEmail() + "...");
        sendMessageToServer(new MessageLogin(username, uiObject.getEmail()));
        System.out.println("Login message sent");
    }
    
    public void stopClient()
    {
    	clientThread.interrupt();
    	
    	try
    	{
    		vmkSocket.close();
    		vmkSocket = null;
    	}
    	catch(Exception e)
    	{
    		System.out.println("Could not stop client: " + e.getClass().getName() + " - " + e.getMessage());
    	}
    }
    
    // send a message to the client thread to route to the server
    public void sendMessageToServer(Message m)
    {
    	clientThread.sendMessageToServer(m);
    }

    public static void main(String args[])
    {
    	new VMKClient("VMKPlayer").startClient();
    }
}
