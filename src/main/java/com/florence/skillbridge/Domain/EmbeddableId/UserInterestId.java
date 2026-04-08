package com.florence.skillbridge.Domain.EmbeddableId;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserInterestId implements Serializable {
    private int userId;
    private int skillId;

    public UserInterestId(int userId, int skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    public UserInterestId() {
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public int getSkillId() {
        return skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserInterestId that)) return false;
        return userId == that.userId && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }
}
