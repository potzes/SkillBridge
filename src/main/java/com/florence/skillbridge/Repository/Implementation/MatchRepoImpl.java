package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.MatchStatus;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Repository.Interface.MatchRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatchRepoImpl implements MatchRepo {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Match> getMatches(User user) {
        return  em.createQuery("SELECT m FROM Match m WHERE m.sender.user.id = :userId OR m.receiver.user.id = :userId", Match.class)
                .setParameter("userId", user.getId())
                .getResultList();
    }

    @Override
    public Optional<Match> getMatch(User user, int matchId) {
        try {
            Match match = em.createQuery("SELECT m FROM Match m WHERE m.id = :id AND (m.sender.user.id = :userId OR m.receiver.user.id = :userId)", Match.class)
                    .setParameter("id", matchId)
                    .setParameter("userId", user.getId())
                    .getSingleResult();
            return Optional.of(match);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveMatch(Match match) {
        match.setStatus(em.getReference(MatchStatus.class, 1));
        em.persist(match);
    }

    @Override
    public Optional<Match> getOngoingMatch(User user) {
        try {
            Match match = em.createQuery("SELECT m FROM Match m WHERE (m.receiver.user = :user OR m.sender.user = :user) AND m.status = :ongoing", Match.class)
                    .setParameter("user", user)
                    .setParameter("ongoing", em.getReference(MatchStatus.class, 1))
                    .getSingleResult();
            return Optional.of(match);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Match> getOngoingMatch(User user, int matchId) {
        try {
            Match match = em.createQuery("SELECT m FROM Match m WHERE (m.receiver.user = :user OR m.sender.user = :user) AND m.status = :ongoing AND m.id = :matchId", Match.class)
                    .setParameter("user", user)
                    .setParameter("ongoing", em.getReference(MatchStatus.class, 1))
                    .setParameter("matchId", matchId)
                    .getSingleResult();
            return Optional.of(match);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void endMatch(Match match) {
        MatchStatus end = em.getReference(MatchStatus.class, 3);
        match.setStatus(end);
        em.merge(match);
    }
}
