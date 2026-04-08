package com.florence.skillbridge.Domain;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn(name = "reply_to")
    private Message replyTo;

    @Column(name = "sent_at")
    private Timestamp sentAt;

    @PrePersist
    protected void onCreate() {
        sentAt = Timestamp.valueOf(LocalDateTime.now());
    }

    protected Message() {
    }

    public Message(Match match, User sender, String message, Message replyTo) {
        this.match = match;
        this.sender = sender;
        this.message = message;
        this.replyTo = replyTo;
    }

    public Message(Match match, User sender, String message) {
        this.match = match;
        this.sender = sender;
        this.message = message;
    }

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

    public Message getReplyTo() {
        return replyTo;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public User getSender() {
        return sender;
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
