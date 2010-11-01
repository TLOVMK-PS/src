// VMKServerThread.java by Matt Fritz
// November 20, 2009
// SERVER SIDE - Controls messages passed between the client and server

package sockets;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import astar.AStarCharacter;

import roomobject.RoomItem;
import sockets.messages.Message;
import sockets.messages.MessageAddChatToRoom;
import sockets.messages.MessageAddFriendConfirmation;
import sockets.messages.MessageAddFriendRequest;
import sockets.messages.MessageAddInventory;
import sockets.messages.MessageAddUserToRoom;
import sockets.messages.MessageAlterFriendStatus;
import sockets.messages.MessageCreateGuestRoom;
import sockets.messages.MessageGetCharactersInRoom;
import sockets.messages.MessageGetFriendsList;
import sockets.messages.MessageGetInventory;
import sockets.messages.MessageGetOfflineMailMessages;
import sockets.messages.MessageLogin;
import sockets.messages.MessageLogout;
import sockets.messages.MessageMoveCharacter;
import sockets.messages.MessageReconnectToServer;
import sockets.messages.MessageRemoveFriend;
import sockets.messages.MessageRemoveUserFromRoom;
import sockets.messages.MessageSaveGuestRoom;
import sockets.messages.MessageSaveMailMessages;
import sockets.messages.MessageSendMailToUser;
import sockets.messages.MessageUpdateCharacterClothing;
import sockets.messages.MessageUpdateCharacterInRoom;
import sockets.messages.MessageUpdateInventory;
import sockets.messages.MessageUpdateItemInRoom;
import sockets.messages.VMKProtocol;
import sockets.messages.games.MessageGameAddUserToRoom;
import sockets.messages.games.MessageGameRemoveUserFromRoom;
import sockets.messages.games.MessageGameScore;
import util.FileOperations;
import util.FriendsList;
import util.StaticAppletData;
import util.VMKRoom;

public class VMKServerThread extends Thread
{
    private Socket socket = null;
    private InetSocketAddress remoteAddress = null;
    private boolean waitingForReconnect = false;
    
    ObjectOutputStream out;
    ObjectInputStream in;
    
    Message inputMessage; // input message sent from client
    Message outputMessage; // output message sent to client
    VMKProtocol vmkp = new VMKProtocol(); // message handler protocol
    private ArrayList<Message> cachedMessages = new ArrayList<Message>(); // cached list of messages to be sent after the client re-connects
    
    private String roomID = ""; // ID of the current room the user is in
    private String roomName = ""; // name of the current room the user is in
    
    private ArrayList<VMKServerThread> serverThreads = new ArrayList<VMKServerThread>(); // ArrayList of server threads
    
