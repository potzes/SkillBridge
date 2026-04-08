package com.florence.skillbridge.DTO.Response;

import java.util.List;

public class PagedMessageDTO {
    private int page;
    private int limit;
    private List<MessageDTO> messages;

    public PagedMessageDTO() {
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }
}
