package com.florence.skillbridge.DTO.Request;

import java.util.List;

public class SendRequestDTO {
    private int requestedSkillId;
    private List<Integer> offeredSkillIds;
    private String message;

    public SendRequestDTO() {
    }

    public SendRequestDTO(int requestedSkillId, List<Integer> offeredSkillIds, String message) {
        this.requestedSkillId = requestedSkillId;
        this.offeredSkillIds = offeredSkillIds;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRequestedSkillId(int requestedSkillId) {
        this.requestedSkillId = requestedSkillId;
    }

    public void setOfferedSkillIds(List<Integer> offeredSkillIds) {
        this.offeredSkillIds = offeredSkillIds;
    }

    public int getRequestedSkillId() {
        return requestedSkillId;
    }

    public List<Integer> getOfferedSkillIds() {
        return offeredSkillIds;
    }
}
