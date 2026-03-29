package com.florence.skillbridge.Domain.EmbeddableId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserSkillId implements Serializable {
    @Column(name = "user_id")
    private int userId;
    @Column(name = "skill_id")
    private Integer skillId;

    public UserSkillId() {}

    public UserSkillId(int userId, Integer skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserSkillId that)) return false;
        return userId == that.userId && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }
}

