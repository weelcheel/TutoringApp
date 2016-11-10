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

    public Conversation(){
        otherUserID = "test";
        messages = new ArrayList<>();
    }

    public String getOtherUserID() { return otherUserID; }
    public ArrayList<ConversationMessage> getMessages() { return messages; }
}
