package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Response.SkillDTO;
import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Repository.Interface.SkillRepo;
import com.florence.skillbridge.Service.Interface.SkillService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SkillServiceImpl implements SkillService {
    @Inject
    SkillRepo skillRepo;

    public List<SkillDTO> getSkills() {
        List<Skill> skills = skillRepo.getSkills();
        List<SkillDTO> skillDTOS = new ArrayList<>();
        for(Skill skill : skills) {
            SkillDTO skillDTO = new SkillDTO(skill.getId(), skill.getName(), skill.getCategory().getName());
            skillDTOS.add(skillDTO);
        }
        return skillDTOS;
    }
}
