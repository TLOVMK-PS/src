// VMKClientThread.java by Matt Fritz
// November 20, 2009
// CLIENT SIDE - Controls messages passed between the client and server

package sockets;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.imageio.IIOException;
import javax.swing.JOptionPane;

import rooms.VMKRoom;
import roomviewer.RoomViewerUI;

import sockets.messages.*;
import sockets.messages.games.*;
import sockets.messages.games.pirates.*;

import util.MailMessage;
import util.StaticAppletData;

public class VMKClientThread extends Thread
{
    private Socket socket = null;
    private InetSocketAddress remoteAddress = null;
    private boolean rebooting = false;
    
    ObjectOutputStream out;
    ObjectInputStream in;

    Message inputMessage; // input message sent from server
    Message outputMessage; // output message sent to server
    VMKProtocol vmkp = new VMKProtocol(); // message handler protocol
    
    private String roomID = "";
    private String roomName = "";
    RoomViewerUI uiObject; // reference to the client UI
    
    private ArrayList<Message> cachedMessages = new ArrayList<Message>(); // ArrayList of cached messages to send to the server after a re-connect

    public VMKClientThread(Socket socket)
    {
    	super("VMKClientThread");
    	this.remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
    	this.socket = socket;
    	
    	// initialize the object IO
    	try
    	{
    		out = new ObjectOutputStream(socket.getOutputStream());
    	    in = new ObjectInputStream(socket.getInputStream());
    	}
    	catch(IOException e)
    	{
    		System.out.println("Could not initialize object I/O for the client");
    		System.out.println(e.getMessage());
    	}
    }
    
