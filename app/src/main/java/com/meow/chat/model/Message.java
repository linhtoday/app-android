package com.meow.chat.model;

public class Message {

    private String message, type;

    private String from;

    private boolean seen;


    public Message(){

    }


    public Message(String message, String type, boolean seen, String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
