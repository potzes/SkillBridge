package com.florence.skillbridge.DTO.Response;

import com.florence.skillbridge.Util.UserCard;

import java.util.List;

public class PagedUsersDTO {
    private List<UserCardDTO> userCards;
    private int page;
    private int totalPage;
    private Long totalCount;

    public PagedUsersDTO(List<UserCardDTO> userCards, int page, int totalPage, Long totalCount) {
        this.userCards = userCards;
        this.page = page;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }

    public void setUserCards(List<UserCardDTO> userCards) {
        this.userCards = userCards;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public List<UserCardDTO> getUserCards() {
        return userCards;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public Long getTotalCount() {
        return totalCount;
    }
}
