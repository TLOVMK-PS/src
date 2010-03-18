// VMKServerThread.java by Matt Fritz
// November 20, 2009
// SERVER SIDE - Controls messages passed between the client and server

package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import astar.AStarCharacter;

import sockets.messages.Message;
import sockets.messages.MessageAddChatToRoom;
import sockets.messages.MessageAddFriendConfirmation;
import sockets.messages.MessageAddFriendRequest;
import sockets.messages.MessageAddUserToRoom;
import sockets.messages.MessageGetCharactersInRoom;
import sockets.messages.MessageGetFriendsList;
import sockets.messages.MessageLogin;
import sockets.messages.MessageLogout;
import sockets.messages.MessageMoveCharacter;
import sockets.messages.MessageRemoveUserFromRoom;
import sockets.messages.MessageUpdateCharacterInRoom;
import sockets.messages.VMKProtocol;
import util.FileOperations;

public class VMKServerThread extends Thread
{
    private Socket socket = null;
    
    ObjectOutputStream out;
    ObjectInputStream in;
    
    Message inputMessage; // input message sent from client
    Message outputMessage; // output message sent to client
    VMKProtocol vmkp = new VMKProtocol(); // message handler protocol
    
    private ArrayList<VMKServerThread> serverThreads = new ArrayList<VMKServerThread>(); // ArrayList of server threads

    public VMKServerThread(Socket socket)
    {
    	super("VMKServerThread");
    	this.socket = socket;
    	
    	try
    	{
    		out = new ObjectOutputStream(socket.getOutputStream());
    		in = new ObjectInputStream(socket.getInputStream());
    		
    		System.out.println("Client " + socket.getRemoteSocketAddress().toString() + " connected to server");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Could not set up object I/O on the server");
    	}
    }
    
    public void setServerThreads(ArrayList<VMKServerThread> serverThreads) {this.serverThreads = serverThreads;}

