package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Skill;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Domain.UserInterest;
import com.florence.skillbridge.Repository.Interface.UserInterestRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserInterestRepoImpl implements UserInterestRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<UserInterest> findUserInterestByUserInterestId(User user, Skill interest) {
        try {
            UserInterest userInterest = em.createQuery("SELECT ui FROM UserInterest ui WHERE ui.user = :user AND ui.interest = :interest", UserInterest.class)
                    .setParameter("user", user)
                    .setParameter("interest", interest)
                    .getSingleResult();

            return Optional.of(userInterest);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Skill> findUserInterests(User user) {
        return em.createQuery("SELECT ui.interest FROM UserInterest ui WHERE ui.user = :user", Skill.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public void addInterestToUser(UserInterest userInterest) {
        em.persist(userInterest);
    }

    @Override
    public void deleteInterestToUser(UserInterest userInterest) {
        em.remove(userInterest);
    }
}
