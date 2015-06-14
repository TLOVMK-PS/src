// VMKServerThread.java by Matt Fritz
// November 20, 2009
// SERVER SIDE - Controls messages passed between the client and server

package sockets;

// TODO: START THE GODDAMN TIMEOUT THREAD FOR ANY SOCKET DISCONNECTION

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import astar.AStarCharacter;
import astar.AStarCharacterBasicData;

import roomobject.RoomItem;
import rooms.VMKRoom;

import sockets.messages.*;
import sockets.messages.games.*;
import sockets.messages.games.pirates.*;
import svc.WebService;

import util.FileOperations;
import util.FriendsList;

public class VMKServerThread extends Thread
{
    private Socket socket = null;
    private InetSocketAddress remoteAddress = null;
    private boolean waitingForReconnect = false;
    
    DataOutputStream out;
    DataInputStream in;
    
    int objectHeader = -1; // integer header (size of the next object in bytes) in the stream
    byte[] objectBytes; // the byte array that will store the specified number of bytes above
    MessageSecure inputMessage; // input message received from client

    private ArrayList<MessageSecure> cachedMessages = new ArrayList<MessageSecure>(); // cached list of messages to be sent after the client re-connects
    
    private String roomID = ""; // ID of the current room the user is in
    private String roomName = ""; // name of the current room the user is in
    
    private ArrayList<VMKServerThread> serverThreads = new ArrayList<VMKServerThread>(); // ArrayList of server threads
    
    private WebService webServiceModule = new WebService();
    
    // timeout functionality for reconnection attempts
    private ReconnectTimeoutThread reconnectTimeoutThread = null;
    protected final long TIMEOUT_THRESHOLD = 20000;
    
    public VMKServerThread(Socket socket, boolean sameIPAddress)
    {	
    	super("VMKServerThread");
    	try
    	{	
        	this.remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
        	this.socket = socket;
        	
    		// create the input and output streams for the socket
    		createSocketStreams();
    		
    		System.out.println("Socket port: " + socket.getPort());
    		System.out.println("Client " + remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort() + " connected to server");
    	}
    	catch(IOException e)
    	{	
    		System.out.println("Could not set up object I/O on the server for client " + socket.getRemoteSocketAddress().toString());
    		e.printStackTrace();
    	}
    }
    
    // create the input and output streams for the socket
    private void createSocketStreams() throws IOException
    {
    	out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }
    
    // check whether this thread is waiting for a socket re-connection from the client
    public boolean isWaitingForReconnect()
    {
    	return waitingForReconnect;
    }
    
    public InetSocketAddress getRemoteAddress() {return remoteAddress;}
    public void setSocket(Socket socket)
    {
    	// make sure this thread is still running
    	if(!isInterrupted())
    	{
	    	// set the socket again
	    	this.socket = socket;
	    	
	    	// set the remote address again (on the off-chance that the client's IP address changed)
	    	this.remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
	
	    	System.out.println("Client [" + this.getName() + "] re-connected.");
	    	
	    	// stop the timeout thread if one exists
	    	if(reconnectTimeoutThread != null)
	    	{
	    		reconnectTimeoutThread.stopThread();
	    		reconnectTimeoutThread = null;
	    	}
	    	
	    	try
	    	{
	    		// re-create the output and input streams so we can communicate with the client again
	    		createSocketStreams();
			
	    		System.out.println("Streams re-initialized for client [" + this.getName() + "]");
	    		
	    		// let the thread know that the client has re-connected
	    		waitingForReconnect = false;
	    		
	    		// start collecting input again
	    		collectInput();
	    	}
	    	catch(Exception e)
	    	{
	    		System.out.println("Client had a re-connection error: " + e.getMessage());
	    		e.printStackTrace();
	    	}
    	}
    }
    
    public void setServerThreads(ArrayList<VMKServerThread> serverThreads) {this.serverThreads = serverThreads;}

