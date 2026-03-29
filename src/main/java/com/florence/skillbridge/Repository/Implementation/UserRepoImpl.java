package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Repository.Interface.UserRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class UserRepoImpl implements UserRepo {
    @PersistenceContext(name = "SkillBridgePU")
    EntityManager em;

    public Optional<User> findUserById(int id) {
        try {
            User user1 = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(user1);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Double getAverageRating(User user) {
        return em.createQuery("SELECT AVG(f.rating) FROM Feedback f WHERE f.receiver = :user", Double.class)
                .setParameter("user", user)
                .getSingleResult();
    }
}