    public VMKServerThread(Socket socket, boolean sameIPAddress)
    {	
    	super("VMKServerThread");
    	try
    	{	
        	this.remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
        	this.socket = socket;
        	
    		// TODO: Figure out another way to handle possibly multiple connections
    		// from the same machine since we can't use the same input stream for the
    		// same computer, as it corrupts the stream with an invalid header.
    		out = new ObjectOutputStream(socket.getOutputStream());
    		in = new ObjectInputStream(socket.getInputStream());
    		
    		System.out.println("Socket port: " + socket.getPort());
    		System.out.println("Client " + remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort() + " connected to server");
    	}
    	catch(IOException e)
    	{	
    		System.out.println("Could not set up object I/O on the server for client " + socket.getRemoteSocketAddress().toString());
    		e.printStackTrace();
    		
    		this.interrupt();
    		System.exit(-1);
    	}
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
	    	
	    	try
	    	{
	    		// re-create the output and input streams so we can communicate with the client again
	    		out = new ObjectOutputStream(socket.getOutputStream());
	    		in = new ObjectInputStream(socket.getInputStream());
			
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
	    		
	    		this.interrupt();
	    		System.exit(-1);
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
		    		// create a response from an input message
		    		inputMessage = (Message)in.readUnshared();
		    		outputMessage = vmkp.processInput(inputMessage);

		    		System.out.println("Received message (" + outputMessage.getType() + ") from client " + this.getName());

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

		    			System.out.println("Changing thread name for client " + socket.getRemoteSocketAddress().toString() + ": " + loginMessage.getName());

		    			// load the character from a file
		    			loginMessage.setCharacter(FileOperations.loadCharacter(loginMessage.getName(), loginMessage.getEmail()));

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

		    			// send the login message back to the client
		    			sendMessageToClient(loginMessage);

		    			// update the player's status in the database (online)
		    			updatePlayerStatusInDatabase(this.getName(), "online");

		    			// load up the friends list in the VMKServerPlayerData class
		    			VMKServerPlayerData.addFriendsList(this.getName(), FileOperations.loadFriendsList(loginMessage.getEmail()));

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
		    			sendMessageToClient(new MessageGetOfflineMailMessages(this.getName(), FileOperations.loadMailMessages(this.getName(), loginMessage.getEmail())));

		    			// load a user's inventory and send it to him
		    			sendMessageToClient(new MessageGetInventory(FileOperations.loadInventory(loginMessage.getEmail())));
		    		}
		    		else if (outputMessage instanceof MessageLogout)
		    		{
		    			// logout/shutdown message received from client
		    			System.out.println("Logout message received from client for thread: " + this.getName());

		    			// break out of the loop to execute the centralized shutdown code
		    			break;
		    		}
		    		else if(outputMessage instanceof MessageGetCharactersInRoom)
		    		{
		    			// get server threads for room message received from client
		    			//System.out.println("Get characters in room message received from client for thread: " + this.getName());

		    			// set the characters in the room
		    			MessageGetCharactersInRoom userMsg = (MessageGetCharactersInRoom) outputMessage;

		    			// add each character in the room to the return message
		    			System.out.println("GET CHARACTERS IN ROOM; SERVER THREADS SIZE: " + serverThreads.size() + " FOR CLIENT [" + this.getName() + "]");
		    			for(int i = 0; i < serverThreads.size(); i++)
		    			{
		    				if(VMKServerPlayerData.roomContainsUser(serverThreads.get(i).getName(), userMsg.getRoomID()))
		    				{
		    					userMsg.addCharacter(VMKServerPlayerData.getCharacter(serverThreads.get(i).getName()));
		    				}
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
		    			roomID = userMsg.getRoomID();
		    			VMKServerPlayerData.addCharacter(userMsg.getCharacter().getUsername(), userMsg.getCharacter(), userMsg.getRoomID());
		    		}
		    		else if(outputMessage instanceof MessageUpdateCharacterClothing)
		    		{
		    			// update character clothing message received from client
		    			MessageUpdateCharacterClothing userMsg = (MessageUpdateCharacterClothing)outputMessage;

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
		    		else if(outputMessage instanceof MessageAddUserToRoom)
		    		{
		    			// add user to room message received from client
		    			//System.out.println("Add user to room message received from client for thread: " + this.getName());

		    			// add the character to the static server HashMap
		    			MessageAddUserToRoom userMsg = (MessageAddUserToRoom)outputMessage;
		    			if(VMKServerPlayerData.getCharacter(userMsg.getUsername()) == null)
		    			{
		    				// only add the character if it doesn't already exist in the room
		    				roomID = userMsg.getRoomID();
		    				roomName = userMsg.getRoomName();
		    				VMKServerPlayerData.addCharacter(userMsg.getUsername(), userMsg.getCharacter(), userMsg.getRoomID());
		    			}

		    			// send the message to ALL clients in the room he's in
		    			sendMessageToAllClientsInRoom(userMsg, userMsg.getRoomID());
		    		}
		    		else if(outputMessage instanceof MessageRemoveUserFromRoom)
		    		{
		    			// remove user from room message received from client
		    			//System.out.println("Remove user from room message received from client for thread: " + this.getName());

		    			// remove the character from the static server HashMap
		    			MessageRemoveUserFromRoom userMsg = (MessageRemoveUserFromRoom)outputMessage;
		    			VMKServerPlayerData.removeCharacter(userMsg.getUsername(), userMsg.getRoomID());

		    			// send the message to ALL clients
		    			sendMessageToAllClientsInRoom(userMsg, userMsg.getRoomID());
		    		}
		    		else if(outputMessage instanceof MessageAddChatToRoom)
		    		{
		    			// add chat to room message received from client
		    			//System.out.println("Add chat to room message received from client for thread: " + this.getName());

		    			// send the message to ALL clients
		    			MessageAddChatToRoom chatMsg = (MessageAddChatToRoom)outputMessage;
		    			sendMessageToAllClientsInRoom(chatMsg, chatMsg.getRoomID());
		    		}
		    		else if(outputMessage instanceof MessageMoveCharacter)
		    		{
		    			// move character message received from client
		    			//System.out.println("Move character message received from client for thread: " + this.getName());

		    			// send the message to ALL clients EXCEPT the client that issued the message
		    			MessageMoveCharacter moveMsg = (MessageMoveCharacter)outputMessage;
		    			sendMessageToAllClientsInRoom(moveMsg, moveMsg.getRoomID(), this.getName());
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
		    		else if(outputMessage instanceof MessageRemoveFriend)
		    		{
		    			// remove friend message received from client
		    			MessageRemoveFriend removeMsg = (MessageRemoveFriend)outputMessage;

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
		    		else if(outputMessage instanceof MessageSendMailToUser)
		    		{
		    			// mail message to user message received from client
		    			MessageSendMailToUser mailMsg = (MessageSendMailToUser)outputMessage;

		    			// make sure the user is online
		    			if(VMKServerPlayerData.containsFriendsList(mailMsg.getRecipient()))
		    			{
		    				// send the mail message to the recipient client
		    				sendMessageToClient(mailMsg.getRecipient(), mailMsg);
		    			}

		    			// save the message to hard storage
		    			FileOperations.addMailMessage(VMKServerPlayerData.getEmailFromUsername(mailMsg.getRecipient()), mailMsg.getSender(), mailMsg.getMessage(), mailMsg.getDateSent().toString());
		    		}
		    		else if(outputMessage instanceof MessageSaveMailMessages)
		    		{
		    			// save mail message received from client
		    			MessageSaveMailMessages saveMailMsg = (MessageSaveMailMessages)outputMessage;

		    			// save the messages to hard storage
		    			FileOperations.saveMailMessages(VMKServerPlayerData.getEmailFromUsername(saveMailMsg.getSender()), saveMailMsg.getMessages());
		    		}
		    		else if(outputMessage instanceof MessageUpdateItemInRoom)
		    		{
		    			// update item in room message received from client
		    			MessageUpdateItemInRoom updateItemMsg = (MessageUpdateItemInRoom)outputMessage;

		    			// send the message to all characters in the given room
		    			sendMessageToAllClientsInRoom(updateItemMsg, updateItemMsg.getRoomID());
		    		}
		    		else if(outputMessage instanceof MessageSaveGuestRoom)
		    		{
		    			// save guest room message received from client
		    			MessageSaveGuestRoom saveRoomMsg = (MessageSaveGuestRoom)outputMessage;

		    			// save the guest room
		    			FileOperations.saveGuestRoom(VMKServerPlayerData.getEmailFromUsername(saveRoomMsg.getRoomInfo().get("OWNER")), saveRoomMsg.getRoomInfo(), saveRoomMsg.getRoomItems(), false);
		    		}
		    		else if(outputMessage instanceof MessageCreateGuestRoom)
		    		{
		    			// create guest room message received from client
		    			MessageCreateGuestRoom createRoomMsg = (MessageCreateGuestRoom)outputMessage;

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
		    		else if(outputMessage instanceof MessageUpdateInventory)
		    		{
		    			// update inventory message received from client
		    			MessageUpdateInventory updateInvMsg = (MessageUpdateInventory)outputMessage;

		    			// resolve the player's email address
		    			String email = VMKServerPlayerData.getEmailFromUsername(updateInvMsg.getUsername());

		    			// update the player's inventory file
		    			FileOperations.saveInventory(email, updateInvMsg.getInventory());
		    		}
		    		else if(outputMessage instanceof MessageAddInventory)
		    		{
		    			// add inventory message received from client
		    			MessageAddInventory addInvMsg = (MessageAddInventory)outputMessage;

		    			// resolve the player's email address
		    			String email = VMKServerPlayerData.getEmailFromUsername(addInvMsg.getUsername());

		    			// update the player's inventory file
		    			FileOperations.appendInventory(email, addInvMsg.getItem());
		    		}
		    		else if(outputMessage instanceof MessageReconnectToServer)
		    		{
		    			// reconnect to server message received from client
		    			MessageReconnectToServer reconnectMsg = (MessageReconnectToServer)outputMessage;

		    			// update the character in the HashMap so we don't lose information
		    			roomID = reconnectMsg.getRoomID();
		    			VMKServerPlayerData.addCharacter(reconnectMsg.getCharacter().getUsername(), reconnectMsg.getCharacter(), reconnectMsg.getRoomID());

		    			// send the cached messages back to the client since he's re-connected now
		    			sendCachedMessages();
		    		}
		    		else if(outputMessage instanceof MessageGameAddUserToRoom)
		    		{
		    			//System.out.println("Game add user to room message received from client");

		    			// add user to GAME ROOM message received from client
		    			MessageGameAddUserToRoom addUserGameMsg = (MessageGameAddUserToRoom)outputMessage;

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
		    		else if(outputMessage instanceof MessageGameRemoveUserFromRoom)
		    		{
		    			// remove user from GAME ROOM message received from client
		    			MessageGameRemoveUserFromRoom removeUserGameMsg = (MessageGameRemoveUserFromRoom)outputMessage;

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
		    		else if(outputMessage instanceof MessageGameScore)
		    		{
		    			// add game score message received from client
		    			MessageGameScore gameScoreMsg = (MessageGameScore)outputMessage;

		    			// pass the message back to all clients in the current game room
		    			sendMessageToAllClientsInRoom(gameScoreMsg, roomID);
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
	    		if(se.getMessage().toLowerCase().contains("socket write error") || se.getMessage().toLowerCase().contains("abort"))
	    		{
	    			// try to re-boot this server thread
		    		rebootSocket();

		    		return;
	    		}
	    		
	    		// client shut down, so the connection was reset
	    		System.out.println("Client shutdown (" + this.getName() + ")");
	    	}
	    	catch(StreamCorruptedException sce)
	    	{
	    		// somehow the stream got corrupted; shut down the thread gracefully so the server doesn't hang
	    		System.out.println("Stream corrupted on client (" + this.getName() + ")");
	    		
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
	    		
	    		// try to re-boot this server thread
	    		rebootSocket();
	    		
	    		return;
	    	}
	    	catch(EOFException eofe)
	    	{
	    		// somehow the client-side stream got corrupted with an EOF exception; shut down the thread gracefully so the server doesn't hang
	    		System.out.println();
	    		System.out.println("Stream corrupted [invalid type code: client-side] on client (" + this.getName() + ")");
	    		System.out.println();
	    		
	    		// try to re-boot this server thread
	    		rebootSocket();

	    		return;
	    	}
	    	
	    	if(!waitingForReconnect)
	    	{
		    	System.out.println("VMKServerThread - Shutting down client socket [" + this.getName() + "]...");
		    	
		    	// update the player's status in the database (offline)
		    	updatePlayerStatusInDatabase(this.getName(), "offline");
		    	
		    	// shut down the server thread gracefully
		    	shutDownServerThreadGracefully();
		    	
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
		}
		catch (SocketException se)
		{
			// check to see if the software caused a connection abort or if there was a socket write error
			if(se.getMessage().toLowerCase().contains("abort") || se.getMessage().toLowerCase().contains("socket write error"))
			{
				// attempt to re-boot the socket
				rebootSocket();
			    return;
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
    private void shutDownServerThreadGracefully()
    {
    	// check to see if the fucking character exists
    	if(VMKServerPlayerData.getCharacter(this.getName()) == null)
    	{
    		System.out.println("THE FUCKING CHARACTER IS NULL.  WHY IS THE CHARACTER FUCKING NULL? [" + this.getName() + "]");
    	}
    	
    	// save the character to file
    	FileOperations.saveCharacter(VMKServerPlayerData.getCharacter(this.getName()));
		
		System.out.println("Saved character (" + this.getName() + ") to file");
		
		// remove the character's server thread
		serverThreads.remove(this);
		
		// set an offline status alteration message to this user's friends
		sendMessageToAllClients(new MessageAlterFriendStatus(this.getName(), false));
		
		// remove the character from the room
		sendMessageToAllClientsInRoom(new MessageRemoveUserFromRoom(this.getName(), roomID), roomID);
		
		// remove the character from the room on the server end
		VMKServerPlayerData.removeCharacter(this.getName(), roomID);
		
		this.interrupt(); // stop this server thread
    }
    
    // write a message to the output buffer to be sent to the client
    private synchronized void writeOutputToClient(Message m) throws SocketException, IOException
    {
    	//out.reset();
    	out.writeUnshared(m);
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
    				// send the cached message to the client
    				System.out.println("Sending cached message (" + cachedMessage.getType() + ") to client [" + this.getName() + "]...");
    				writeOutputToClient(cachedMessage);
    				
    				Thread.sleep(40);
    			}
    			catch(Exception e)
    			{
    				System.out.println("Could not send cached message (" + cachedMessage.getType() + ") to client [" + this.getName() + "]");
    				
    				// add the message back to the cached messages structure for later sending
    				cachedMessages.add(cachedMessage);
    				
    				// allow the client to re-connect
    				rebootSocket();
    			}
    		}
    	}
    }
    
    // add a message to the messages cache for later sending
    private synchronized void cacheMessage(Message m)
    {
    	// add the message to the cache
    	cachedMessages.add(m);
    	
    	System.out.println("Cached message (" + m.getType() + ") for client [" + this.getName() + "]");
    }
    
    // send a message to the client
    public synchronized void sendMessageToClient(Message m)
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
	    			// print out the error message
	    			System.out.println(se.getMessage());
	    			
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
    
    // send a message to ALL clients in a given room
    public synchronized void sendMessageToAllClientsInRoom(Message m, String room)
    {
    	//System.out.println("Sending message (" + m.getType() + ") to ALL clients...");
    	
    	for(int i = 0; i < serverThreads.size(); i++)
    	{
    		if(VMKServerPlayerData.roomContainsUser(serverThreads.get(i).getName(), room))
    		{
    		//System.out.println("Sending message (" + m.getType() + ") to " + serverThreads.get(i).getName());
    			serverThreads.get(i).sendMessageToClient(m);
    		}
    	}
    }
    
 // send a message to ALL clients in a given room EXCEPT a specified user
    public synchronized void sendMessageToAllClientsInRoom(Message m, String room, String exemptedUser)
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
    
    // update a player's status in the server-side database
    private void updatePlayerStatusInDatabase(String player, String status)
    {
    	String command = "";
    	
    	// figure out the command to issue
    	if(status.toLowerCase().equals("offline"))
    	{
    		command = "playerOffline";
    	}
    	else if(status.toLowerCase().equals("online"))
    	{
    		command = "playerOnline";
    	}
    	
    	// issue the command
    	try
    	{
    		Scanner s = new Scanner(new URL("http://vmk.burbankparanormal.com/game/playerControl.php?command=" + command + "&player=" + player).openStream());
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
}
