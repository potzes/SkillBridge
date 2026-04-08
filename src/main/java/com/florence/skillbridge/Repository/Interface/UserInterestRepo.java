package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserInterest;
import com.florence.skillbridge.Domain.UserSkill;

import java.util.List;
import java.util.Optional;

public interface UserInterestRepo {
    Optional<UserInterest> findUserInterestByUserInterestId(User user, Skill skill);
    Optional<List<Skill>> findUserInterests(User user);
    void addInterestToUser(UserInterest userInterest);
    void deleteInterestToUser(UserInterest userInterest);
}
