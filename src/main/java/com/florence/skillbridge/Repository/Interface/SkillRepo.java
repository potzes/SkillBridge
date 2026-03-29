package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Skill;

import java.util.Optional;

public interface SkillRepo {
    Optional<Skill> findSkillByName(String name);
    Optional<Skill> findSkillById(int id);
    void saveSkill(Skill skill);
}
