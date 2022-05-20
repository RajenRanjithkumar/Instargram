package com.restapi.insta.Model;

public class Chat {

private String sender;
private String receiver;
private String messageId;
private String message;
private String url;
private Boolean isSeen;



    public Chat(){}

    public Chat(String sender, String receiver, String messageId, String message, String url, Boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageId = messageId;
        this.message = message;
        this.url = url;
        this.isSeen = isseen;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(Boolean isSeen) {
        this.isSeen = isSeen;
    }
}
