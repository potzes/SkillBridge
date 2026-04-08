package com.florence.skillbridge.Domain;

import com.florence.skillbridge.Domain.EmbeddableId.UserInterestId;
import jakarta.persistence.*;

@Entity
@Table(name = "user_interest")
public class UserInterest {
    @EmbeddedId
    private UserInterestId id = new UserInterestId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill interest;

    public UserInterest() {
    }

    public UserInterest(User user, Skill interest) {
        this.user = user;
        this.interest = interest;
    }

    public void setId(UserInterestId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public UserInterestId getId() {
        return id;
    }

    public Skill getSkill() {
        return interest;
    }

    public void setSkill(Skill interest) {
        this.interest = interest;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
