package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.SendRequestDTO;
import com.florence.skillbridge.DTO.Response.ReceivedRequestDTO;
import com.florence.skillbridge.DTO.Response.SentRequestDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;

import java.util.List;

public interface RequestService {
    void sendRequest(int senderId, int receiverId, SendRequestDTO sendRequestDTO);

    void cancelRequest(UserDTO userDTO, int requestId);

    List<ReceivedRequestDTO> getReceivedRequests(UserDTO userDTO);

    List<SentRequestDTO> getSentRequests(UserDTO userDTO);

    void acceptRequest(UserDTO userDTO, int requestId, int chosenSkill);

    void rejectRequest(UserDTO userDTO, int requestId);
}
