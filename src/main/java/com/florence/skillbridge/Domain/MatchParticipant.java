package com.florence.skillbridge.Domain;

import com.florence.skillbridge.Domain.EmbeddableId.MatchParticipantId;
import jakarta.persistence.*;

@Entity
@Table(name = "match_participants")
public class MatchParticipant {
    @EmbeddedId
    private MatchParticipantId id;

    @ManyToOne
    @MapsId("matchId")
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "participant_id")
    private User participant;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill participantSkill;

    public void setId(MatchParticipantId id) {
        this.id = id;
    }

    public MatchParticipantId getId() {
        return id;
    }

    public void setParticipantSkill(Skill participantSkill) {
        this.participantSkill = participantSkill;
    }

    public Skill getParticipantSkill() {
        return participantSkill;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }
}
