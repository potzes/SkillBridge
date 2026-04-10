package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Request.SendRequestDTO;
import com.florence.skillbridge.DTO.Response.ReceivedRequestDTO;
import com.florence.skillbridge.DTO.Response.SentRequestDTO;
import com.florence.skillbridge.DTO.Response.SkillDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.Domain.*;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Repository.Interface.*;
import com.florence.skillbridge.Service.Interface.RequestService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RequestServiceImpl implements RequestService {
    @Inject
    UserRepo userRepo;
    @Inject
    MatchRepo matchRepo;
    @Inject
    SkillRepo skillRepo;
    @Inject
    UserSkillRepo userSkillRepo;
    @Inject
    RequestRepo requestRepo;

    @Transactional
    @Override
    public void sendRequest(int senderId, int receiverId, SendRequestDTO sendRequestDTO) {
        User sender = userRepo.findUserById(senderId).orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        if(matchRepo.getOngoingMatch(sender).isPresent()) throw new BadRequestException("You can't send match request while having an ongoing match");
        User receiver = userRepo.findUserById(receiverId).orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Optional<Request> requestOptional = requestRepo.getActiveRequest(sender, receiver);
        if(requestOptional.isPresent()) throw new ConflictException("You already sent a request to this user.");

        Skill requestedSkill = skillRepo.findSkillById(sendRequestDTO.getRequestedSkillId()).orElseThrow(() -> new ResourceNotFoundException("Requested skill not found"));
        UserSkill userSkill = userSkillRepo.findUserSkillByUserSkillId(receiver, requestedSkill).orElseThrow(() -> new ResourceNotFoundException("Requested skill not found"));


        if(requestRepo.getActiveRequest(sender, userSkill).isPresent()) throw new ConflictException("Cannot send multiple pending request to same user");
        Request request = new Request();
        request.setSender(sender);
        request.setReceiver(userSkill);
        request.setStatus(requestRepo.getRequestStatusById(1));
        if(!sendRequestDTO.getMessage().isBlank()) {
            request.setMessage(sendRequestDTO.getMessage());
        }

        requestRepo.sendRequest(request);

        List<Skill> offeredSkills = skillRepo.getOfferedSkills(sender,sendRequestDTO.getOfferedSkillIds()).orElseThrow(() -> new ResourceNotFoundException("Offered skill does not exist in sender profile"));
        List<OfferedSkill> offeredSkills1 = new ArrayList<>();
        for (Skill offeredSkill : offeredSkills) {
            offeredSkills1.add(new OfferedSkill(request, offeredSkill, sender));
        }

        requestRepo.saveOfferedSkills(offeredSkills1);
    }

    @Override
    public List<ReceivedRequestDTO> getReceivedRequests(UserDTO userDTO) {
        User user = getUser(userDTO);
        Optional<List<Request>> receivedRequests = requestRepo.getReceivedRequests(user);
        List<ReceivedRequestDTO> receivedRequestDTOS = new ArrayList<>();

        if(receivedRequests.isPresent()) {
            List<Request> requests = receivedRequests.get();
            for(Request request : requests) {
                List<Skill> skills = requestRepo.getOfferedSkills(request);
                List<SkillDTO> skillDTOS = new ArrayList<>();
                for(Skill skill : skills) {
                    skillDTOS.add(new SkillDTO(skill));
                }

                if(request.getMessage() == null)
                receivedRequestDTOS.add(new ReceivedRequestDTO(
                        request.getSender().getId()
                        , request.getSender().getUsername()
                        , request.getId()
                        , request.getStatus().getName()
                        , userRepo.getAverageRating(request.getSender())
                        , skillDTOS
                        , new SkillDTO(request.getRequestedSkill())
                ));
                else
                    receivedRequestDTOS.add(new ReceivedRequestDTO(
                        request.getSender().getId()
                        , request.getSender().getUsername()
                        , request.getMessage()
                        , request.getId()
                        , request.getStatus().getName()
                        , userRepo.getAverageRating(request.getSender())
                        , skillDTOS
                        , new SkillDTO(request.getRequestedSkill())
                ));
            }

            return receivedRequestDTOS;
        } else {
            return receivedRequestDTOS;
        }
    }

    @Override
    public List<SentRequestDTO> getSentRequests(UserDTO userDTO) {
        User user = getUser(userDTO);
        Optional<List<Request>> sentRequests = requestRepo.getSentRequests(user);
        List<SentRequestDTO> sentRequestDTOS = new ArrayList<>();

        if(sentRequests.isPresent()) {
            List<Request> requests = sentRequests.get();
            for(Request request : requests) {
                List<Skill> skills = requestRepo.getOfferedSkills(request);
                List<SkillDTO> skillDTOS = new ArrayList<>();
                for(Skill skill : skills) {
                    skillDTOS.add(new SkillDTO(skill));
                }

                sentRequestDTOS.add(new SentRequestDTO(
                        request.getReceiver().getId()
                        , request.getReceiver().getUsername()
                        , request.getId()
                        , request.getStatus().getName()
                        , userRepo.getAverageRating(request.getReceiver())
                        , skillDTOS
                        , new SkillDTO(request.getRequestedSkill())
                ));
            }

            return sentRequestDTOS;
        } else {
            return sentRequestDTOS;
        }
    }

    @Transactional
    @Override
    public void acceptRequest(UserDTO userDTO, int requestId, int chosenSkill) {
        User user = getUser(userDTO);
        if(matchRepo.getOngoingMatch(user).isPresent()) throw new BadRequestException("User can only have one ongoing match at a time");
        Request request = requestRepo.getReceivedRequestById(user, requestId).orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        requestRepo.acceptRequest(request);
        Match match = new Match();
        Skill skill = skillRepo.findSkillById(chosenSkill).orElseThrow(() -> new ResourceNotFoundException("Chosen skill not found"));
        match.setSender(userSkillRepo.findUserSkillByUserSkillId(request.getSender(), skill).orElseThrow(() -> new ResourceNotFoundException("Sender user skill not found")));
        match.setReceiver(userSkillRepo.findUserSkillByUserSkillId(request.getReceiver(), request.getRequestedSkill()).orElseThrow(() -> new ResourceNotFoundException("Receiver user skill not found")));
        matchRepo.saveMatch(match);
    }

    @Transactional
    @Override
    public void rejectRequest(UserDTO userDTO, int requestId) {
        User user = getUser(userDTO);
        Request request = requestRepo.getReceivedRequestById(user, requestId).orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        requestRepo.rejectRequest(request);
    }

    @Transactional
    @Override
    public void cancelRequest(UserDTO userDTO, int requestId) {
        User user = getUser(userDTO);
        Request request = requestRepo.getSentRequest(user, requestId).orElseThrow(() -> new ResourceNotFoundException("This request does not exist"));
        requestRepo.deleteSentRequest(request);
    }

//================================ Helper Methods ==========================================================


    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
