package mainProgram;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class SocketTest
{
	private String username = "VMKPlayer";
	
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
		
    	String hostname = "169.234.108.111";
    	int port = 80;
        Socket vmkSocket = null;
        
        try
        {
        	JOptionPane.showMessageDialog(null, "Press OK to test the socket connection");
        	
        	// connect to the server
            vmkSocket = new Socket(hostname, port);
            
            // send a login message to the server to perform an authentication handshake
            JOptionPane.showMessageDialog(null, "Connected to server.\n\nPress OK to close.");
            
            vmkSocket.close();
        }
        catch (UnknownHostException e)
        {
        	// couldn't connect to the host
        	JOptionPane.showMessageDialog(null, "Couldn't resolve host: " + hostname + ":" + port, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (IOException e)
        {
        	// couldn't open up the I/O streams
        	JOptionPane.showMessageDialog(null, "Couldn't get I/O connection for host: " + hostname + ":" + port, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String args[])
    {
    	new SocketTest().startClient();
    }
}
