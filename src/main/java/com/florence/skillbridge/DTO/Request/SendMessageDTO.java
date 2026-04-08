package com.florence.skillbridge.DTO.Request;

public class SendMessageDTO {
    private int replyTo;
    private String message;

    public SendMessageDTO() {
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public String getMessage() {
        return message;
    }
}
