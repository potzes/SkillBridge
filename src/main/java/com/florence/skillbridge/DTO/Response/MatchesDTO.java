package com.florence.skillbridge.DTO.Response;

import java.sql.Timestamp;

public class MatchesDTO {
    private int id;
    private String status;
    private Timestamp startDate;
    private Timestamp endDate;
    private int senderId;
    private String senderUsername;
    private SkillDTO senderSkill;
    private int receiverId;
    private String receiverUsername;
    private SkillDTO receiverSkill;

    public MatchesDTO(int id, String status, Timestamp startDate, Timestamp endDate, int senderId, String senderUsername, SkillDTO senderSkill, int receiverId, String receiverUsername, SkillDTO receiverSkill) {
        this.id = id;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderSkill = senderSkill;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.receiverSkill = receiverSkill;
    }

    public MatchesDTO() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setSenderSkill(SkillDTO senderSkill) {
        this.senderSkill = senderSkill;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public void setReceiverSkill(SkillDTO receiverSkill) {
        this.receiverSkill = receiverSkill;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public SkillDTO getSenderSkill() {
        return senderSkill;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public SkillDTO getReceiverSkill() {
        return receiverSkill;
    }
}
