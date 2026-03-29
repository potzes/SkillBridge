package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserSkill;
import com.florence.skillbridge.Repository.Interface.UserSkillRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserSkillRepoImpl implements UserSkillRepo {
    @PersistenceContext(name = "SkillBridgePU")
    EntityManager em;

    @Override
    public Optional<UserSkill> findUserSkillByUserSkillId(User user, Skill skill) {
        try{
            UserSkill userSkill = em.createQuery("SELECT us FROM UserSkill us WHERE us.user = :user AND us.skill = :skill", UserSkill.class)
                    .setParameter("user", user)
                    .setParameter("skill", skill)
                    .getSingleResult();
            return Optional.of(userSkill);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<UserSkill> findUserSkills(User user) {
        return em.createQuery("SELECT us FROM UserSkill us WHERE us.user = :user", UserSkill.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Skill> findUserInterests(User user) {
        return em.createQuery("SELECT s FROM UserInterest ui JOIN Skill s ON ui.interest = s WHERE ui.user = :user", Skill.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public void addSkillToUser(UserSkill userSkill) {
        em.persist(userSkill);
    }

    @Override
    public void deleteSkillToUser(UserSkill userSkill) {
        em.remove(userSkill);
    }

}