    // run the thread and process the input from the client
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
		    	while (!isInterrupted())
		    	{
		    		// grab the integer header to denote the size in bytes of the next object
		    		objectHeader = in.readInt();
		    		
		    		// grab the specified number of bytes specified by the received header
		    		objectBytes = new byte[objectHeader];
		    		in.readFully(objectBytes);
		    		
		    		// read the message sent by the client
		    		inputMessage = MessageSecure.getMessageFromBytes(objectBytes);

		    		//System.out.println("Received message (" + outputMessage.getType() + ") from client " + this.getName());

		    		// perform a validity check on the message before proceeding further
		    		if(webServiceModule.isMessageValid(inputMessage))
		    		{
		    			// figure out the type of the message and perform the respective operation
			    		if(inputMessage instanceof MessageLogin)
			    		{
			    			MessageLogin loginMessage = (MessageLogin)inputMessage;
			    			System.out.println("Login message received from client");
	
			    			// load the character from a file (or create a new one from the username in the login message)
			    			AStarCharacter authCharacter = FileOperations.loadCharacter(loginMessage.getAvatarBasicData());
	
			    			// set the username for the first time if necessary
			    			if(authCharacter.getUsername().equals(""))
			    			{
			    				// probably in development mode if the username wasn't set properly (also: FUCK!)
			    				authCharacter.setUsername("VMK Player");
			    			}
			    			
			    			// set the thread name depending on the character that was loaded
			    			this.setName(authCharacter.getUsername());
	
			    			System.out.println("Changing thread name for client " + socket.getRemoteSocketAddress().toString() + ": " + authCharacter.getUsername());
			    			
			    			// set the character for the login message
			    			loginMessage.setCharacter(authCharacter);
	
			    			// make sure we have an actual email address
			    			if(!loginMessage.getAvatarBasicData().getEmail().equals(""))
			    			{
			    				// add the username:email mapping
			    				VMKServerPlayerData.addUsernameEmailMapping(authCharacter.getUsername(), loginMessage.getAvatarBasicData().getEmail());
			    			}
	
			    			// send the login message back to the client
			    			sendMessageToClient(loginMessage);
	
			    			// update the player's status in the database (online)
			    			updatePlayerStatusInDatabase(this.getName(), "online");
	
			    			// load up the friends list in the VMKServerPlayerData class
			    			VMKServerPlayerData.addFriendsList(this.getName(), FileOperations.loadFriendsList(loginMessage.getAvatarBasicData().getEmail()));
	
			    			// send the player's friends list to him
			    			FriendsList playerFriends = VMKServerPlayerData.getFriendsList(this.getName());
			    			sendMessageToClient(new MessageGetFriendsList(playerFriends));
	
			    			// send the user's online friends to him by iterating through active users
			    			for(int i = 0; i < serverThreads.size(); i++)
			    			{
			    				// get the friend's name
			    				String friendName = serverThreads.get(i).getName();
	
			    				if(playerFriends.contains(friendName))
			    				{
			    					// send a message to the client showing that this friend is online
			    					sendMessageToClient(new MessageAlterFriendStatus(friendName, true));
	
			    					// send a message to the friend showing that this player is online
			    					sendMessageToClient(friendName, new MessageAlterFriendStatus(this.getName(), true));
			    				}
			    			}
	
			    			// load a user's offline messages and send them to him
			    			sendMessageToClient(new MessageGetOfflineMailMessages(this.getName(), FileOperations.loadMailMessages(this.getName(), loginMessage.getAvatarBasicData().getEmail())));
	
			    			// load a user's inventory and send it to him
			    			sendMessageToClient(new MessageGetInventory(FileOperations.loadInventory(loginMessage.getAvatarBasicData().getEmail())));
			    		}
			    		else if (inputMessage instanceof MessageLogout)
			    		{
			    			// logout/shutdown message received from client
			    			System.out.println("Logout message received from client for thread: " + this.getName());
	
			    			// break out of the loop to execute the centralized shutdown code
			    			break;
			    		}
			    		else if(inputMessage instanceof MessageGetCharacterInRoom)
			    		{
			    			// get server threads for room message received from client
			    			//System.out.println("Get characters in room message received from client for thread: " + this.getName());
	
			    			// set the characters in the room
			    			MessageGetCharacterInRoom userMsg = (MessageGetCharacterInRoom) inputMessage;
	
			    			// return all the characters in the room with a separate message for each in order to
			    			// prevent an IllegalStateException with "unread block data"
			    			for(int i = 0; i < serverThreads.size(); i++)
			    			{
			    				if(VMKServerPlayerData.roomContainsUser(serverThreads.get(i).getName(), roomID))
			    				{
			    					// send a "Get Character" message to the client for each character in the room
			    					userMsg.setCharacter(VMKServerPlayerData.getCharacter(serverThreads.get(i).getName()));
			    					sendMessageToClient(userMsg);
			    				}
			    			}
			    		}
			    		else if(inputMessage instanceof MessageUpdateCharacterInRoom)
			    		{
			    			// update character in room message received from client
			    			//System.out.println("Update character in room message received from client for thread: " + this.getName());
	
			    			// update the character in the room HashMap
			    			MessageUpdateCharacterInRoom userMsg = (MessageUpdateCharacterInRoom)inputMessage;
			    			roomID = userMsg.getRoomID();
			    			VMKServerPlayerData.addCharacter(userMsg.getCharacter().getUsername(), userMsg.getCharacter(), userMsg.getRoomID());
			    		}
			    		else if(inputMessage instanceof MessageUpdateCharacterClothing)
			    		{
			    			// update character clothing message received from client
			    			MessageUpdateCharacterClothing userMsg = (MessageUpdateCharacterClothing)inputMessage;
			    			
			    			// start up an update thread to make sure we can still process other operations
			    			Thread updateThread = new Thread(new UpdateClothingRunnable(userMsg));
			    			updateThread.start();
			    		}
			    		else if(inputMessage instanceof MessageAddUserToRoom)
			    		{
			    			// add user to room message received from client
			    			//System.out.println("Add user to room message received from client for thread: " + this.getName());
	
			    			// put the character into the static server HashMap
			    			MessageAddUserToRoom userMsg = (MessageAddUserToRoom)inputMessage;
	
			    			// set the room ID and room name properties and then add the character into the HashMap
			    			roomID = userMsg.getRoomID();
			    			roomName = userMsg.getRoomName();
			    			VMKServerPlayerData.addCharacter(userMsg.getUsername(), userMsg.getCharacter(), userMsg.getRoomID());
	
			    			// send the message to ALL clients in the room he's in
			    			sendMessageToAllClientsInRoom(userMsg, userMsg.getRoomID());
			    		}
			    		else if(inputMessage instanceof MessageRemoveUserFromRoom)
			    		{
			    			// remove user from room message received from client
			    			//System.out.println("Remove user from room message received from client for thread: " + this.getName());
	
			    			// remove the character from the static server HashMap
			    			MessageRemoveUserFromRoom userMsg = (MessageRemoveUserFromRoom)inputMessage;
			    			VMKServerPlayerData.removeCharacter(userMsg.getUsername(), userMsg.getRoomID());
			    			System.out.println("Characters in " + userMsg.getRoomID() + ": " + VMKServerPlayerData.getRoom(userMsg.getRoomID()).countCharacters());
	
			    			// send the message to ALL clients
			    			sendMessageToAllClientsInRoom(userMsg, userMsg.getRoomID());
			    		}
			    		else if(inputMessage instanceof MessageAddChatToRoom)
			    		{
			    			// add chat to room message received from client
			    			//System.out.println("Add chat to room message received from client for thread: " + this.getName());
	
			    			// send the message to ALL clients
			    			MessageAddChatToRoom chatMsg = (MessageAddChatToRoom)inputMessage;
			    			sendMessageToAllClientsInRoom(chatMsg, chatMsg.getRoomID());
			    		}
			    		else if(inputMessage instanceof MessageMoveCharacter)
			    		{
			    			// move character message received from client
			    			//System.out.println("Move character message received from client for thread: " + this.getName());
	
			    			// send the message to ALL clients EXCEPT the client that issued the message
			    			MessageMoveCharacter moveMsg = (MessageMoveCharacter)inputMessage;
			    			sendMessageToAllClientsInRoom(moveMsg, moveMsg.getRoomID(), this.getName());
			    		}
			    		else if(inputMessage instanceof MessageAddFriendRequest)
			    		{
			    			// add friend request message received from client
			    			MessageAddFriendRequest requestMsg = (MessageAddFriendRequest)inputMessage;
	
			    			// send the message to the recipient client
			    			sendMessageToClient(requestMsg.getRecipient(), requestMsg);
			    		}
			    		else if(inputMessage instanceof MessageAddFriendConfirmation)
			    		{
			    			// add friend confirmation message received from client
			    			MessageAddFriendConfirmation confirmMsg = (MessageAddFriendConfirmation)inputMessage;
	
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
			    		else if(inputMessage instanceof MessageRemoveFriend)
			    		{
			    			// remove friend message received from client
			    			MessageRemoveFriend removeMsg = (MessageRemoveFriend)inputMessage;
	
			    			// load the recipient's friend list from the file, if necessary (handles offline cases)
			    			if(!VMKServerPlayerData.containsFriendsList(removeMsg.getRecipient()))
			    			{
			    				// load up the recipient's friends list in the VMKServerPlayerData class
			    				VMKServerPlayerData.addFriendsList(removeMsg.getRecipient(), FileOperations.loadFriendsList(VMKServerPlayerData.getEmailFromUsername(removeMsg.getRecipient())));
			    			}
	
			    			// remove the sender and recipient from each other's lists
			    			VMKServerPlayerData.removeFriendFromList(removeMsg.getSender(), removeMsg.getRecipient());
			    			VMKServerPlayerData.removeFriendFromList(removeMsg.getRecipient(), removeMsg.getSender());
	
			    			// update the friends list files to reflect the changes
			    			FileOperations.saveFriendsList(VMKServerPlayerData.getEmailFromUsername(removeMsg.getSender()), VMKServerPlayerData.getFriendsList(removeMsg.getSender()));
			    			FileOperations.saveFriendsList(VMKServerPlayerData.getEmailFromUsername(removeMsg.getRecipient()), VMKServerPlayerData.getFriendsList(removeMsg.getRecipient()));
	
			    			// send the message to the recipient client
			    			sendMessageToClient(removeMsg.getRecipient(), removeMsg);
			    		}
			    		else if(inputMessage instanceof MessageSendMailToUser)
			    		{
			    			// mail message to user message received from client
			    			MessageSendMailToUser mailMsg = (MessageSendMailToUser)inputMessage;
	
			    			// make sure the user is online
			    			if(VMKServerPlayerData.containsFriendsList(mailMsg.getRecipient()))
			    			{
			    				// send the mail message to the recipient client
			    				sendMessageToClient(mailMsg.getRecipient(), mailMsg);
			    			}
	
			    			// save the message to hard storage
			    			FileOperations.addMailMessage(VMKServerPlayerData.getEmailFromUsername(mailMsg.getRecipient()), mailMsg.getSender(), mailMsg.getMessage(), mailMsg.getDateSent().toString());
			    		}
			    		else if(inputMessage instanceof MessageSaveMailMessages)
			    		{
			    			// save mail message received from client
			    			MessageSaveMailMessages saveMailMsg = (MessageSaveMailMessages)inputMessage;
	
			    			// save the messages to hard storage
			    			FileOperations.saveMailMessages(VMKServerPlayerData.getEmailFromUsername(saveMailMsg.getSender()), saveMailMsg.getMessages());
			    		}
			    		else if(inputMessage instanceof MessageUpdateItemInRoom)
			    		{
			    			// update item in room message received from client
			    			MessageUpdateItemInRoom updateItemMsg = (MessageUpdateItemInRoom)inputMessage;
	
			    			// send the message to all characters in the given room
			    			sendMessageToAllClientsInRoom(updateItemMsg, updateItemMsg.getRoomID());
			    		}
			    		else if(inputMessage instanceof MessageSaveGuestRoom)
			    		{
			    			// save guest room message received from client
			    			MessageSaveGuestRoom saveRoomMsg = (MessageSaveGuestRoom)inputMessage;
	
			    			// save the guest room
			    			FileOperations.saveGuestRoom(VMKServerPlayerData.getEmailFromUsername(saveRoomMsg.getRoomInfo().get("OWNER")), saveRoomMsg.getRoomInfo(), saveRoomMsg.getRoomItems(), false);
			    		}
			    		else if(inputMessage instanceof MessageCreateGuestRoom)
			    		{
			    			// create guest room message received from client
			    			MessageCreateGuestRoom createRoomMsg = (MessageCreateGuestRoom)inputMessage;
	
			    			// figure out the room ID
			    			VMKServerPlayerData.incrementGuestRoomCount();
			    			createRoomMsg.addRoomInfo("ID", "gr" + VMKServerPlayerData.getGuestRoomCount());
	
			    			// create the guest room
			    			String savedPath = FileOperations.saveGuestRoom(VMKServerPlayerData.getEmailFromUsername(createRoomMsg.getRoomInfo().get("OWNER")), createRoomMsg.getRoomInfo(), new ArrayList<RoomItem>(), true);
	
			    			// add the saved room path to the message
			    			createRoomMsg.addRoomInfo("PATH", savedPath);
	
			    			// add the new room to the VMKServerPlayerData class
			    			VMKRoom room = new VMKRoom(createRoomMsg.getRoomInfo().get("ID"), createRoomMsg.getRoomInfo().get("NAME"), createRoomMsg.getRoomInfo().get("PATH"));
			    			room.setRoomOwner(createRoomMsg.getRoomInfo().get("OWNER"));
			    			room.setRoomDescription(createRoomMsg.getRoomInfo().get("DESCRIPTION"));
			    			room.setRoomTimestamp(Long.parseLong(createRoomMsg.getRoomInfo().get("TIMESTAMP")));
			    			VMKServerPlayerData.addRoom(createRoomMsg.getRoomInfo().get("ID"), room);
	
			    			// pass the message along to the player to affect the listing client-side as well
			    			sendMessageToClient(createRoomMsg.getRoomInfo().get("OWNER"), createRoomMsg);
			    		}
			    		else if(inputMessage instanceof MessageUpdateInventory)
			    		{
			    			// update inventory message received from client
			    			MessageUpdateInventory updateInvMsg = (MessageUpdateInventory)inputMessage;
	
			    			// resolve the player's email address
			    			String email = VMKServerPlayerData.getEmailFromUsername(updateInvMsg.getUsername());
	
			    			// update the player's inventory file
			    			FileOperations.saveInventory(email, updateInvMsg.getInventory());
			    		}
			    		else if(inputMessage instanceof MessageAddInventory)
			    		{
			    			// add inventory message received from client
			    			MessageAddInventory addInvMsg = (MessageAddInventory)inputMessage;
	
			    			// resolve the player's email address
			    			String email = VMKServerPlayerData.getEmailFromUsername(addInvMsg.getUsername());
	
			    			// update the player's inventory file
			    			FileOperations.appendInventory(email, addInvMsg.getItem());
			    		}
			    		else if(inputMessage instanceof MessageReconnectToServer)
			    		{
			    			// reconnect to server message received from client
			    			MessageReconnectToServer reconnectMsg = (MessageReconnectToServer)inputMessage;
	
			    			// update the character in the HashMap so we don't lose information
			    			roomID = reconnectMsg.getRoomID();
			    			VMKServerPlayerData.addCharacter(reconnectMsg.getCharacter().getUsername(), reconnectMsg.getCharacter(), reconnectMsg.getRoomID());
	
			    			// send the cached messages back to the client since he's re-connected now
			    			sendCachedMessages();
			    		}
			    		else if(inputMessage instanceof MessageGameAddUserToRoom)
			    		{
			    			//System.out.println("Game add user to room message received from client");
	
			    			// add user to GAME ROOM message received from client
			    			MessageGameAddUserToRoom addUserGameMsg = (MessageGameAddUserToRoom)inputMessage;
	
			    			// check to make sure the character is still logged-in
			    			if(VMKServerPlayerData.getCharacter(addUserGameMsg.getCharacter().getUsername()) != null)
			    			{
			    				// remove the character from the current room he's in
			    				VMKServerPlayerData.removeCharacter(addUserGameMsg.getCharacter().getUsername(), roomID);
	
			    				// send the message to ALL clients in the room he's in
			    				sendMessageToAllClientsInRoom(new MessageRemoveUserFromRoom(addUserGameMsg.getCharacter().getUsername(), roomID), roomID);
	
			    				// figure out the game room to which the user should be added
			    				String gameID = addUserGameMsg.getGameID();
			    				roomID = VMKServerPlayerData.addCharacterToGameRoom(gameID, addUserGameMsg.getCharacter());
	
			    				System.out.println("Added user to game room: " + roomID);
	
			    				// pass the message back to the client with the new roomID
			    				addUserGameMsg.setRoomID(roomID);
			    				sendMessageToClient(addUserGameMsg);
			    			}
			    		}
			    		else if(inputMessage instanceof MessageGameRemoveUserFromRoom)
			    		{
			    			// remove user from GAME ROOM message received from client
			    			MessageGameRemoveUserFromRoom removeUserGameMsg = (MessageGameRemoveUserFromRoom)inputMessage;
	
			    			// remove the character from the current room he's in
			    			VMKServerPlayerData.removeCharacter(removeUserGameMsg.getCharacter().getUsername(), roomID);
	
			    			// send the message to ALL clients in the room he's in
			    			sendMessageToAllClientsInRoom(new MessageRemoveUserFromRoom(removeUserGameMsg.getCharacter().getUsername(), roomID), roomID);
	
			    			// check to make sure the character is still logged-in
			    			if(VMKServerPlayerData.getCharacter(removeUserGameMsg.getCharacter().getUsername()) != null)
			    			{
			    				// add him back to the original room he came from before the game
			    				roomID = removeUserGameMsg.getDestRoomID();
			    				VMKServerPlayerData.addCharacter(removeUserGameMsg.getUsername(), removeUserGameMsg.getCharacter(), roomID);
	
			    				// send an add character message to ALL clients in the specified destination room
			    				MessageAddUserToRoom addUserMsg = new MessageAddUserToRoom(removeUserGameMsg.getCharacter(), roomID, VMKServerPlayerData.getRoom(roomID).getRoomName());
			    				sendMessageToAllClientsInRoom(addUserMsg, addUserMsg.getRoomID());
			    			}
	
			    		}
			    		else if(inputMessage instanceof MessageGameMoveCharacter)
			    		{
			    			// move character in GAME ROOM message received from client
			    			MessageGameMoveCharacter moveUserGameMsg = (MessageGameMoveCharacter)inputMessage;
			    			
			    			// send the message to ALL clients in the game room EXCEPT the one that initiated the request
			    			sendMessageToAllClientsInRoom(moveUserGameMsg, moveUserGameMsg.getGameRoomID(), this.getName());
			    		}
			    		else if(inputMessage instanceof MessageGameScore)
			    		{
			    			// add game score message received from client
			    			MessageGameScore gameScoreMsg = (MessageGameScore)inputMessage;
	
			    			// pass the message back to all clients in the current game room
			    			sendMessageToAllClientsInRoom(gameScoreMsg, roomID);
			    		}
			    		else if(inputMessage instanceof MessageGamePiratesFireCannons)
			    		{
			    			// POTC fire cannons message received from client
			    			MessageGamePiratesFireCannons fireCannonsMsg = (MessageGamePiratesFireCannons)inputMessage;
			    			
			    			// pass the message back to all clients in the current game room EXCEPT the one that initiated the request
			    			sendMessageToAllClientsInRoom(fireCannonsMsg, fireCannonsMsg.getGameRoomID(), this.getName());
			    		}
		    		}
		    	}
		    }
		    catch(ClassNotFoundException cne)
		    {
		    	System.out.println("VMKServerThread - Class not found");
		    	cne.printStackTrace();
		    }
		    catch(ClassCastException cce)
		    {
		    	System.out.println("VMKServerThread [" + this.getName() + "] - Class Cast Exception (" + cce.getMessage() + ")");
		    	cce.printStackTrace();
		    }
	    	catch(SocketException se)
	    	{
	    		// check to see if something happened on the client's end
	    		if(se.getMessage().toLowerCase().contains("socket write error") || se.getMessage().toLowerCase().contains("abort"))
	    		{
	    			waitingForReconnect = true;
	    			
	    			// start a timeout here where the socket would be closed after 20 seconds or so
		    		reconnectTimeoutThread = new ReconnectTimeoutThread();
		    		reconnectTimeoutThread.start();
	    			
	    			// try to re-boot this server thread
		    		rebootSocket();

		    		return;
	    		}
	    		else
	    		{
	    			// check to see if we're waiting for a client re-connect
	    			if(!waitingForReconnect)
	    			{
	    				// client shut down, so the connection was reset
				    	System.out.println("Client shutdown (" + this.getName() + ")");
	    			}
	    			else
	    			{
	    				// start a timeout here where the socket would be closed after 20 seconds or so
			    		reconnectTimeoutThread = new ReconnectTimeoutThread();
			    		reconnectTimeoutThread.start();
			    		
	    				// we're waiting for a re-connection, so return
	    				return;
	    			}
	    		}
	    	}
	    	catch(StreamCorruptedException sce)
	    	{
	    		// somehow the stream got corrupted; shut down the thread gracefully so the server doesn't hang
	    		System.out.println("Stream corrupted on client (" + this.getName() + ")");
	    		
	    		// start a timeout here where the socket would be closed after 20 seconds or so
	    		reconnectTimeoutThread = new ReconnectTimeoutThread();
	    		reconnectTimeoutThread.start();
	    		
	    		// try to re-boot this server thread
	    		rebootSocket();

	    		return;
	    	}
	    	catch(OptionalDataException ode)
	    	{
	    		// somehow the stream got corrupted; shut down the thread gracefully so the server doesn't hang
	    		System.out.println();
	    		System.out.println("Stream corrupted [optional data] on client (" + this.getName() + ")");
	    		System.out.println();
	    		
	    		// start a timeout here where the socket would be closed after 20 seconds or so
	    		reconnectTimeoutThread = new ReconnectTimeoutThread();
	    		reconnectTimeoutThread.start();
	    		
	    		// try to re-boot this server thread
	    		rebootSocket();
	    		
	    		return;
	    	}
	    	catch(EOFException eofe)
	    	{
	    		// somehow the client-side stream got corrupted with an EOF exception or did not receive a header it
	    		// was expecting from in.readInt(); shut down the thread gracefully so the server doesn't hang
	    		System.out.println();
	    		System.out.println("Stream corrupted [invalid type code: client-side] on client (" + this.getName() + ")");
	    		System.out.println();
	    		
	    		// make sure we are waiting for a re-connection
	    		waitingForReconnect = true;

	    		System.out.println("Starting timeout thread for client [" + this.getName() + "]...");
	    		
	    		// Start a timeout here where the socket would be closed after 20 seconds or so, in order
	    		// to account for occurrences where an EOFException would happen but where it would not necessarily
	    		// represent a logout.
	    		reconnectTimeoutThread = new ReconnectTimeoutThread();
	    		reconnectTimeoutThread.start();

	    		// try to re-boot this server thread
	    		rebootSocket();

	    		return;
	    	}
	    	
