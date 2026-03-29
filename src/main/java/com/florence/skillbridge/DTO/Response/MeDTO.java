package com.florence.skillbridge.DTO.Response;

import java.util.List;

public class MeDTO {
    private String username;
    private String email;
    private Double averageRating;
    private String status;
    private List<UserSkillDTO> skills;
    private List<SkillDTO> interests;

    public MeDTO(String username, String email, Double averageRating, String status, List<UserSkillDTO> skills, List<SkillDTO> interests) {
        this.username = username;
        this.email = email;
        this.averageRating = averageRating;
        this.status = status;
        this.skills = skills;
        this.interests = interests;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSkills(List<UserSkillDTO> skills) {
        this.skills = skills;
    }

    public void setInterests(List<SkillDTO> interests) {
        this.interests = interests;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public String getStatus() {
        return status;
    }

    public List<UserSkillDTO> getSkills() {
        return skills;
    }

    public List<SkillDTO> getInterests() {
        return interests;
    }
}
