package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.*;

import java.util.List;
import java.util.Optional;

public interface RequestRepo {
    void sendRequest(Request request);
    void saveOfferedSkills(List<OfferedSkill> offeredSkills);
    RequestStatus getRequestStatusById(int id);
    Optional<Request> getActiveRequest(User sender, UserSkill receiver);
    Optional<Request> getActiveRequest(User sender, User receiver);
    Optional<List<Request>> getReceivedRequests(User user);
    Optional<List<Request>> getSentRequests(User user);
    Optional<Request> getSentRequest(User user, int requestId);
    void deleteSentRequest(Request request);
    List<Skill> getOfferedSkills(Request request);
    Optional<Request> getReceivedRequestById(User user, int requestId);
    void acceptRequest(Request request);
    void rejectRequest(Request request);
}
