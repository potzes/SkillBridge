package com.florence.skillbridge.Domain.EmbeddableId;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MatchParticipantId implements Serializable {
    private int matchId;
    private int userId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MatchParticipantId that)) return false;
        return matchId == that.matchId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, userId);
    }
}
