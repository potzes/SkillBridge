package com.florence.skillbridge.DTO.Response;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.UserStatus;
import com.florence.skillbridge.Util.UserCard;

import java.util.ArrayList;
import java.util.List;

public class UserCardDTO {
    private int id;
    private String username;
    private String status;
    private Double rating;
    private List<SkillDTO> skills;
    private List<SkillDTO> interests;

    public UserCardDTO(UserCard userCard) {
        this.id = userCard.getId();
        this.username = userCard.getUsername();
        this.status = userCard.getStatus().getName();
        this.rating = userCard.getRating();
        this.skills = convertToSkillDTO(userCard.getSkills());
        this.interests = convertToSkillDTO(userCard.getInterests());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public void setInterests(List<SkillDTO> interests) {
        this.interests = interests;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public Double getRating() {
        return rating;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public List<SkillDTO> getInterests() {
        return interests;
    }

    private List<SkillDTO> convertToSkillDTO(List<Skill> skills) {
        List<SkillDTO> skillDTOS = new ArrayList<>();

        for(Skill skill : skills) {
            skillDTOS.add(new SkillDTO(skill.getId(), skill.getName(), skill.getCategory().getName()));
        }

        return skillDTOS;
    }
}