    public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}

    // run the thread and process the responses from the server
    public void run()
    {
		collectInput();
    }
    
    // collect input from the thread objects
    private void collectInput()
    {
    	try
		{
		    try
		    {
		    	// process message input from the server
			    while (!isInterrupted())
			    {
			    	// process message input from the server
			    	inputMessage = (Message)in.readUnshared();
			    	
			    	// get the response from an input message
					outputMessage = vmkp.processInput(inputMessage);

					if(outputMessage instanceof MessageLogin)
					{
						// update the description in the loading window
						uiObject.setLoadingDescription("Logging you into HVMK...");
						
						// login response from server
						MessageLogin loginMessage = (MessageLogin)outputMessage;
						System.out.println("Login response received from server");
						System.out.println("Changing thread name: " + ((MessageLogin)outputMessage).getName());
						
						// change the thread name
						this.setName(loginMessage.getName());
						
						// send an "Add To Room" message
						roomID = "template_gr4";
						roomName = "Boot Hill Shooting Gallery Guest Room";
						uiObject.setRoomInformation(roomID, roomName);
						sendMessageToServer(new MessageAddUserToRoom(loginMessage.getCharacter(), roomID, roomName));
					}
					else if (outputMessage instanceof MessageLogout)
					{
						// logout/shutdown response received from server
						System.out.println("Logout response received from server for thread: " + this.getName());

					    break;
					}
					else if (outputMessage instanceof MessageGetCharacterInRoom)
					{
						MessageGetCharacterInRoom userMsg = (MessageGetCharacterInRoom)outputMessage;
						
						// get character in room response received from server
						System.out.println("Get character in room response received from server for thread: " + this.getName());
						
						// add the character to the current room
						uiObject.addCharacterToRoom(userMsg.getCharacter());

						// make the chat text box visible if necessary
						uiObject.showChatBox();
					}
					else if (outputMessage instanceof MessageAddUserToRoom)
					{
						MessageAddUserToRoom addMsg = (MessageAddUserToRoom)outputMessage;
						
						// update the description in the loading window
						uiObject.setLoadingDescription("Adding you to the HVMK map...");
						
						// user response received from server
						roomID = addMsg.getRoomID();
						roomName = addMsg.getRoomName();
						uiObject.setRoomInformation(roomID, roomName);
						System.out.println("Add user to room response received from server for thread: " + this.getName());
						
						// get all characters currently in the room
						sendMessageToServer(new MessageGetCharacterInRoom(roomID));
					}
					else if(outputMessage instanceof MessageRemoveUserFromRoom)
					{
						MessageRemoveUserFromRoom userMsg = (MessageRemoveUserFromRoom)outputMessage;
						
						// user response received from server
						System.out.println("Remove user from room response received from server for thread: " + this.getName());
						
						// remove the user from the current room
						uiObject.removeUserFromRoom(userMsg.getUsername());
					}
					else if(outputMessage instanceof MessageAddChatToRoom)
					{
						MessageAddChatToRoom chatMsg = (MessageAddChatToRoom)outputMessage;
						
						// user chat response received from server
						System.out.println("Add chat to room response received from server for thread: " + this.getName());
						
						// add the chat to the current room
						uiObject.addChatToRoom(chatMsg.getUsername(), chatMsg.getText());
					}
					else if(outputMessage instanceof MessageMoveCharacter)
					{
						MessageMoveCharacter moveMsg = (MessageMoveCharacter)outputMessage;
						
						// move character response received from server
						System.out.println("Move character response received from server for thread: " + this.getName());
					
						// move the character in the current room (if it's not user that issued the instruction)
						if(!moveMsg.getCharacter().getUsername().equals(uiObject.getUsername()))
						{
							uiObject.moveCharacter(moveMsg.getCharacter(), moveMsg.getDestGridX(), moveMsg.getDestGridY());
						}
					}
					else if(outputMessage instanceof MessageAddFriendRequest)
					{
						// add friend response received from server
						MessageAddFriendRequest requestMsg = (MessageAddFriendRequest)outputMessage;
						
						System.out.println("Add friend request response received from server");
						
						// add the friend request to the user's UI
						uiObject.addFriendRequest(requestMsg.getSender());
					}
					else if(outputMessage instanceof MessageAddFriendConfirmation)
					{
						MessageAddFriendConfirmation confirmMsg = (MessageAddFriendConfirmation)outputMessage;
						
						// add friend response received from server
						System.out.println("Add friend confirmation response (" + confirmMsg.isAccepted() + ") received from server for thread: " + this.getName());
						
						// add the new friend to the user's UI if the request was accepted
						if(confirmMsg.isAccepted())
						{
							uiObject.addFriendToList(confirmMsg.getSender());
						}
					}
					else if(outputMessage instanceof MessageGetFriendsList)
					{
						// update the description in the loading window
						uiObject.setLoadingDescription("Receiving your friends list...");
						
						MessageGetFriendsList getFriendsMsg = (MessageGetFriendsList)outputMessage;
						
						// get friends list message received from server
						System.out.println("Get friends list message received from server");
						
						// set the friends list
						uiObject.setFriendsList(getFriendsMsg.getFriendsList());
					}
					else if(outputMessage instanceof MessageRemoveFriend)
					{
						MessageRemoveFriend removeMsg = (MessageRemoveFriend)outputMessage;
						
						// remove friend message received from server
						System.out.println("Remove friend message received from server");
						
						// remove the friend from the list
						uiObject.removeFriendFromList(removeMsg.getSender());
					}
					else if(outputMessage instanceof MessageSendMailToUser)
					{
						MessageSendMailToUser mailMsg = (MessageSendMailToUser)outputMessage;
						
						// mail message received from server
						System.out.println("Mail message received from server");
						
						// add the message to the user's mail messages
						uiObject.addMailMessage(new MailMessage(mailMsg.getSender(), mailMsg.getRecipient(), mailMsg.getMessage(), mailMsg.getDateSent().toString()));
					}
					else if(outputMessage instanceof MessageGetOfflineMailMessages)
					{
						// update the description in the loading window
						uiObject.setLoadingDescription("Receiving your offline mail messages...");
						
						MessageGetOfflineMailMessages offlineMsg = (MessageGetOfflineMailMessages)outputMessage;
						
						// offline mail messages received from server
						System.out.println("Offline mail messages received from server");
						
						// set the user's mail messages
						uiObject.setMailMessages(offlineMsg.getMessages());
					}
					else if(outputMessage instanceof MessageAlterFriendStatus)
					{
						MessageAlterFriendStatus alterStatusMsg = (MessageAlterFriendStatus)outputMessage;
						
						// alter friend status message received from server
						System.out.println("Alter friend status message received for friend: " + alterStatusMsg.getFriend() + " (" + alterStatusMsg.isOnline() + ")");
						uiObject.setFriendOnline(alterStatusMsg.getFriend(), alterStatusMsg.isOnline());
					}
					else if(outputMessage instanceof MessageGetInventory)
					{
						// update the description in the loading window
						uiObject.setLoadingDescription("Receiving your inventory...");
						
						MessageGetInventory getInvMsg = (MessageGetInventory)outputMessage;
						
						// get inventory message received from server
						System.out.println("Player inventory received from server");
						uiObject.setInventory(getInvMsg.getInventory());
					}
					else if(outputMessage instanceof MessageUpdateItemInRoom)
					{
						MessageUpdateItemInRoom updateItemMsg = (MessageUpdateItemInRoom)outputMessage;
						
						// update item in room message received from server (if it's not from the user that issued it)
						if(!updateItemMsg.getItem().getOwner().equals(uiObject.getUsername()))
						{
							uiObject.updateRoomItem(updateItemMsg.getItem());
						}
					}
					else if(outputMessage instanceof MessageCreateGuestRoom)
					{
						MessageCreateGuestRoom createRoomMsg = (MessageCreateGuestRoom)outputMessage;
						
						// add the room mapping to the list for this user
						VMKRoom room = new VMKRoom(createRoomMsg.getRoomInfo().get("ID"), createRoomMsg.getRoomInfo().get("NAME"), createRoomMsg.getRoomInfo().get("PATH"));
						room.setRoomOwner(createRoomMsg.getRoomInfo().get("OWNER"));
						room.setRoomDescription(createRoomMsg.getRoomInfo().get("DESCRIPTION"));
						room.setRoomTimestamp(Long.parseLong(createRoomMsg.getRoomInfo().get("TIMESTAMP")));
						StaticAppletData.addRoomMapping(createRoomMsg.getRoomInfo().get("ID"), room);
						
						// set the newly-created room ID client-side
						uiObject.setNewlyCreatedRoomID(createRoomMsg.getRoomInfo().get("ID"));
					}
					else if(outputMessage instanceof MessageUpdateCharacterClothing)
					{
						MessageUpdateCharacterClothing updateClothingMsg = (MessageUpdateCharacterClothing)outputMessage;
						System.out.println("Update clothing response received from server");
						
						uiObject.updateCharacterClothing(updateClothingMsg.getCharacter());
					}
					else if(outputMessage instanceof MessageGameAddUserToRoom)
					{
						MessageGameAddUserToRoom gameAddUserMsg = (MessageGameAddUserToRoom)outputMessage;
						System.out.println("Game add user to room response received from server");
						
						uiObject.setGameRoomID(gameAddUserMsg.getGameID(), gameAddUserMsg.getRoomID());
					}
					else if(outputMessage instanceof MessageGameMoveCharacter)
					{
						MessageGameMoveCharacter gameMoveUserMsg = (MessageGameMoveCharacter)outputMessage;
						System.out.println("Game move user in room response received from server");
						
						// check to make sure this is not the client that issued the message
						if(!gameMoveUserMsg.getUsername().equals(uiObject.getUsername()))
						{
							// move the character in the game room
							uiObject.gameMoveCharacter(gameMoveUserMsg.getUsername(), gameMoveUserMsg.getGameID(), gameMoveUserMsg.getDestGridX(), gameMoveUserMsg.getDestGridY());
						}
					}
					else if(outputMessage instanceof MessageGameScore)
					{
						MessageGameScore gameScoreMsg = (MessageGameScore)outputMessage;
						System.out.println("Game score response received from server");
						
						uiObject.addGameScore(gameScoreMsg.getGameScore().getGame(), gameScoreMsg.getGameScore());
					}
					else if(outputMessage instanceof MessageGamePiratesFireCannons)
					{
						MessageGamePiratesFireCannons fireCannonsMsg = (MessageGamePiratesFireCannons)outputMessage;
						System.out.println("Game fire cannons response received from server");
						
						uiObject.gamePiratesFireCannons(fireCannonsMsg.getUsername(), fireCannonsMsg.getDirection());
					}
			    }
		    }
		    catch(EOFException eofe)
		    {
		    	// end of the transmission
		    }
		    catch(ClassNotFoundException cne)
		    {
		    	System.out.println("VMKClientThread - Class not found");
		    	cne.printStackTrace();
		    }
	    	catch(SocketException se)
	    	{
	    		// make sure the user is not logging-out when we attempt to re-connect to the server
	    		if(!uiObject.isWindowClosing())
	    		{
	    			reconnectToServer();
	    		
	    			collectInput();
	    			return;
	    		}
	    		else
	    		{
	    			// the user is logging-out
	    			rebooting = false;
	    		}
	    		
	    		// server shut down, so the connection was reset
	    		//System.out.println("Logout / Server shutdown");
	    		
	    		//this.interrupt(); // stop this client thread
	    		
	    		// pop up a notification that the server shut down
	    		//JOptionPane.showMessageDialog(null, "Your connection has been lost because the server has shut down.\n\nPlease close the VMK window.", "Hawk's Virtual Magic Kingdom", JOptionPane.WARNING_MESSAGE);
	    	}
	    	catch(StreamCorruptedException sce)
	    	{
	    		System.out.println("Stream corrupted when trying to read an object: " + sce.getMessage());
	    		
	    		// pop up a message letting the user know that there was a problem
	    		//JOptionPane.showMessageDialog(null, "Whoops!\n\nIt appears HVMK has crashed while reading object data.\n\nPlease close the HVMK window and try logging back in.","Hawk's Virtual Magic Kingdom",JOptionPane.WARNING_MESSAGE);
	    		
	    		// stop this client thread
	    		//this.interrupt();
	    		
	    		reconnectToServer();
	    		
	    		// try to re-boot this client thread
	    		collectInput();
	    		return;
	    	}
	    	catch(IllegalStateException ise)
	    	{
	    		System.out.println("Stream corrupted when trying to read a state object: " + ise.getMessage());
	    		
	    		// pop up a message letting the user know that there was a problem
	    		//JOptionPane.showMessageDialog(null, "Whoops!\n\nIt appears HVMK has crashed while reading state data.\n\nPlease close the HVMK window and try logging back in.","Hawk's Virtual Magic Kingdom",JOptionPane.WARNING_MESSAGE);
	    		
	    		// stop this client thread
	    		//this.interrupt();
	    		
	    		reconnectToServer();
	    		
	    		// try to re-boot this client thread
	    		collectInput();
	    		return;
	    	}
	    	catch(IIOException iioe)
	    	{
	    		System.out.println("Stream corrupted when trying to read an image: " + iioe.getMessage());
	    		
	    		// pop up a message letting the user know that there was a problem
	    		//JOptionPane.showMessageDialog(null, "Whoops!\n\nIt appears HVMK has crashed while reading image data.\n\nPlease close the HVMK window and try logging back in.","Hawk's Virtual Magic Kingdom",JOptionPane.WARNING_MESSAGE);
	    		
	    		// stop this client thread
	    		//this.interrupt();

	    		reconnectToServer();
	    		
	    		// try to re-boot this client thread
	    		collectInput();
	    		return;
	    	}
	    	
	    	// remove the current user from the current room
	    	//sendMessageToServer(new MessageRemoveUserFromRoom(uiObject.getUsername(), "Boot Hill Shooting Gallery Guest Room"));
	    	
	    	// check to make sure the socket is not re-booting
	    	if(!rebooting)
	    	{
	    		// shut down the connection to the server
		    	shutDownConnection();
	    	}
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
    }
    
    // shut down the connection to the server
    private synchronized void shutDownConnection()
    {
    	try
    	{
	    	// close down the socket if it's still connected
	    	if(socket.isConnected())
	    	{
	    		in.close(); // close the input stream
	    		out.close(); // close the output stream
	    		socket.close(); // close the socket
	    	}
	    	
	    	if(!this.isInterrupted())
	    	{
	    		this.interrupt(); // stop this server thread
	    	}
    	}
    	catch(IOException e) {}
    }
    
    // reconnect to the server after something has happened
    private synchronized void reconnectToServer()
    {
    	// check to see if the client window is terminating
    	if(uiObject.isWindowClosing())
    	{
    		// instruct the thread that re-booting will not take place
    		rebooting = false;
    		return;
    	}
    	
    	// instruct the thread that re-booting is taking place
    	rebooting = true;
    	System.out.println("Reconnecting to server...");
    	
    	try
    	{
    		// close the input stream and the socket
    		in.close();
    		socket.close();

    		System.out.println("Closed input stream");
			
	    	System.out.println("Creating socket connection...");
	    	
	    	// create the socket connection to the server again
			socket = new Socket(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
			
			System.out.println("Created socket connection");
			
			// create the output stream again so we can write to the server
			out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Created socket output stream");
    		
			// create the input stream again so we can read from the server
			in = new ObjectInputStream(socket.getInputStream());
    		System.out.println("Created socket input stream");
    		
    		// send a reconnect message to the server to make sure character data isn't lost
    		// we write the output directly since the sendMessageToServer() method is paused
    		writeOutputToServer(new MessageReconnectToServer(uiObject.getMyCharacter(), roomID));
    		
    		// send out the cached messages
    		sendCachedMessages();
    		
    		// instruct the thread that we have finished re-booting
    		rebooting = false;
			
			System.out.println("Reconnected to server");
    	}
    	catch(ConnectException ce)
    	{
    		// the server probably isn't running any more or it doesn't want to accept the fucking connection
    		JOptionPane.showMessageDialog(null, "Whoops!\n\nIt appears that the HVMK server is not running right now.","Hawk's Virtual Magic Kingdom",JOptionPane.ERROR_MESSAGE);
    	
    		// reveal that the socket is no longer re-booting
    		rebooting = false;
    		
    		// interrupt the thread and CLOSE FUCKING EVERYTHING
    		this.interrupt();
    		
    		try
    		{
    			in.close();
    			out.close();
    			socket.close();
    		}
    		catch(IOException ioe) {}
    	}
    	catch(IOException e)
    	{
    		// ah... fuck
    		e.printStackTrace();
    	}
    }
    
    // make the server shit its pants
    // for testing purposes only.
    public void fuckUpServer(Exception typeOfException) throws Exception
    {
    	System.out.println("Fucking up server...");
    	
    	if(typeOfException instanceof OptionalDataException)
    	{
    		// make the server throw an OptionalDataException
    		out.writeInt(27); // the server expects an object, so sending a primitive will make its bunghole angry
    		out.reset();
    	}
    	else if(typeOfException instanceof StreamCorruptedException)
    	{
    		// make the server throw a StreamCorruptedException by initializing another output stream on the same socket
    		ObjectOutputStream out2 = new ObjectOutputStream(socket.getOutputStream());
    		out2.reset();
    	}
    	
    	System.out.println("Server fucked up.");
    	
    	// re-connect to the server once we're finished with our up-fucking
    	reconnectToServer();
    }
    
    // write a message to the output buffer to be sent to the server
    private synchronized void writeOutputToServer(Message m) throws SocketException, IOException
    {
    	out.writeUnshared(m);
    	out.reset();
		out.flush();
    }
    
    // send out the cached messages after a re-connect
    private synchronized void sendCachedMessages()
    {
    	// check to see if there are cached messages
    	if(cachedMessages.size() > 0)
    	{
    		// send out all the cached messages first
    		for(int i = 0; i < cachedMessages.size(); i++)
    		{
    			// get the next cached message
    			Message cachedMessage = cachedMessages.remove(0);
    			
    			try
    			{
    				// send the cached message
    				System.out.println("Sending cached message (" + cachedMessage.getType() + ") to server...");
    				writeOutputToServer(cachedMessage);
    				
    				Thread.sleep(40);
    			}
    			catch(Exception e)
    			{
    				System.out.println("Could not send cached message (" + cachedMessage.getType() + ") to server");
    				
    				// add the message back to the cached messages structure for later sending
    				cachedMessages.add(cachedMessage);
    				
    				// try to reconnect to the server again
    				reconnectToServer();
    			}
    		}
    	}
    }
    
    // add a message to the messages cache for later sending
    private synchronized void cacheMessage(Message m)
    {
    	// check the type of message to see if it needs to be cached
    	if(m instanceof MessageLogout)
    	{
    		// it's a logout message, so just shut down the connection instead
    		shutDownConnection();
    	}
    	else
    	{
	    	// add the message to the cache
	    	cachedMessages.add(m);
	    	
	    	System.out.println("Cached message (" + m.getType() + ")");
    	}
    }
    
    // send a message to the server
    public synchronized void sendMessageToServer(Message m)
    {
    	// check to see if we're currently re-booting the socket
    	if(!rebooting)
    	{
	    	try
	    	{
	    		System.out.println("Sending message (" + m.getType() + ") to server...");
	    		writeOutputToServer(m);
	    	}
	    	catch(SocketException se)
	    	{
	    		// cache the message for later sending
	    		cacheMessage(m);
	    		
	    		// something happened on the server-end with the socket connection
	    		System.out.println("Socket exception: " + se.getMessage());
	    		
	    		// check to see if the socket was either written improperly or if the server closed the connection
	    		if(se.getMessage().toLowerCase().contains("socket write error") || se.getMessage().toLowerCase().contains("socket closed"))
	    		{
	    			// try to re-connect to the server
	    			reconnectToServer();
	    		}
	    	}
	    	catch(ConcurrentModificationException e)
	    	{
	    		// cache the message for later sending
	    		cacheMessage(m);
	    	}
	    	catch(IOException e)
	    	{
	    		// cache the message for later sending
	    		cacheMessage(m);
	    		
	    		System.out.println("Could not send message (" + m.getType() + ") to server for reason: " + e.getClass().getName() + " - " + e.getMessage());
	    	}
	    	catch(Exception e)
	    	{
	    		// some other problem
	    		System.out.println("Ah shit: " + e.getClass().getName() + " - " + e.getMessage());
	    		e.printStackTrace();
	    	}
    	}
    	else
    	{
    		// cache the message for later sending
    		cacheMessage(m);
    	}
    }
}
