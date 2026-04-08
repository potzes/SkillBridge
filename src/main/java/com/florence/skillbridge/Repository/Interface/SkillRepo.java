package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserSkill;

import java.util.List;
import java.util.Optional;

public interface SkillRepo {
    List<Skill> getSkills();
    Optional<Skill> findSkillByName(String name);
    Optional<Skill> findSkillById(int id);
    void saveSkill(Skill skill);
    Optional<List<Skill>> getOfferedSkills(User user,List<Integer> skillIds);
}
