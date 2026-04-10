package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Match;
import com.florence.skillbridge.Domain.Message;
import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Repository.Interface.MessageRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MessageRepoImpl implements MessageRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Message> getMessageById(int id) {
        try {
            Message message = em.createQuery("SELECT m FROM Message m WHERE m.id = :id", Message.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(message);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void sendMessage(Message message) {
        em.persist(message);
    }

    @Override
    public Optional<List<Message>> getMatchMessages(User user, Match match, int pageNum, int limitNum) {
        try {
            List<Message> messages = em.createQuery("SELECT m FROM Message m WHERE m.match.id = :matchId ORDER BY m.sentAt DESC", Message.class)
                    .setParameter("matchId", match.getId())
                    .setFirstResult(pageNum * limitNum)
                    .setMaxResults(limitNum)
                    .getResultList();
            return Optional.of(messages);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