    // run the thread and process the input from the client
    public void run()
    {
		try
		{
		    try
		    {
		    	// process message input from the client
			    while ((inputMessage = (Message)in.readUnshared()) != null)
			    {
			    	// create a response from an input message
					outputMessage = vmkp.processInput(inputMessage);

					if(outputMessage instanceof MessageLogin)
					{
						MessageLogin loginMessage = (MessageLogin)outputMessage;
						System.out.println("Login message received from client");
						
						// change the thread name
						if(!loginMessage.getName().trim().equals(""))
						{
							this.setName(loginMessage.getName());
						}
						else
						{
							this.setName("VMK Player");
							loginMessage.setName("VMK Player");
						}
						
						System.out.println("Changing thread name: " + loginMessage.getName());
						
						// load the character from a file
						loginMessage.setCharacter(FileOperations.loadCharacter(loginMessage.getEmail()));
						
						// set the username for the first time if necessary
						if(loginMessage.getCharacter().getUsername().equals(""))
						{
							AStarCharacter character = loginMessage.getCharacter();
							character.setUsername(this.getName());
							loginMessage.setCharacter(character);
						}
						
						// make sure we have an actual email address
						if(!loginMessage.getEmail().equals(""))
						{
							// update the username:email mapping file
							if(!VMKServerPlayerData.containsUsernameEmailMapping(this.getName()))
							{
								FileOperations.addUsernameEmailMapping(this.getName(), loginMessage.getEmail());
							}
							
							// add the username:email mapping
							VMKServerPlayerData.addUsernameEmailMapping(this.getName(), loginMessage.getEmail());
						}

						// load up the friends list in the VMKServerPlayerData class
						VMKServerPlayerData.addFriendsList(this.getName(), FileOperations.loadFriendsList(loginMessage.getEmail()));
						
						// send the player's friends list to him
						sendMessageToClient(new MessageGetFriendsList(VMKServerPlayerData.getFriendsList(this.getName())));
						
						// send the login message back to the client
						sendMessageToClient(loginMessage);
					}
					else if (outputMessage instanceof MessageLogout)
					{
						// logout/shutdown message received from client
						System.out.println("Logout message received from client for thread: " + this.getName());
						
						// save the character to file
						FileOperations.saveCharacter(VMKServerPlayerData.getCharacter(this.getName()));
						
						System.out.println("Saved character (" + this.getName() + ") to file");
						
						// remove the user from the room
						VMKServerPlayerData.removeCharacter(this.getName());
						
						// remove this thread from the group
						serverThreads.remove(this);
						
						// send the message to ALL clients
						sendMessageToAllClients(new MessageRemoveUserFromRoom(this.getName(), "Boot Hill Shooting Gallery Guest Room"));
						
						// send the logout message back to the client
						//sendMessageToClient((MessageLogout)outputMessage);
					    break;
					}
					else if(outputMessage instanceof MessageGetCharactersInRoom)
					{
						// get server threads for room message received from client
						//System.out.println("Get characters in room message received from client for thread: " + this.getName());
						
						// set the characters in the room
						MessageGetCharactersInRoom userMsg = (MessageGetCharactersInRoom) outputMessage;
						
						// add each character in the room to the return message
						for(int i = 0; i < serverThreads.size(); i++)
						{
							userMsg.addCharacter(VMKServerPlayerData.getCharacter(serverThreads.get(i).getName()));
						}
						
						// send the message back to the client
						sendMessageToClient(userMsg);
					}
					else if(outputMessage instanceof MessageUpdateCharacterInRoom)
					{
						// update character in room message received from client
						//System.out.println("Update character in room message received from client for thread: " + this.getName());
						
						// update the character in the room HashMap
						MessageUpdateCharacterInRoom userMsg = (MessageUpdateCharacterInRoom)outputMessage;
						VMKServerPlayerData.addCharacter(userMsg.getCharacter().getUsername(), userMsg.getCharacter());
					}
					else if(outputMessage instanceof MessageAddUserToRoom)
					{
						// add user to room message received from client
						//System.out.println("Add user to room message received from client for thread: " + this.getName());
						
						// add the character to the static server HashMap
						MessageAddUserToRoom userMsg = (MessageAddUserToRoom)outputMessage;
						if(VMKServerPlayerData.getCharacter(userMsg.getUsername()) == null)
						{
							// only add the character if it doesn't already exist in the room
							VMKServerPlayerData.addCharacter(userMsg.getUsername(), userMsg.getCharacter());
						}

						// send the message to ALL clients
						sendMessageToAllClients(userMsg);
					}
					else if(outputMessage instanceof MessageRemoveUserFromRoom)
					{
						// remove user from room message received from client
						//System.out.println("Remove user from room message received from client for thread: " + this.getName());
						
						// remove the character from the static server HashMap
						MessageRemoveUserFromRoom userMsg = (MessageRemoveUserFromRoom)outputMessage;
						VMKServerPlayerData.removeCharacter(userMsg.getUsername());
						
						// send the message to ALL clients
						sendMessageToAllClients((MessageRemoveUserFromRoom)outputMessage);
					}
					else if(outputMessage instanceof MessageAddChatToRoom)
					{
						// add chat to room message received from client
						//System.out.println("Add chat to room message received from client for thread: " + this.getName());
						
						// send the message to ALL clients
						sendMessageToAllClients((MessageAddChatToRoom)outputMessage);
					}
					else if(outputMessage instanceof MessageMoveCharacter)
					{
						// move character message received from client
						//System.out.println("Move character message received from client for thread: " + this.getName());
						
						// send the message to ALL clients
						sendMessageToAllClients((MessageMoveCharacter)outputMessage);
					}
					else if(outputMessage instanceof MessageAddFriendRequest)
					{
						// add friend request message received from client
						MessageAddFriendRequest requestMsg = (MessageAddFriendRequest)outputMessage;
						
						// send the message to the recipient client
						sendMessageToClient(requestMsg.getRecipient(), requestMsg);
					}
					else if(outputMessage instanceof MessageAddFriendConfirmation)
					{
						// add friend confirmation message received from client
						MessageAddFriendConfirmation confirmMsg = (MessageAddFriendConfirmation)outputMessage;
						
						// check if the request has been accepted
						if(confirmMsg.isAccepted())
						{
							// update the sender's friends list to include the new friend
							VMKServerPlayerData.addFriendToList(confirmMsg.getSender(), confirmMsg.getRecipient());
							
							// update the recipient's friends list to include the new friend
							VMKServerPlayerData.addFriendToList(confirmMsg.getRecipient(), confirmMsg.getSender());
							
							// update the sender's friends list file to include the new friend
							FileOperations.saveFriendsList(VMKServerPlayerData.getEmailFromUsername(confirmMsg.getSender()), VMKServerPlayerData.getFriendsList(confirmMsg.getSender()));
							
							// udpate the recipient's friends list file to include the new friend
							FileOperations.saveFriendsList(VMKServerPlayerData.getEmailFromUsername(confirmMsg.getRecipient()), VMKServerPlayerData.getFriendsList(confirmMsg.getRecipient()));
						}
						
						// send the message back to the recipient
						sendMessageToClient(confirmMsg.getRecipient(), confirmMsg);
					}
			    }
		    }
		    catch(ClassNotFoundException cne)
		    {
		    	System.out.println("VMKServerThread - Class not found");
		    	cne.printStackTrace();
		    }
	    	catch(SocketException se)
	    	{
	    		// client shut down, so the connection was reset
	    		System.out.println("Client shutdown (" + this.getName() + ")");
	    		
				// save the character to file
				FileOperations.saveCharacter(VMKServerPlayerData.getCharacter(this.getName()));
				
				System.out.println("Saved character (" + this.getName() + ") to file");
	    		
	    		// remove the character's server thread
	    		serverThreads.remove(this);
	    		
	    		// remove the character from the room
				sendMessageToAllClients(new MessageRemoveUserFromRoom(this.getName(), "Boot Hill Shooting Gallery Guest Room"));
	    		
	    		this.interrupt(); // stop this server thread
	    	}
	    	
	    	System.out.println("VMKServerThread - Shutting down client socket...");
	    	
	    	// close down the socket if it's still connected
	    	if(socket.isConnected())
	    	{
	    		out.close(); // close the output stream
	    		in.close(); // close the input stream
	    		socket.close(); // close the socket
	    	}
	    	
	    	if(!this.isInterrupted())
	    	{
	    		this.interrupt(); // stop this server thread
	    	}
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
    }
    
    // send a message to the client
    public synchronized void sendMessageToClient(Message m)
    {
    	try
    	{
    		//System.out.println("Sending message (" + m.getType() + ") to client...");
    		out.writeUnshared(m);
    		out.reset();
    		//out.flush();
    	}
    	catch(IOException e)
    	{
    		System.out.println("Could not send message (" + m.getType() + ") to client");
    		e.printStackTrace();
    	}
    }
    
    // send a message to a specific client
    public synchronized void sendMessageToClient(String client, Message m)
    {
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		// find the client thread with the specific name
    		if(serverThreads.get(i).getName().equals(client))
    		{
    			serverThreads.get(i).sendMessageToClient(m);
    			break;
    		}
    	}
    }
    
    // send a message to ALL clients
    public synchronized void sendMessageToAllClients(Message m)
    {
    	//System.out.println("Sending message (" + m.getType() + ") to ALL clients...");
    	
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		//System.out.println("Sending message (" + m.getType() + ") to " + serverThreads.get(i).getName());
    		serverThreads.get(i).sendMessageToClient(m);
    	}
    }
}
