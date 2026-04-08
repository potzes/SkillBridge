package com.florence.skillbridge.Domain;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private MatchStatus status;

    @Column(name = "started_at", insertable = false, updatable = false)
    private Timestamp startedAt;

    @Column(name = "ended_at")
    private Timestamp endedAt;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "sender_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "sender_skill_id", referencedColumnName = "skill_id")
    })
    private UserSkill sender;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "receiver_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "receiver_skill_id", referencedColumnName = "skill_id")
    })
    private UserSkill receiver;

    @PrePersist
    protected void onCreate() {
        startedAt = Timestamp.valueOf(LocalDateTime.now());
    }

    public UserSkill getSender() {
        return sender;
    }

    public UserSkill getReceiver() {
        return receiver;
    }

    public void setSender(UserSkill sender) {
        this.sender = sender;
    }

    public void setReceiver(UserSkill receiver) {
        this.receiver = receiver;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }
}
