package com.florence.skillbridge.DTO.Response;

import java.util.List;

public class ReceivedRequestDTO {
    private int senderId;
    private String senderName;
    private String message;
    private int requestId;
    private String status;
    private Double senderRating;
    private List<SkillDTO> offeredSkills;
    private SkillDTO requestedSkill;

    public ReceivedRequestDTO(int senderId, String senderName, int requestId, String status, Double senderRating, List<SkillDTO> offeredSkills, SkillDTO requestedSkill) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.requestId = requestId;
        this.status = status;
        this.senderRating = senderRating;
        this.offeredSkills = offeredSkills;
        this.requestedSkill = requestedSkill;
    }

    public ReceivedRequestDTO(int senderId, String senderName, String message, int requestId, String status, Double senderRating, List<SkillDTO> offeredSkills, SkillDTO requestedSkill) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.requestId = requestId;
        this.status = status;
        this.senderRating = senderRating;
        this.offeredSkills = offeredSkills;
        this.requestedSkill = requestedSkill;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSenderRating(Double senderRating) {
        this.senderRating = senderRating;
    }

    public void setOfferedSkills(List<SkillDTO> offeredSkills) {
        this.offeredSkills = offeredSkills;
    }

    public void setRequestedSkill(SkillDTO requestedSkill) {
        this.requestedSkill = requestedSkill;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public Double getSenderRating() {
        return senderRating;
    }

    public List<SkillDTO> getOfferedSkills() {
        return offeredSkills;
    }

    public SkillDTO getRequestedSkill() {
        return requestedSkill;
    }
}
