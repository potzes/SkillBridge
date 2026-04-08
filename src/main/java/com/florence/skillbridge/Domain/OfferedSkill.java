package com.florence.skillbridge.Domain;

import com.florence.skillbridge.Domain.EmbeddableId.OfferedSkillId;
import jakarta.persistence.*;

@Entity
@Table(name = "offered_skills")
public class OfferedSkill {
    @EmbeddedId
    private OfferedSkillId id = new OfferedSkillId();

    @ManyToOne
    @MapsId("requestId")
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    public OfferedSkill() {
    }

    public OfferedSkill(Request request, Skill skill, User sender) {
        this.request = request;
        this.skill = skill;
        this.sender = sender;
    }



    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setId(OfferedSkillId id) {
        this.id = id;
    }

    public OfferedSkillId getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public Skill getSkill() {
        return skill;
    }

    public Request getRequest() {
        return request;
    }



    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
