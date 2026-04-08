package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Request.*;
import com.florence.skillbridge.DTO.Response.*;
import com.florence.skillbridge.Domain.*;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.DTOValidationException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Repository.Interface.*;
import com.florence.skillbridge.Service.Interface.UserService;
import com.florence.skillbridge.Util.DTOValidator;
import com.florence.skillbridge.Util.UserCard;
import com.florence.skillbridge.Util.UserFilter;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {
    @Inject
    private UserRepo userRepo;
    @Inject
    private UserSkillRepo userSkillRepo;
    @Inject
    private SkillRepo skillRepo;
    @Inject
    private CategoryRepo categoryRepo;
    @Inject
    private ExpertiseRepo expertiseRepo;
    @Inject
    private UserInterestRepo userInterestRepo;
    @Inject
    private RequestRepo requestRepo;
    @Inject
    private DTOValidator validator;
    @Inject
    private MatchRepo matchRepo;
    @Inject
    private FeedbackRepo feedbackRepo;
    @Inject
    private MessageRepo messageRepo;

    @Override
    public MeDTO getMyInfo(UserDTO userDTO) {
        User user = getUser(userDTO);

        return convertToMeDTO(user, userSkillRepo.findUserSkills(user), userInterestRepo.findUserInterests(user));
    }

    @Override
    public OtherProfileDTO getOtherProfile(int id) {
        User user = getUser(id);

        return convertToOtherProfileDTO(user, userSkillRepo.findUserSkills(user), userInterestRepo.findUserInterests(user));
    }

    @Transactional
    @Override
    public void addSkillToUser(UserDTO userDTO, AddUserSkillDTO addSkill) {
        User user = getUser(userDTO);
        if(addSkill.getName() == null || addSkill.getCategory() == 0|| addSkill.getExpertise() == 0 || addSkill.getProofLink() == null)
            throw new BadRequestException("Missing JSON required field");
        validator.validate(addSkill);

        Optional<Skill> skillOptional = skillRepo.findSkillByName(addSkill.getName().trim().toUpperCase());

        if(skillOptional.isPresent()) {
            Skill skill = skillOptional.get();
            Optional<UserSkill> userSkillOptional = userSkillRepo.findUserSkillByUserSkillId(user, skill);
            if(userSkillOptional.isPresent()) throw new ConflictException("This skill is already added to the profile");

            UserSkill userSkill = new UserSkill(user, skill, validatedExpertise(addSkill), addSkill.getProofLink());
            userSkillRepo.addSkillToUser(userSkill);
        } else {
            Skill newSkill = new Skill(addSkill.getName().trim().toUpperCase(), validatedCategory(addSkill.getCategory()));
            skillRepo.saveSkill(newSkill);

            UserSkill userSkill = new UserSkill(user, newSkill, validatedExpertise(addSkill), addSkill.getProofLink());
            userSkillRepo.addSkillToUser(userSkill);
        }
    }

    @Transactional
    @Override
    public void addInterestToUser(UserDTO userDTO, AddUserInterestDTO addInterest) {
        User user = getUser(userDTO);
        if(addInterest.getName() == null || addInterest.getCategory() == 0)
            throw new BadRequestException("Missing JSON required field");
        validator.validate(addInterest);

        Optional<Skill> skillOptional = skillRepo.findSkillByName(addInterest.getName().trim().toUpperCase());

        if(skillOptional.isPresent()) {
            Skill skill = skillOptional.get();
            Optional<UserInterest> userInterestOptional = userInterestRepo.findUserInterestByUserInterestId(user, skill);
            if(userInterestOptional.isPresent()) throw new ConflictException("This skill interest is already added to the profile");

            UserInterest userInterest = new UserInterest(user, skill);
            userInterestRepo.addInterestToUser(userInterest);
        } else {
            Skill newSkill = new Skill(addInterest.getName().trim().toUpperCase(), validatedCategory(addInterest.getCategory()));
            skillRepo.saveSkill(newSkill);

            UserInterest userInterest = new UserInterest(user, newSkill);
            userInterestRepo.addInterestToUser(userInterest);
        }
    }

    @Transactional
    @Override
    public void deleteSkillToUser(int skillId, UserDTO userDTO) {
        User user = getUser(userDTO);
        Skill skill = skillRepo.findSkillById(skillId).orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        UserSkill userSkill = userSkillRepo.findUserSkillByUserSkillId(user, skill).orElseThrow(() -> new ResourceNotFoundException("This skill does not exist in this user"));
        userSkillRepo.deleteSkillToUser(userSkill);
    }

    @Transactional
    @Override
    public void deleteInterestToUser(int skillId, UserDTO userDTO) {
        User user = getUser(userDTO);
        Skill skill = skillRepo.findSkillById(skillId).orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        UserInterest userInterest = userInterestRepo.findUserInterestByUserInterestId(user, skill).orElseThrow(() -> new ResourceNotFoundException("This skill interest does not exist in this user"));
        userInterestRepo.deleteInterestToUser(userInterest);
    }

//----------------------------Users------------------------------------------------------------

@Override
public PagedUsersDTO getFilteredUsers(UserFilterDTO userFilterDTO, UserDTO userDTO) {

        UserFilter userFilter = new UserFilter();
        User user = getUser(userDTO);

        if(userFilterDTO.getWho() != null && !userFilterDTO.getWho().isBlank()) {
            if(userFilterDTO.getWho().equals("teach")) userFilter.setWho(true);
            else if(userFilterDTO.getWho().equals("learn")) userFilter.setWho(false);
        }
        else userFilter.setWho(true);

        if(userFilterDTO.getPage() != null && !userFilterDTO.getPage().isBlank()) userFilter.setPage(intParser(userFilterDTO.getPage()));
        else userFilter.setPage(0);

        if(userFilterDTO.getLimit() != null && !userFilterDTO.getLimit().isBlank()) userFilter.setLimit(intParser(userFilterDTO.getLimit()));
        else userFilter.setLimit(10);

        if(userFilterDTO.getRating() != null && !userFilterDTO.getRating().isBlank()) userFilter.setRating(intParser(userFilterDTO.getRating()));

        if(userFilterDTO.getCategory() != null && !userFilterDTO.getCategory().isBlank()) userFilter.setCategory(intParser(userFilterDTO.getCategory()));

        if(userFilterDTO.getExpertise() != null && !userFilterDTO.getExpertise().isBlank()) userFilter.setExpertise(intParser(userFilterDTO.getExpertise()));

        List<UserCard> userCards = userRepo.getUserCards(userFilter, user);
        List<UserCardDTO> userCardDTOS = new ArrayList<>();

        for(UserCard userCard : userCards) {
            userCardDTOS.add(new UserCardDTO(userCard));
        }
        Long count = userRepo.getUserCardsCount(userFilter, user);
        int pageCount = (int) Math.ceil((double) count / userFilter.getLimit());

        return new PagedUsersDTO(userCardDTOS, userFilter.getPage(), pageCount, count);
}

//-------------------------Requests------------------------------------------
    @Transactional
    @Override
    public void sendRequest(int senderId, int receiverId, SendRequestDTO sendRequestDTO) {
        User sender = userRepo.findUserById(senderId).orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepo.findUserById(receiverId).orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
        Skill requestedSkill = skillRepo.findSkillById(sendRequestDTO.getRequestedSkillId()).orElseThrow(() -> new ResourceNotFoundException("Requested skill not found"));
        UserSkill userSkill = userSkillRepo.findUserSkillByUserSkillId(receiver, requestedSkill).orElseThrow(() -> new ResourceNotFoundException("Requested skill not found"));


        if(requestRepo.getRequest(sender, userSkill).isPresent()) throw new ConflictException("Cannot send multiple pending request to same user");
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
            Request request1 = requestRepo.getRequest(sender, userSkill).orElseThrow(() -> new RuntimeException("Request saving error"));
            offeredSkills1.add(new OfferedSkill(request1, offeredSkill, sender));
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

                receivedRequestDTOS.add(new ReceivedRequestDTO(
                        request.getSender().getId()
                        , request.getSender().getUsername()
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

//-------------------------Matches------------------------------------------------------------

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
        Match match = matchRepo.getOngoingMatch(user).orElseThrow(() -> new ResourceNotFoundException("No ongoing match to cancel"));
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
                        user.getUsername(), message.getId(), message.getMessage(), message.getReplyTo().getId(), message.getSentAt()
                ));
            else
                messageDTOS.add(new MessageDTO(
                        user.getUsername(), message.getId(), message.getMessage(), message.getSentAt()
                ));
        }

        pagedMessageDTO.setMessages(messageDTOS);
        pagedMessageDTO.setLimit(limitNum);
        pagedMessageDTO.setPage(pageNum);

        return pagedMessageDTO;
    }


