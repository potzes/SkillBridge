package com.florence.skillbridge.DTO.Response;

import java.util.List;

public class SentRequestDTO {
    private int receiverId;
    private String receiverName;
    private int requestId;
    private String status;
    private Double receiverRating;
    private List<SkillDTO> offeredSkills;
    private SkillDTO requestedSkill;

    public SentRequestDTO(int receiverId, String receiverName, int requestId, String status, Double receiverRating, List<SkillDTO> offeredSkills, SkillDTO requestedSkill) {
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.requestId = requestId;
        this.status = status;
        this.receiverRating = receiverRating;
        this.offeredSkills = offeredSkills;
        this.requestedSkill = requestedSkill;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReceiverRating(Double receiverRating) {
        this.receiverRating = receiverRating;
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

    public Double getReceiverRating() {
        return receiverRating;
    }

    public List<SkillDTO> getOfferedSkills() {
        return offeredSkills;
    }

    public SkillDTO getRequestedSkill() {
        return requestedSkill;
    }
}
