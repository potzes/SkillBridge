package com.florence.skillbridge.DTO.Request;

public class UserFilterDTO {
    private String page;
    private String limit;
    private String category;
    private String expertise;
    private String rating;
    private String who;

    public UserFilterDTO(String page, String limit, String category, String expertise, String rating, String who) {
        this.page = page;
        this.limit = limit;
        this.category = category;
        this.expertise = expertise;
        this.rating = rating;
        this.who = who;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getPage() {
        return page;
    }

    public String getLimit() {
        return limit;
    }

    public String getCategory() {
        return category;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getRating() {
        return rating;
    }

    public String getWho() {
        return who;
    }
}
