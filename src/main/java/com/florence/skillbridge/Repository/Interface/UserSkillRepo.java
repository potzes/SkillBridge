package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserSkill;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepo {
    Optional<UserSkill> findUserSkillByUserSkillId(User user, Skill skill);
    List<UserSkill> findUserSkills(User user);
    void addSkillToUser(UserSkill userSkill);
    void deleteSkillToUser(UserSkill userSkill);
}
