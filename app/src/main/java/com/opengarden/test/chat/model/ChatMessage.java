package com.opengarden.test.chat.model;

import com.opengarden.test.chat.utils.Utils;

/**
 * Created by salbury on 8/9/15.
 */
public class ChatMessage {
    public static final int MESSAGE = 0, LOGOUT = 1, LOGIN = 2;
    private int id;
    private int type;
    private String message;
    private String username;
    private long date;

    //Empty Constructor
    public ChatMessage() {

    }

    public ChatMessage(int id, String username, int type, String message, long date) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.date = date;
        this.message = message;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String msg = "";
        switch(type){
            case ChatMessage.LOGIN:
                msg = message = username + " just joined" + "\n";
                break;
            case ChatMessage.LOGOUT:
                //msg = mMessage  + "\n";
                msg = message = username + " has logged out." + "\n";
                break;
            case ChatMessage.MESSAGE:
                String time = Utils.formatDateTimeForDisplay(date);
                msg = time + " " + username + ": " + message + "\n";

                break;
        }


        return msg;
    }
}