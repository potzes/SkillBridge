package com.florence.skillbridge.Domain;

import com.florence.skillbridge.Domain.EmbeddableId.UserSkillId;
import jakarta.persistence.*;

@Entity
@Table(name = "user_skill")
public class UserSkill {
    @EmbeddedId
    private UserSkillId userSkillId = new UserSkillId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "expertise_id")
    private Expertise expertise;

    @Column(name = "proof_link")
    private String proofLink;

    public UserSkill(User user, Skill skill, Expertise expertise, String proofLink) {
        this.user = user;
        this.skill = skill;
        this.expertise = expertise;
        this.proofLink = proofLink;
    }

    public UserSkill() {
    }

    public UserSkillId getUserSkillId() {
        return userSkillId;
    }

    public void setUserSkillId(UserSkillId userSkillId) {
        this.userSkillId = userSkillId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public String getProofLink() {
        return proofLink;
    }

    public void setProofLink(String proofLink) {
        this.proofLink = proofLink;
    }
}
