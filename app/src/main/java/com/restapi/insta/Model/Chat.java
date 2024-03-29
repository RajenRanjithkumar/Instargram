package com.restapi.insta.Model;

public class Chat {

private String sender;
private String receiver;
private String messageId;
private String message;
private String lastMessage;
private String url;
private Boolean isSeen;
private Boolean messageReceived;



    public Chat(){}

    public Chat(String sender, String receiver, String messageId, String message, String lastMessage, String url, Boolean isSeen, Boolean messageReceived) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageId = messageId;
        this.message = message;
        this.lastMessage = lastMessage;
        this.url = url;
        this.isSeen = isSeen;
        this.messageReceived = messageReceived;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(Boolean seen) {
        isSeen = seen;
    }

    public Boolean getMessageReceived() {
        return messageReceived;
    }

    public void setMessageReceived(Boolean messageReceived) {
        this.messageReceived = messageReceived;
    }
}
