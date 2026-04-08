package com.florence.skillbridge.Util;

public class UserFilter {
    private Integer page;
    private Integer limit;
    private Integer category;
    private Integer expertise;
    private Integer rating;
    private Boolean who;

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setExpertise(Integer expertise) {
        this.expertise = expertise;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setWho(Boolean who) {
        this.who = who;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getCategory() {
        return category;
    }

    public Integer getExpertise() {
        return expertise;
    }

    public Integer getRating() {
        return rating;
    }

    public Boolean getWho() {
        return who;
    }
}
