package com.florence.skillbridge.DTO.Response;

import com.florence.skillbridge.Domain.EmbeddableId.UserSkillId;

public class UserSkillDTO {
    private UserSkillId userSkillId;
    private String skill;
    private String category;
    private String expertise;
    private String proofLink;

    public UserSkillId getUserSkillId() {
        return userSkillId;
    }

    public void setUserSkillId(UserSkillId userSkillId) {
        this.userSkillId = userSkillId;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setProofLink(String proofLink) {
        this.proofLink = proofLink;
    }

    public String getSkill() {
        return skill;
    }

    public String getExpertise() {
        return expertise;
    }

    public String getProofLink() {
        return proofLink;
    }
}
