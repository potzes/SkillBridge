package com.florence.skillbridge.Domain;

import jakarta.persistence.*;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "receiver_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "requested_skill_id", referencedColumnName = "skill_id")
    })
    private UserSkill receiver;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "status_id")
    private RequestStatus status;

    @Column
    private String message;

    public void setReceiver(UserSkill receiver) {
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getReceiver() {
        return receiver.getUser();
    }

    public Skill getRequestedSkill() {
        return receiver.getSkill();
    }
}
