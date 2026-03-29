package com.florence.skillbridge.Domain;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "match_id", referencedColumnName = "match_id"),
            @JoinColumn(name = "writer_id", referencedColumnName = "participant_id")
    })
    private MatchParticipant writer;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column
    private String message;

    @Column
    private int rating;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getRating() {
        return rating;
    }

    public User getWriter() {
        return writer.getParticipant();
    }

    public Match getMatch() {
        return writer.getMatch();
    }

    public void setWriter(MatchParticipant writer) {
        this.writer = writer;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getReceiver() {
        return receiver;
    }

}
