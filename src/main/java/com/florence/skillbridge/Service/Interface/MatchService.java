package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.FeedbackDTO;
import com.florence.skillbridge.DTO.Request.SendMessageDTO;
import com.florence.skillbridge.DTO.Response.MatchesDTO;
import com.florence.skillbridge.DTO.Response.PagedMessageDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;

import java.util.List;

public interface MatchService {
    List<MatchesDTO> getMatches(UserDTO userDTO);
    void endMatch(UserDTO userDTO, int matchId, String end);
    void submitFeedback(UserDTO userDTO, int matchId, FeedbackDTO feedbackDTO);
    void sendMessage(UserDTO userDTO, int matchId, SendMessageDTO sendMessageDTO);
    PagedMessageDTO getPagedMessages(UserDTO userDTO, int matchId, String page, String limit);
}
