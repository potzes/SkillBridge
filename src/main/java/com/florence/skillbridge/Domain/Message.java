package com.florence.skillbridge.Domain;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "match_id", referencedColumnName = "match_id"),
            @JoinColumn(name = "sender_id", referencedColumnName = "participant_id")
    })
    private MatchParticipant sender;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn(name = "reply_to")
    private Message replyTo;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(MatchParticipant sender) {
        this.sender = sender;
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public User getSender() {
        return sender.getParticipant();
    }

    public Match getMatch() {
        return sender.getMatch();
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setReplyTo(Message replyTo) {
        this.replyTo = replyTo;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
