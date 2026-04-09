package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Request.FeedbackDTO;
import com.florence.skillbridge.DTO.Request.SendMessageDTO;
import com.florence.skillbridge.DTO.Response.*;
import com.florence.skillbridge.Domain.Feedback;
import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.Message;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Repository.Interface.FeedbackRepo;
import com.florence.skillbridge.Repository.Interface.MatchRepo;
import com.florence.skillbridge.Repository.Interface.MessageRepo;
import com.florence.skillbridge.Repository.Interface.UserRepo;
import com.florence.skillbridge.Service.Interface.MatchService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class MatchServiceImpl implements MatchService {
    @Inject
    MatchRepo matchRepo;
    @Inject
    FeedbackRepo feedbackRepo;
    @Inject
    MessageRepo messageRepo;
    @Inject
    UserRepo userRepo;

    @Override
    public List<MatchesDTO> getMatches(UserDTO userDTO) {
        User user = getUser(userDTO);
        List<Match> matches = matchRepo.getMatches(user);
        List<MatchesDTO> matchesDTOS = new ArrayList<>();

        for(Match match : matches) {
            matchesDTOS.add(
                    new MatchesDTO(match.getId(), match.getStatus().getName(), match.getStartedAt()
                            , match.getEndedAt(), match.getSender().getUser().getId(), match.getSender().getUser().getUsername()
                            , new SkillDTO(match.getSender().getSkill()), match.getReceiver().getUser().getId(), match.getReceiver().getUser().getUsername()
                            , new SkillDTO(match.getReceiver().getSkill()))
            );
        }

        return matchesDTOS;
    }

    @Transactional
    @Override
    public void endMatch(UserDTO userDTO, int matchId, String end) {
        if(!end.equals("true")) throw new BadRequestException("Invalid parameter value");
        User user = getUser(userDTO);
        Match match = matchRepo.getOngoingMatch(user,matchId).orElseThrow(() -> new ResourceNotFoundException("No ongoing match to cancel"));
        matchRepo.endMatch(match);
    }

    @Transactional
    @Override
    public void submitFeedback(UserDTO userDTO, int matchId, FeedbackDTO feedbackDTO) {
        User user = getUser(userDTO);
        Match match = matchRepo.getMatch(user, matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        if(feedbackRepo.getFeedbackByWriterAndMatch(user, match).isPresent()) throw new ConflictException("You already submitted a feedback in this match");
        User receiver;

        if(user.getId() == match.getSender().getUser().getId()) {
            receiver = match.getReceiver().getUser();
        } else {
            receiver = match.getSender().getUser();
        }

        Feedback feedback = new Feedback(
                match, user, receiver, feedbackDTO.getMessage(), feedbackDTO.getRating()
        );

        feedbackRepo.submitFeedback(feedback);
    }

//---------------------Messages------------------------------------------------------------

    @Transactional
    @Override
    public void sendMessage(UserDTO userDTO, int matchId, SendMessageDTO sendMessageDTO) {
        User user = getUser(userDTO);
        Match match = matchRepo.getMatch(user, matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        if(sendMessageDTO.getMessage().isBlank()) throw new DTOValidationException(Map.of("Sending error", "Cannot send a blank message"));

        if(sendMessageDTO.getReplyTo() == 0) messageRepo.sendMessage(new Message(match, user, sendMessageDTO.getMessage()));
        else {
            Message replyTo = messageRepo.getMessageById(sendMessageDTO.getReplyTo()).orElseThrow(() -> new ResourceNotFoundException("Message replying to is not found"));

            messageRepo.sendMessage(new Message( match, user, sendMessageDTO.getMessage(), replyTo));
        }
    }

    @Override
    public PagedMessageDTO getPagedMessages(UserDTO userDTO, int matchId, String page, String limit) {
        User user = getUser(userDTO);
        Match match = matchRepo.getMatch(user, matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        int pageNum, limitNum;

        if(page == null || page.isBlank()) pageNum = 0;
        else pageNum = Integer.parseInt(page) - 1;
        if(limit == null || limit.isBlank()) limitNum = 20;
        else limitNum = Integer.parseInt(limit);

        PagedMessageDTO pagedMessageDTO = new PagedMessageDTO();
        Optional<List<Message>> optionalMessages = messageRepo.getMatchMessages(user, match, pageNum, limitNum);

        if(optionalMessages.isEmpty()) return pagedMessageDTO;
        List<Message> messages = optionalMessages.get();

        List<MessageDTO> messageDTOS = new ArrayList<>();

        for (Message message : messages) {
            if(message.getReplyTo() != null)
                messageDTOS.add(new MessageDTO(
                        message.getSender().getUsername(), message.getId(), message.getMessage(), message.getReplyTo().getId(), message.getSentAt()
                ));
            else
                messageDTOS.add(new MessageDTO(
                        message.getSender().getUsername(), message.getId(), message.getMessage(), message.getSentAt()
                ));
        }

        pagedMessageDTO.setMessages(messageDTOS);
        pagedMessageDTO.setLimit(limitNum);
        pagedMessageDTO.setPage(pageNum);

        return pagedMessageDTO;
    }

//============================== Helper Methods ======================================================

    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
