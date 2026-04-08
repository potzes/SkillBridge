package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserSkill;
import com.florence.skillbridge.Repository.Interface.SkillRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SkillRepoImpl implements SkillRepo {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<Skill> getSkills() {
        return em.createQuery("SELECT s FROM Skill s", Skill.class)
                .getResultList();
    }

    @Override
    public Optional<Skill> findSkillByName(String name) {
        try {
            Skill skill = em.createQuery("SELECT s FROM Skill s WHERE s.name = :name", Skill.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(skill);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Skill> findSkillById(int id) {
        try {
            Skill skill = em.createQuery("SELECT s FROM Skill s WHERE s.id = :id", Skill.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(skill);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveSkill(Skill skill) {
        em.persist(skill);
    }

    @Override
    public Optional<List<Skill>> getOfferedSkills(User user, List<Integer> skillIds) {
        try {
            List<Skill> offeredSkills = em.createQuery("SELECT us.skill FROM UserSkill us WHERE us.user = :user AND us.skill.id IN :skillIds", Skill.class)
                    .setParameter("user", user)
                    .setParameter("skillIds", skillIds)
                    .getResultList();
            return Optional.of(offeredSkills);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
