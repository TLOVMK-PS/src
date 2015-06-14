// VMKClient.java by Matt Fritz
// November 20, 2009
// Class that handles message passing to the server and interprets responses
// Originally made only for socket-testing purposes, but functioned so well that it was put into further production

package mainProgram;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

import roomviewer.RoomViewerUI;
import sockets.VMKClientThread;
import sockets.messages.MessageSecure;
import sockets.messages.MessageLogin;
import svc.WebService;

public class VMKClient
{
	private Socket vmkSocket = null;
	private VMKClientThread clientThread; // client thread
	private RoomViewerUI uiObject; // room viewer UI object
	
	private WebService webServiceModule = new WebService();
	
	public VMKClient() {}
	
	public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}
	public RoomViewerUI getUIObject() {return uiObject;}
	
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
    	int port = 4444;
    	
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
        	try
        	{
        		// get the server IP address from the web service to ensure it's always the most current version
        		Scanner scan = new Scanner(webServiceModule.doGetServerIP());
        		hostname = scan.nextLine();
        		scan.close();
        	}
        	catch(UnknownHostException uhe)
        	{
        		// default to the offline production server mode
        		InetAddress address = InetAddress.getLocalHost();
        		hostname = address.getHostAddress();
        	}
        	
        	System.out.println("Connecting to HVMK server...");
        	
        	// connect to the server
            vmkSocket = new Socket(hostname, port);

            System.out.println("Connected to server");
        }
        catch (ConnectException ce)
        {
        	// couldn't connect to the server (since the damn thing isn't running)
        	uiObject.displayLoadingWindowServerDown();
        	JOptionPane.showMessageDialog(null, "Whoops!\n\nIt appears the HVMK server isn't running right now.","Hawk's Virtual Magic Kingdom",JOptionPane.ERROR_MESSAGE);
        	uiObject.destroy();
        	
        	return;
        }
        catch (UnknownHostException e)
        {
        	// couldn't resolve the host for the server
        	uiObject.displayLoadingWindowServerDown();
            JOptionPane.showMessageDialog(null, "Couldn't resolve host for the HVMK server.\n\nEither it's not running or you're not connected to the Internet.", "HVMK Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            uiObject.destroy();
            
            return;
        }
        catch (IOException e)
        {
        	// couldn't open up the I/O streams
        	uiObject.displayLoadingWindowServerDown();
        	JOptionPane.showMessageDialog(null, "Couldn't get I/O for the HVMK server.\n\nIt's probably not running.", "HVMK I/O Error", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        	uiObject.destroy();
        	
        	return;
        }
        
        // start up the client thread to communicate with the server
        clientThread = new VMKClientThread(vmkSocket);
        clientThread.setUIObject(uiObject);
        clientThread.start();
        
        // send a login message to the server to perform an authentication handshake
        System.out.println("Sending login message to server for email " + uiObject.getAvatarBasicData().getEmail() + "...");
        sendMessageToServer(new MessageLogin(uiObject.getAvatarBasicData()));
        System.out.println("Login message sent");
    }
    
    // stop the client
    public void stopClient()
    {
    	if(clientThread != null)
    	{
    		clientThread.interrupt();
    	}
    	
    	try
    	{
    		if(vmkSocket != null)
    		{
    			vmkSocket.close();
    			vmkSocket = null;
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.println("Could not stop client: " + e.getClass().getName() + " - " + e.getMessage());
    	}
    }
    
    // send a message to the client thread to route to the server
    public void sendMessageToServer(MessageSecure m)
    {
    	if(clientThread != null)
    	{
    		clientThread.sendMessageToServer(m);
    	}
    }
    
    // return whether the socket is connected to the server
    public boolean isClientConnected()
    {
    	if(vmkSocket != null)
    	{
    		return vmkSocket.isConnected();
    	}
    	
    	return false;
    }

    public static void main(String args[])
    {
    	new VMKClient().startClient();
    }
}
