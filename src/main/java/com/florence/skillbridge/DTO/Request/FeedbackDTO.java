package com.florence.skillbridge.DTO.Request;

public class FeedbackDTO {
    private int rating;
    private String message;

    public FeedbackDTO() {
    }

    public int getRating() {
        return rating;
    }

    public String getMessage() {
        return message;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
