package com.florence.skillbridge.Domain.EmbeddableId;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OfferedSkillId implements Serializable {
    private int requestId;
    private int skillId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OfferedSkillId that)) return false;
        return requestId == that.requestId && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, skillId);
    }
}