//----------------------Helper Methods-----------------------------------------------------

    private Integer intParser(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid query parameter");
        }
    }

    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User getUser(int id) {
        return userRepo.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private MeDTO convertToMeDTO(User user, Optional<List<UserSkill>> optionalUserSkills, Optional<List<Skill>> optionalUserInterest) {
        String username = user.getUsername();
        String email = user.getEmail();
        String status = user.getStatus().getName();
        Double averageRating = userRepo.getAverageRating(user);
        List<UserSkillDTO> userSkillResponse = new ArrayList<>();
        List<SkillDTO> userInterestsResponse = new ArrayList<>();


        if(optionalUserSkills.isPresent()) userSkillResponse = convertUserSkillToDTO(optionalUserSkills.get());
        if(optionalUserInterest.isPresent()) userInterestsResponse = convertUserInterestsToDTO(optionalUserInterest.get());

        return new MeDTO(username, email, averageRating, status, userSkillResponse, userInterestsResponse);
    }

    private OtherProfileDTO convertToOtherProfileDTO(User user, Optional<List<UserSkill>> optionalUserSkills, Optional<List<Skill>> optionalUserInterest) {
        String username = user.getUsername();
        String status = user.getStatus().getName();
        Double averageRating = userRepo.getAverageRating(user);
        List<UserSkillDTO> userSkillResponse = new ArrayList<>();
        List<SkillDTO> userInterestsResponse = new ArrayList<>();


        if(optionalUserSkills.isPresent()) userSkillResponse = convertUserSkillToDTO(optionalUserSkills.get());
        if(optionalUserInterest.isPresent()) userInterestsResponse = convertUserInterestsToDTO(optionalUserInterest.get());

        return new OtherProfileDTO(username, averageRating, status, userSkillResponse, userInterestsResponse);
    }

    @Nonnull
    private static List<UserSkillDTO> convertUserSkillToDTO(List<UserSkill> userSkills) {
        List<UserSkillDTO> userSkillResponse = new ArrayList<>();
        for(UserSkill userSkill : userSkills) {
            SkillDTO skillDTO = new SkillDTO
                    (userSkill.getSkill().getId(), userSkill.getSkill().getName(), userSkill.getSkill().getCategory().getName());

            UserSkillDTO userSkillDTO = new UserSkillDTO();
            userSkillDTO.setUserSkillId(userSkill.getUserSkillId());
            userSkillDTO.setExpertise(userSkill.getExpertise().getName());
            userSkillDTO.setProofLink(userSkill.getProofLink());
            userSkillDTO.setSkill(skillDTO.getName());
            userSkillDTO.setCategory(skillDTO.getCategory());

            userSkillResponse.add(userSkillDTO);
        }
        return userSkillResponse;
    }

    private static List<SkillDTO> convertUserInterestsToDTO (List<Skill> userInterests) {
        List<SkillDTO> userInterestsDTO = new ArrayList<>();

        for(Skill interest : userInterests) {
            SkillDTO userInterest = new SkillDTO(interest.getId(), interest.getName(), interest.getCategory().getName());

            userInterestsDTO.add(userInterest);
        }

        return userInterestsDTO;
    }

    private Category validatedCategory(int categoryId) {
        return categoryRepo.findCategoryById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Invalid category"));
    }

    private Expertise validatedExpertise(AddUserSkillDTO addSkill) {
        return expertiseRepo.findExpertiseById(addSkill.getExpertise()).orElseThrow(() -> new ResourceNotFoundException("Expertise not found"));
    }
}
