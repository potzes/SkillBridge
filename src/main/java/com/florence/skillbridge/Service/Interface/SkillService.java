package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Response.SkillDTO;
import com.florence.skillbridge.Domain.Skill;

import java.util.List;

public interface SkillService {
    List<SkillDTO> getSkills();
}
