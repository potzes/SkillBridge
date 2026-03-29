package com.florence.skillbridge.Domain.EmbeddableId;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserInterestId implements Serializable {
    private int userId;
    private int skillId;

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
