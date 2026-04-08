package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Feedback;
import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Repository.Interface.FeedbackRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class FeedbackRepoImpl implements FeedbackRepo {
    @PersistenceContext
    EntityManager em;

    @Override
    public void submitFeedback(Feedback feedback) {
        em.persist(feedback);
    }

    @Override
    public Optional<Feedback> getFeedbackByWriterAndMatch(User user, Match match) {
        try {
            Feedback feedback = em.createQuery("SELECT f FROM Feedback f WHERE f.match.id = :matchId AND f.writer.id = :writerId", Feedback.class)
                    .setParameter("matchId", match.getId())
                    .setParameter("writerId", user.getId())
                    .getSingleResult();
            return Optional.of(feedback);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