	    	if(!waitingForReconnect)
	    	{
		    	System.out.println("VMKServerThread - Shutting down client socket [" + this.getName() + "]...");
		    	
		    	// shut down the server thread gracefully
		    	shutDownServerThreadGracefully();
	    	}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    // close the connection so the user can re-connect automatically
    private void rebootSocket()
    {
		try
		{
			// let the thread know that we're waiting for the client to re-connect automatically
			waitingForReconnect = true;
			System.out.println("Closing input stream and socket for client [" + this.getName() + "]...");
			
			// close the input stream and the socket
			in.close();
			socket.close();
	    	
	    	System.out.println("Waiting for a re-connection for client [" + this.getName() + "]...");
		}
		catch(Exception ioe)
		{
			ioe.printStackTrace();
		}
    }
    
    // shut down the server thread gracefully
    protected void shutDownServerThreadGracefully()
    {
    	// make sure we are not waiting for a re-connection from the client
    	waitingForReconnect = false;
    	
    	// get the character from the mapping
    	AStarCharacter theCharacter = VMKServerPlayerData.getCharacter(this.getName());
    	
    	// check to see if the fucking character exists
    	if(theCharacter == null)
    	{
    		System.out.println("THE FUCKING CHARACTER IS NULL.  WHY IS THE CHARACTER FUCKING NULL? [" + this.getName() + "]");
    	}
    	else
    	{
	    	// perform a web-service call to update the data on the web-server
	    	AStarCharacterBasicData data = new AStarCharacterBasicData(theCharacter.getUsername(), theCharacter.getEmail(), theCharacter.getGender(), theCharacter.getContentRatingAsString(), theCharacter.getCredits());
	    	updatePlayerDataInDatabase(data);
	    	
	    	// save the character to file
	    	FileOperations.saveCharacter(theCharacter);
			
			System.out.println("Saved character (" + this.getName() + ") to file");
    	}
		
		// remove the character's server thread
		serverThreads.remove(this);
		
		// set an offline status alteration message to this user's friends
		sendMessageToAllClients(new MessageAlterFriendStatus(this.getName(), false));
		
		// remove the character from the room
		sendMessageToAllClientsInRoom(new MessageRemoveUserFromRoom(this.getName(), roomID), roomID);
		
		// remove the character from the room on the server end
		VMKServerPlayerData.removeCharacter(this.getName(), roomID);
		
		// close down the socket if it's still connected
    	if(socket.isConnected())
    	{
    		try
    		{
    			in.close(); // close the input stream
    			out.close(); // close the output stream
    			socket.close(); // close the socket
    		}
    		catch(Exception e) {}
    	}
		
		// update the player's status in the database (offline)
    	updatePlayerStatusInDatabase(this.getName(), "offline");
		
		this.interrupt(); // stop this server thread
    }
    
