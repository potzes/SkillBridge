package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Repository.Interface.SkillRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class SkillRepoImpl implements SkillRepo {
    @PersistenceContext
    EntityManager em;

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
}
