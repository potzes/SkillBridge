package com.florence.skillbridge.Util;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.UserStatus;

import java.util.List;

public class UserCard {
    private int id;
    private String username;
    private UserStatus status;
    private Double rating;
    private List<Skill> skills;
    private List<Skill> interests;

    public UserCard(int id, String username, UserStatus status, Double rating) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void setInterests(List<Skill> interests) {
        this.interests = interests;
    }

    public String getUsername() {
        return username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Double getRating() {
        return rating;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<Skill> getInterests() {
        return interests;
    }
}
