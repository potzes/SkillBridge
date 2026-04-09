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
import com.florence.skillbridge.Service.Interface.ProfileService;
import com.florence.skillbridge.Util.DTOValidator;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProfileServiceImpl implements ProfileService {
    @Inject
    UserRepo userRepo;
    @Inject
    UserSkillRepo userSkillRepo;
    @Inject
    UserInterestRepo userInterestRepo;
    @Inject
    DTOValidator validator;
    @Inject
    SkillRepo skillRepo;
    @Inject
    CategoryRepo categoryRepo;
    @Inject
    ExpertiseRepo expertiseRepo;

    @Override
    public MeDTO getMyInfo(UserDTO userDTO) {
        User user = getUser(userDTO);

        return convertToMeDTO(user, userSkillRepo.findUserSkills(user), userInterestRepo.findUserInterests(user));
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

//============================ Helper Methods ============================================================

    private User getUser(UserDTO userDTO) {
        return userRepo.findUserById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
