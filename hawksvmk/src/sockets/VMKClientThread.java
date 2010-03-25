// VMKClientThread.java by Matt Fritz
// November 20, 2009
// CLIENT SIDE - Controls messages passed between the client and server

package sockets;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import astar.AStarCharacter;

import roomviewer.RoomViewerUI;
import sockets.messages.Message;
import sockets.messages.MessageAddChatToRoom;
import sockets.messages.MessageAddFriendConfirmation;
import sockets.messages.MessageAddFriendRequest;
import sockets.messages.MessageAddUserToRoom;
import sockets.messages.MessageAlterFriendStatus;
import sockets.messages.MessageGetCharactersInRoom;
import sockets.messages.MessageGetFriendsList;
import sockets.messages.MessageGetInventory;
import sockets.messages.MessageGetOfflineMailMessages;
import sockets.messages.MessageLogin;
import sockets.messages.MessageLogout;
import sockets.messages.MessageMoveCharacter;
import sockets.messages.MessageRemoveFriend;
import sockets.messages.MessageRemoveUserFromRoom;
import sockets.messages.MessageSendMailToUser;
import sockets.messages.VMKProtocol;
import util.MailMessage;

public class VMKClientThread extends Thread
{
    private Socket socket = null;
    
    ObjectOutputStream out;
    ObjectInputStream in;

    Message inputMessage; // input message sent from server
    Message outputMessage; // output message sent to server
    VMKProtocol vmkp = new VMKProtocol(); // message handler protocol
    
    RoomViewerUI uiObject; // reference to the client UI

    public VMKClientThread(Socket socket)
    {
    	super("VMKClientThread");
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
    	}
    }
    
    public void setUIObject(RoomViewerUI uiObject) {this.uiObject = uiObject;}

    // run the thread and process the responses from the server
    public void run()
    {
		try
		{
		    try
		    {
		    	// process message input from the server
			    while ((inputMessage = (Message)in.readUnshared()) != null)
			    {
			    	// get the response from an input message
					outputMessage = vmkp.processInput(inputMessage);

					if(outputMessage instanceof MessageLogin)
					{
						// login response from server
						MessageLogin loginMessage = (MessageLogin)outputMessage;
						System.out.println("Login response received from server");
						System.out.println("Changing thread name: " + ((MessageLogin)outputMessage).getName());
						
						// change the thread name
						this.setName(loginMessage.getName());
						
						// send an "Add To Room" message
						sendMessageToServer(new MessageAddUserToRoom(loginMessage.getCharacter(), "Boot Hill Shooting Gallery Guest Room"));
					}
					else if (outputMessage instanceof MessageLogout)
					{
						// logout/shutdown response received from server
						System.out.println("Logout response received from server for thread: " + this.getName());

					    break;
					}
					else if (outputMessage instanceof MessageGetCharactersInRoom)
					{
						MessageGetCharactersInRoom userMsg = (MessageGetCharactersInRoom)outputMessage;
						
						// get characters in room response received from server
						System.out.println("Get characters in room response received from server for thread: " + this.getName());
						
						for(int i = 0; i < userMsg.getCharacters().size(); i++)
						{
							// get the next character based upon the name of the thread received
							AStarCharacter nextCharacter = userMsg.getCharacter(i);
							
							// add the user to the current room
							uiObject.addCharacterToRoom(nextCharacter);
							//uiObject.addUserToRoom(nextCharacter.getUsername(), nextCharacter.getRow(), nextCharacter.getCol());
						}
					}
					else if (outputMessage instanceof MessageAddUserToRoom)
					{
						//MessageAddUserToRoom userMsg = (MessageAddUserToRoom)outputMessage;
						
						// user response received from server
						System.out.println("Add user to room response received from server for thread: " + this.getName());
						
						// get all characters currently in the room
						sendMessageToServer(new MessageGetCharactersInRoom("Boot Hill Shooting Gallery Guest Room"));
						
						// add the user to the current room
						//uiObject.addUserToRoom(userMsg.getUsername(), userMsg.getRow(), userMsg.getCol());
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
					
						// move the character in the current room
						uiObject.moveCharacter(moveMsg.getCharacter(), moveMsg.getDestGridX(), moveMsg.getDestGridY());
					}
					else if(outputMessage instanceof MessageAddFriendRequest)
					{
						// add friend response received from server
						MessageAddFriendRequest requestMsg = (MessageAddFriendRequest)outputMessage;
						
						System.out.println("FRIENDSHIP REQUESTED. Add friend request response received from server");
						
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
						MessageGetInventory getInvMsg = (MessageGetInventory)outputMessage;
						
						// get inventory message received from server
						System.out.println("Player inventory received from server");
						uiObject.setInventory(getInvMsg.getInventory());
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
	    		// server shut down, so the connection was reset
	    		System.out.println("Logout / Server shutdown");
	    		
	    		this.interrupt(); // stop this client thread
	    		
	    		// pop up a notification that the server shut down
	    		//JOptionPane.showMessageDialog(null, "Your connection has been lost because the server has shut down.\n\nPlease close the VMK window.", "Hawk's Virtual Magic Kingdom", JOptionPane.WARNING_MESSAGE);
	    	}
	    	
	    	// remove the current user from the current room
	    	//sendMessageToServer(new MessageRemoveUserFromRoom(uiObject.getUsername(), "Boot Hill Shooting Gallery Guest Room"));
	    	
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
    
    // send a message to the server
    public synchronized void sendMessageToServer(Message m)
    {
    	try
    	{
    		System.out.println("Sending message (" + m.getType() + ") to server...");
    		out.writeUnshared(m);
    		out.reset();
    		//out.flush();
    	}
    	catch(IOException e)
    	{
    		System.out.println("Could not send message (" + m.getType() + ") to server");
    	}
    	catch(Exception e)
    	{
    		// some other problem
    	}
    }
}
