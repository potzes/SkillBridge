package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.*;
import com.florence.skillbridge.DTO.Response.*;

import java.util.List;

public interface UserService {
    MeDTO getMyInfo(UserDTO userDTO);
    void addSkillToUser(UserDTO userDTO, AddUserSkillDTO skillDTO);
    void addInterestToUser(UserDTO userDTO, AddUserInterestDTO interestDTO);
    void deleteSkillToUser(int skillId, UserDTO userDTO);
    void deleteInterestToUser(int skillId, UserDTO userDTO);
    PagedUsersDTO getFilteredUsers(UserFilterDTO userFilterDTO, UserDTO userDTO);
    OtherProfileDTO getOtherProfile(int id);
    void sendRequest(int senderId, int receiverId, SendRequestDTO sendRequestDTO);
    List<ReceivedRequestDTO> getReceivedRequests(UserDTO userDTO);
    List<SentRequestDTO> getSentRequests(UserDTO userDTO);
    void acceptRequest(UserDTO userDTO, int requestId, int chosenSkill);
    void rejectRequest(UserDTO userDTO, int requestId);
    void cancelRequest(UserDTO userDTO, int requestId);
    List<MatchesDTO> getMatches(UserDTO userDTO);
    void endMatch(UserDTO userDTO, int matchId, String end);
    void submitFeedback(UserDTO userDTO, int matchId, FeedbackDTO feedbackDTO);
    void sendMessage(UserDTO userDTO, int matchId, SendMessageDTO sendMessageDTO);
    PagedMessageDTO getPagedMessages(UserDTO userDTO, int matchId, String page, String limit);
}

