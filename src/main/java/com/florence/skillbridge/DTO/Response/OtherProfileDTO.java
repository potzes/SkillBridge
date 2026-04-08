package com.florence.skillbridge.DTO.Response;

import java.util.List;

public class OtherProfileDTO {
    private String username;
    private Double averageRating;
    private String status;
    private List<UserSkillDTO> skills;
    private List<SkillDTO> interests;

    public OtherProfileDTO(String username, Double averageRating, String status, List<UserSkillDTO> skills, List<SkillDTO> interests) {
        this.username = username;
        this.averageRating = averageRating;
        this.status = status;
        this.skills = skills;
        this.interests = interests;
    }

    public String getUsername() {
        return username;
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
