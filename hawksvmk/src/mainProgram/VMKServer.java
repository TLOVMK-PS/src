// VMKServer.java by Matt Fritz
// November 20, 2009
// Controls the initialization of the VMK server

package mainProgram;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import sockets.VMKServerPlayerData;
import sockets.VMKServerThread;
import svc.WebService;
import util.FileOperations;
import util.StaticAppletData;

public class VMKServer
{
	private String logTitle = "HAWK'S VMK SERVER 1.0";
	
    private boolean listening = true; // whether the server is listening for new connections
    private ServerSocket serverSocket = null; // server socket that listens for client connections
    private ArrayList<VMKServerThread> serverThreads = new ArrayList<VMKServerThread>(); // ArrayList of server threads
    
    private WebService webServiceModule = new WebService();
    
    // start the VMK server
	public void startServer()
	{
		try
		{
			// set up logging capabilities
			//System.setOut(new PrintStream("logs/serverlog.txt"));
			//System.setErr(new PrintStream("logs/serverlog.txt"));
		}
		catch(Exception e) {}
		
		// print out a formatted server title
		String dashes = "";
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
		
		int port = 4444;
		System.out.println("Starting VMK server on port " + port + "...");
		
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        
        // get the name and address of the server
        try
        {
        	// print out necessary server information
        	InetAddress address = InetAddress.getLocalHost();
        	System.out.println("Server IP address: " + address.getHostAddress());
        	System.out.println("Server host name: " + address.getHostName());
        	System.out.println("Server started on port " + port);
        	
        	// send the IP address to the web service database
        	System.out.println("Sending server IP to web service...");
        	webServiceModule.doSetServerIP(address.getHostAddress()).close();
        	System.out.println("Server IP set");
        	System.out.println();
        }
        catch(Exception e) {}

        // load up the pin mappings
        StaticAppletData.createInvMappings();
        
        // load the username:email mappings
        System.out.println("Loading username:email mappings...");
        VMKServerPlayerData.setUsernameEmailMappings(FileOperations.loadUsernameEmailMappings());
        System.out.println("Username:email mappings loaded");
        System.out.println();
        
        // load the room mappings
        System.out.println("Loading room mappings...");
        VMKServerPlayerData.setRooms(FileOperations.loadRoomMappings(true));
        System.out.println("Room mappings loaded");
        System.out.println();
        
        // load the game rooms
        System.out.println("Loading game rooms...");
        VMKServerPlayerData.createGameRooms();
        System.out.println("Game rooms loaded");
        System.out.println();
        
        System.out.println("Listening for client connections...");
        System.out.println();
        
        while (isListening())
        {
        	try
        	{
        		// get a new server thread from a client connection
        		Socket newSocket = serverSocket.accept();
        		
        		if(serverThreads.size() > 0)
        		{
	        		for(int i = 0; i < serverThreads.size(); i++)
	        		{
	        			// check to see if the IP address matches a client that already
	        			// has a thread, checking to make sure that thread is also waiting
	        			// for a re-connect from the client.
	        			if(serverThreads.get(i).getRemoteAddress().getAddress().getHostAddress().equals(newSocket.getInetAddress().getHostAddress())
	        					&& serverThreads.get(i).isWaitingForReconnect())
	        			{
	        				System.out.println("Client attempting to re-connect...");
	        				
	        				// make sure the server thread is still alive
	        				if(!serverThreads.get(i).isInterrupted())
	        				{
	        					System.out.println("Set new socket for existing client [" + serverThreads.get(i).getName() + "]");
	        					serverThreads.get(i).setSocket(newSocket);
	        					
	        					break;
	        				}
	        			}
	        			else
	        			{
	        				VMKServerThread newServerThread = null;
	        				
	        				// it's an entirely different IP address
	        				newServerThread = new VMKServerThread(newSocket, false);
	        					
	        				System.out.println("Accepted client socket - NO HIGHLANDER SHIT");
	        				
	                		newServerThread.start();
	                		serverThreads.add(newServerThread);
	                		
	                		newServerThread.setServerThreads(serverThreads);
	                		
	                		break;
	        			}
	        		}
        		}
        		else
        		{
        			VMKServerThread newServerThread = null;
        			
    				// there's only one client so don't worry about stream corruption yet
    				newServerThread = new VMKServerThread(newSocket, false);
            		
        			newServerThread.start();
            		serverThreads.add(newServerThread);
            		
            		newServerThread.setServerThreads(serverThreads);
            		
            		System.out.println("Accepted client socket - THERE CAN BE ONLY ONE");
        		}
        	}
        	catch(IOException e)
        	{
        		System.out.println("Error accepting client socket: " + e.getClass().getName() + " - " + e.getMessage());
        		e.printStackTrace();
        	}
        }

        try
        {
        	System.out.println("Shutting down server...");
        	serverSocket.close();
        }
        catch(IOException e)
        {
        	System.out.println("Error shutting down server: " + e.getClass().getName() + " - " + e.getMessage());
        	e.printStackTrace();
        }
        
        System.out.println("Server shut down");
	}
	
	// stop the server entirely
	public void stopServer()
	{
		listening = false;
		
		// close the socket
		System.out.println("Shutting down server...");
		
		try
		{
			serverSocket.close();
		
			System.out.println("Server shut down successfully");
		}
		catch(IOException e)
		{
			System.out.println("Could not shut down the server");
			e.printStackTrace();
		}
	}
	
	// return whether the server is listening for connections
	public boolean isListening()
	{
		return listening;
	}
	
	public static void main(String args[])
	{
		new VMKServer().startServer();
	}
}
