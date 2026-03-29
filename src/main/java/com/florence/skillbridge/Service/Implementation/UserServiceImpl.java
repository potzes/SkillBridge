package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Request.AddUserInterestDTO;
import com.florence.skillbridge.DTO.Request.AddUserSkillDTO;
import com.florence.skillbridge.DTO.Response.MeDTO;
import com.florence.skillbridge.DTO.Response.SkillDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;
import com.florence.skillbridge.DTO.Response.UserSkillDTO;
import com.florence.skillbridge.Domain.*;
import com.florence.skillbridge.Exception.ConflictException;
import com.florence.skillbridge.Exception.ResourceNotFoundException;
import com.florence.skillbridge.Repository.Interface.*;
import com.florence.skillbridge.Service.Interface.UserService;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public MeDTO getMyInfo(UserDTO userDTO) {
        User user = getUser(userDTO);

        List<UserSkill> userSkills = userSkillRepo.findUserSkills(user);
        List<Skill> userInterests = userInterestRepo.findUserInterests(user);

        return convertToMeDTO(user, userSkills, userInterests);
    }

    @Transactional
    @Override
    public void addSkillToUser(UserDTO userDTO, AddUserSkillDTO addSkill) {
        User user = getUser(userDTO);
        Optional<Skill> skillOptional = skillRepo.findSkillByName(addSkill.getName());

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

    public void addInterestToUser(UserDTO userDTO, AddUserInterestDTO addInterest) {
        User user = getUser(userDTO);
        Optional<Skill> skillOptional = skillRepo.findSkillByName(addInterest.getName());

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

    @Override
    public void deleteSkillToUser(int skillId, UserDTO userDTO) {
        User user = getUser(userDTO);
        Skill skill = skillRepo.findSkillById(skillId).orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        UserSkill userSkill = userSkillRepo.findUserSkillByUserSkillId(user, skill).orElseThrow(() -> new ResourceNotFoundException("This skill does not exist in this user"));
        userSkillRepo.deleteSkillToUser(userSkill);
    }

    @Override
    public void deleteInterestToUser(int skillId, UserDTO userDTO) {
        User user = getUser(userDTO);
        Skill skill = skillRepo.findSkillById(skillId).orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        UserInterest userInterest = userInterestRepo.findUserInterestByUserInterestId(user, skill).orElseThrow(() -> new ResourceNotFoundException("This skill does not exist in this user"));
        userInterestRepo.deleteInterestToUser(userInterest);
    }

//----------------------Helper Methods-----------------------------------------------------

    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private MeDTO convertToMeDTO(User user, List<UserSkill> userSkills, List<Skill> userInterests) {
        String username = user.getUsername();
        String email = user.getEmail();
        String status = user.getStatus().getName();
        Double averageRating = userRepo.getAverageRating(user);
        List<UserSkillDTO> userSkillResponse = convertUserSkillToDTO(userSkills);
        List<SkillDTO> userInterestsResponse = convertUserInterestsToDTO(userInterests);
        return new MeDTO(username, email, averageRating, status, userSkillResponse, userInterestsResponse);
    }

    @Nonnull
    private static List<UserSkillDTO> convertUserSkillToDTO(List<UserSkill> userSkills) {
        List<UserSkillDTO> userSkillResponse = new ArrayList<>();
        for(UserSkill userSkill : userSkills) {
            SkillDTO skillDTO = new SkillDTO();
            skillDTO.setName(userSkill.getSkill().getName());
            skillDTO.setCategory(userSkill.getSkill().getCategory().getName());

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
            SkillDTO userInterest = new SkillDTO();
            userInterest.setName(interest.getName());
            userInterest.setCategory(interest.getCategory().getName());

            userInterestsDTO.add(userInterest);
        }

        return userInterestsDTO;
    }

    private Category validatedCategory(int categoryId) {
        return categoryRepo.findCategoryById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category"));
    }

    private Expertise validatedExpertise(AddUserSkillDTO addSkill) {
        return expertiseRepo.findExpertiseById(addSkill.getExpertise()).orElseThrow(() -> new ResourceNotFoundException("Expertise not found"));
    }
}
