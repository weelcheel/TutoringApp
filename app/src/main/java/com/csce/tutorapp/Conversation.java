package com.csce.tutorapp;

import java.util.ArrayList;

/**
 * Created by willh_000 on 11/9/2016.
 */

class ConversationMessage{
    private String authorID;
    private String message;

    public ConversationMessage(){
        authorID = "";
        message = "";
    }

    public ConversationMessage(String author, String msg){
        authorID = author;
        message = msg;
    }

    public String getAuthorID() { return authorID; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return message;
    }
}

public class Conversation {
    private String otherUserID;
    private ArrayList<ConversationMessage> messages;
    private String convoName;
    private String convoID;

    public Conversation(){
        otherUserID = "test";
        messages = new ArrayList<>();
        convoName = "Name";
        convoID = "test";
    }

    public Conversation(String otherID, String cName, String cID){
        otherUserID = otherID;
        convoName = cName;
        convoID = cID;
        messages = new ArrayList<>();
    }

    public String getOtherUserID() { return otherUserID; }
    public ArrayList<ConversationMessage> getMessages() { return messages; }
    public String getConvoName() { return convoName; }
    public String getConvoID() { return convoID; }
    public String toString() { return getConvoName(); }
}