    // write a message to the output buffer to be sent to the client
    private synchronized void writeOutputToClient(MessageSecure m) throws SocketException, IOException
    {
    	// convert the object to a byte array
    	byte[] bytes = MessageSecure.getBytesFromMessage(m);
    	
    	// write the header (size of next object in bytes) and then the object's byte array
    	out.writeInt(bytes.length);
    	out.write(bytes);
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
    			MessageSecure cachedMessage = cachedMessages.remove(0);
    			
    			try
    			{
    				// send the cached message to the client
    				System.out.println("Sending cached message (" + cachedMessage.getType() + ") to client [" + this.getName() + "]...");
    				writeOutputToClient(cachedMessage);
    				
    				Thread.sleep(40);
    			}
    			catch(Exception e)
    			{
    				System.out.println("Could not send cached message (" + cachedMessage.getType() + ") to client [" + this.getName() + "]");
    				
    				waitingForReconnect = true;
    				
    				// add the message back to the cached messages structure for later sending
    				cachedMessages.add(cachedMessage);
    				
    				// allow the client to re-connect
    				rebootSocket();
    			}
    		}
    	}
    }
    
    // add a message to the messages cache for later sending
    private synchronized void cacheMessage(MessageSecure m)
    {
    	// add the message to the cache
    	cachedMessages.add(m);
    	
    	System.out.println("Cached message (" + m.getType() + ") for client [" + this.getName() + "]");
    }
    
    // send a message to the client
    public synchronized void sendMessageToClient(MessageSecure m)
    {
    	// check to see if we're waiting for the client to re-connect
    	if(!waitingForReconnect)
    	{
	    	try
	    	{
	    		// write the message to the client
	    		writeOutputToClient(m);
	    	}
	    	catch(SocketException se)
	    	{
	    		// there was a socket error (no shit, right?)
	    		if(se.getMessage().toLowerCase().contains("socket write error"))
	    		{
	    			waitingForReconnect = true;
	    			
	    			// print out the error message
	    			//System.out.println(se.getMessage());
	    			
	    			// cache the message
	    			cacheMessage(m);
	    			
	    			// reboot the socket
	    			rebootSocket();
	    		}
	    		else
	    		{
	    			// cache the message
	    			cacheMessage(m);
	    			
	    			se.printStackTrace();
	    		}
	    	}
	    	catch(StreamCorruptedException sce)
	    	{
	    		// cache the message
				cacheMessage(m);
				
	    		// attempt reboot the socket
	    		rebootSocket();
	    	}
	    	catch(IOException e)
	    	{
	    		System.out.println("Could not send message (" + m.getType() + ") to client");
	    	}
    	}
    	else
    	{
    		// cache the message for later sending
    		cacheMessage(m);
    	}
    }
    
    // send a message to a specific client
    public synchronized void sendMessageToClient(String client, MessageSecure m)
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
    public synchronized void sendMessageToAllClients(MessageSecure m)
    {
    	//System.out.println("Sending message (" + m.getType() + ") to ALL clients...");
    	
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		//System.out.println("Sending message (" + m.getType() + ") to " + serverThreads.get(i).getName());
    		serverThreads.get(i).sendMessageToClient(m);
    	}
    }
    
    // send a message to ALL clients in a given room
    public synchronized void sendMessageToAllClientsInRoom(MessageSecure m, String room)
    {
    	//System.out.println("Sending message (" + m.getType() + ") to ALL clients...");
    	
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		if(VMKServerPlayerData.roomContainsUser(serverThreads.get(i).getName(), room))
    		{
    			//System.out.println("sendMessageToAllClientsInRoom(): Sending message (" + m.getType() + ") to " + serverThreads.get(i).getName() + " in room " + room);
    			serverThreads.get(i).sendMessageToClient(m);
    		}
    	}
    }
    
 // send a message to ALL clients in a given room EXCEPT a specified user
    public synchronized void sendMessageToAllClientsInRoom(MessageSecure m, String room, String exemptedUser)
    {
    	//System.out.println("Sending message (" + m.getType() + ") to ALL clients...");
    	
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		if(!serverThreads.get(i).getName().equals(exemptedUser))
    		{
	    		if(VMKServerPlayerData.roomContainsUser(serverThreads.get(i).getName(), room))
	    		{
	    		//System.out.println("Sending message (" + m.getType() + ") to " + serverThreads.get(i).getName());
	    			serverThreads.get(i).sendMessageToClient(m);
	    		}
    		}
    	}
    }
    
    // update a player's data in the server-side database
    private void updatePlayerDataInDatabase(AStarCharacterBasicData basicData)
    {
    	// issue the command
    	try
    	{
    		webServiceModule.doUpdatePlayerData(basicData);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Could not update basic player data in database: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    	}
    }
    
    // update a player's status in the server-side database
    private void updatePlayerStatusInDatabase(String player, String status)
    {
    	boolean isPlayerOnline = false;
    	
    	// figure out the command to issue
    	if(status.toLowerCase().equals("offline"))
    	{
    		isPlayerOnline = false;
    	}
    	else if(status.toLowerCase().equals("online"))
    	{
    		isPlayerOnline = true;
    	}
    	
    	// issue the command
    	try
    	{
    		Scanner s = new Scanner(webServiceModule.doSetPlayerStatus(isPlayerOnline, player));
    		while(s.hasNextLine())
    		{
    			System.out.println(s.nextLine());
    		}
    	}
    	catch(Exception e)
    	{
    		System.out.println("Could not update player status in database: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    	}
    }
    
    // set-up timeout functionality for re-connections (especially when an EOFException occurs)
    class ReconnectTimeoutThread extends Thread
    {
    	long startTime = 0; // the time the thread was started, in milliseconds
    	boolean running = true;
    	
    	public void run()
    	{	
    		// set the time the thread was started
    		startTime = System.currentTimeMillis();
    		
    		while(running)
    		{
    			// check to see if the time spent has reached the maximum threshold
    			if(System.currentTimeMillis() - startTime >= TIMEOUT_THRESHOLD)
    			{
    				// shut down the thread since the request has timed-out
    				running = false;
    				shutDownServerThreadGracefully();
    			}
    			else
    			{
	    			try
	    			{
	    				Thread.sleep(1000);
	    			}
	    			catch(Exception e) {}
    			}
    		}
    	}
    	
    	// stop the timeout thread
    	public void stopThread()
    	{
    		running = false;
    		interrupt();
    	}
    }
    
    // update a character's clothing in a separate thread so other actions (such as chat) can still be
    // processed while this happens
    class UpdateClothingRunnable implements Runnable
    {
    	private MessageUpdateCharacterClothing userMsg;
    	public UpdateClothingRunnable(MessageUpdateCharacterClothing userMsg) {this.userMsg = userMsg;}
    	
    	public void run()
    	{
    		// re-create the character's avatar rotations from the clothing images
			FileOperations.buildAvatarImages(userMsg.getCharacter());

			// save the character since the clothing IDs have been changed
			FileOperations.saveCharacter(userMsg.getCharacter());

			// tell the character to update the images
			userMsg.getCharacter().updateAvatarImages();

			// update the character in the room HashMap
			VMKServerPlayerData.addCharacter(userMsg.getCharacter().getUsername(), userMsg.getCharacter(), userMsg.getRoomID());

			// send the message back out to the clients to update the character's clothing on their end
			sendMessageToAllClientsInRoom(userMsg, userMsg.getRoomID());
    	}
    }
}
