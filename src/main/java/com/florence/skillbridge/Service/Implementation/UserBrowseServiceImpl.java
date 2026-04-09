package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Request.UserFilterDTO;
import com.florence.skillbridge.DTO.Response.*;
import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserSkill;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Repository.Interface.UserInterestRepo;
import com.florence.skillbridge.Repository.Interface.UserRepo;
import com.florence.skillbridge.Repository.Interface.UserSkillRepo;
import com.florence.skillbridge.Service.Interface.UserBrowseService;
import com.florence.skillbridge.Util.UserCard;
import com.florence.skillbridge.Util.UserFilter;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserBrowseServiceImpl implements UserBrowseService {
    @Inject
    UserSkillRepo userSkillRepo;
    @Inject
    UserRepo userRepo;
    @Inject
    UserInterestRepo userInterestRepo;

    @Override
    public OtherProfileDTO getOtherProfile(int id) {
        User user = getUser(id);

        return convertToOtherProfileDTO(user, userSkillRepo.findUserSkills(user), userInterestRepo.findUserInterests(user));
    }

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

//================================= Helper Methods ======================================================

    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User getUser(int id) {
        return userRepo.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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

    private Integer intParser(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid query parameter");
        }
    }
}
