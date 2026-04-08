package com.florence.skillbridge.DTO.Response;

import java.sql.Timestamp;

public class MessageDTO {
    private String senderName;
    private int messageId;
    private String message;
    private int replyTo;
    private Timestamp timestamp;

    public MessageDTO(String senderName, int messageId, String message, int replyTo, Timestamp timestamp) {
        this.senderName = senderName;
        this.messageId = messageId;
        this.message = message;
        this.replyTo = replyTo;
        this.timestamp = timestamp;
    }

    public MessageDTO(String senderName, int messageId, String message, Timestamp timestamp) {
        this.senderName = senderName;
        this.messageId = messageId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
