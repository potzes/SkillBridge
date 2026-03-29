package com.florence.skillbridge.Domain;

import com.florence.skillbridge.Domain.EmbeddableId.UserInterestId;
import jakarta.persistence.*;

@Entity
@Table(name = "user_interest")
public class UserInterest {
    @EmbeddedId
    private UserInterestId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill interest;

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

    public Skill getInterest() {
        return interest;
    }

    public void setInterest(Skill interest) {
        this.interest = interest;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
