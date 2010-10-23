// VMKProtocol.java by Matt Fritz
// November 20, 2009
// Interpret messages from the server and client

package sockets.messages;

import sockets.messages.games.*;

public class VMKProtocol
{
	// process client input and return an answer from the server
    public Message processInput(Message theInput)
    {
        Message theOutput = null;
        
        if(theInput instanceof MessageLogin)
        {
        	// pass back the login request
        	theOutput = (MessageLogin)theInput;
        	theOutput.setType("MessageLogin");
        }
        else if(theInput instanceof MessageLogout)
        {
        	// pass back the logout request
        	theOutput = (MessageLogout)theInput;
        	theOutput.setType("MessageLogout");
        }
        else if(theInput instanceof MessageChangeName)
        {
        	// pass back the name change request
        	theOutput = (MessageChangeName)theInput;
        	theOutput.setType("MessageChangeName");
        }
        else if(theInput instanceof MessageAddUserToRoom)
        {
        	// pass back the request
        	theOutput = (MessageAddUserToRoom)theInput;
        	theOutput.setType("MessageAddUserToRoom");
        }
        else if(theInput instanceof MessageAddChatToRoom)
        {
        	// pass back the request
        	theOutput = (MessageAddChatToRoom)theInput;
        	theOutput.setType("MessageAddChatToRoom");
        }
        else if(theInput instanceof MessageRemoveUserFromRoom)
        {
        	// pass back the request
        	theOutput = (MessageRemoveUserFromRoom)theInput;
        	theOutput.setType("MessageRemoveUserFromRoom");
        }
        else if(theInput instanceof MessageMoveCharacter)
        {
        	// pass back the request
        	theOutput = (MessageMoveCharacter)theInput;
        	theOutput.setType("MessageMoveCharacter");
        }
        else if(theInput instanceof MessageGetCharactersInRoom)
        {
        	// pass back the request
        	theOutput = (MessageGetCharactersInRoom)theInput;
        	theOutput.setType("MessageGetCharactersInRoom");
        }
        else if(theInput instanceof MessageUpdateCharacterInRoom)
        {
        	// pass back the request
        	theOutput = (MessageUpdateCharacterInRoom)theInput;
        	theOutput.setType("MessageUpdateCharacterInRoom");
        }
        else if(theInput instanceof MessageSendMailToUser)
        {
        	// pass back the request
        	theOutput = (MessageSendMailToUser)theInput;
        	theOutput.setType("MessageSendMailToUser");
        }
        else if(theInput instanceof MessageAddFriendRequest)
        {
        	// pass back the request
        	theOutput = (MessageAddFriendRequest)theInput;
        	theOutput.setType("MessageAddFriendRequest");
        }
        else if(theInput instanceof MessageAddFriendConfirmation)
        {
        	// pass back the request
        	theOutput = (MessageAddFriendConfirmation)theInput;
        	theOutput.setType("MessageAddFriendConfirmation");
        }
        else if(theInput instanceof MessageGetFriendsList)
        {
        	// pass back the request
        	theOutput = (MessageGetFriendsList)theInput;
        	theOutput.setType("MessageGetFriendsList");
        }
        else if(theInput instanceof MessageRemoveFriend)
        {
        	// pass back the request
        	theOutput = (MessageRemoveFriend)theInput;
        	theOutput.setType("MessageRemoveFriend");
        }
        else if(theInput instanceof MessageGetOfflineMailMessages)
        {
        	// pass back the request
        	theOutput = (MessageGetOfflineMailMessages)theInput;
        	theOutput.setType("MessageGetOfflineMailMessages");
        }
        else if(theInput instanceof MessageSaveMailMessages)
        {
        	// pass back the request
        	theOutput = (MessageSaveMailMessages)theInput;
        	theOutput.setType("MessageSaveMailMessages");
        }
        else if(theInput instanceof MessageAlterFriendStatus)
        {
        	// pass back the request
        	theOutput = (MessageAlterFriendStatus)theInput;
        	theOutput.setType("MessageAlterFriendStatus");
        }
        else if(theInput instanceof MessageGetInventory)
        {
        	// pass back the request
        	theOutput = (MessageGetInventory)theInput;
        	theOutput.setType("MessageGetInventory");
        }
        else if(theInput instanceof MessageUpdateInventory)
        {
        	// pass back the request
        	theOutput = (MessageUpdateInventory)theInput;
        	theOutput.setType("MessageUpdateInventory");
        }
        else if(theInput instanceof MessageAddInventory)
        {
        	// pass back the request
        	theOutput = (MessageAddInventory)theInput;
        	theOutput.setType("MessageAddInventory");
        }
        else if(theInput instanceof MessageUpdateItemInRoom)
        {
        	// pass back the request
        	theOutput = (MessageUpdateItemInRoom)theInput;
        	theOutput.setType("MessageUpdateItemInRoom");
        }
        else if(theInput instanceof MessageSaveGuestRoom)
        {
        	// pass back the request
        	theOutput = (MessageSaveGuestRoom)theInput;
        	theOutput.setType("MessageSaveGuestRoom");
        }
        else if(theInput instanceof MessageCreateGuestRoom)
        {
        	// pass back the request
        	theOutput = (MessageCreateGuestRoom)theInput;
        	theOutput.setType("MessageCreateGuestRoom");
        }
        else if(theInput instanceof MessageUpdateCharacterClothing)
        {
        	// pass back the request
        	theOutput = (MessageUpdateCharacterClothing)theInput;
        	theOutput.setType("MessageUpdateCharacterClothing");
        }
        else if(theInput instanceof MessageReconnectToServer)
        {
        	// pass back the request
        	theOutput = (MessageReconnectToServer)theInput;
        	theOutput.setType("MessageReconnectToServer");
        }
        else if(theInput instanceof MessageGameAddUserToRoom)
        {
        	// pass back the request
        	theOutput = (MessageGameAddUserToRoom)theInput;
        	theOutput.setType("MessageGameAddUserToRoom");
        }
        else if(theInput instanceof MessageGameRemoveUserFromRoom)
        {
        	// pass back the request
        	theOutput = (MessageGameRemoveUserFromRoom)theInput;
        	theOutput.setType("MessageGameRemoveUserFromRoom");
        }
        else if(theInput instanceof MessageGameScore)
        {
        	// pass back the request
        	theOutput = (MessageGameScore)theInput;
        	theOutput.setType("MessageGameScore");
        }

        return theOutput;
    }

}
