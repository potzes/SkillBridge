package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.*;
import com.florence.skillbridge.Repository.Interface.RequestRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RequestRepoImpl implements RequestRepo {
    @PersistenceContext
    private EntityManager em;

    public void sendRequest(Request request) {
        em.persist(request);
    }

    public void saveOfferedSkills(List<OfferedSkill> offeredSkills) {
        for (OfferedSkill skill : offeredSkills) {
            em.persist(skill);
        }
    }

    public RequestStatus getRequestStatusById(int id) {
        return em.createQuery("SELECT rs FROM RequestStatus rs WHERE rs.id = :id", RequestStatus.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public Optional<Request> getActiveRequest(User sender, UserSkill receiver) {
        try {
            Request request = em.createQuery("SELECT r FROM Request r WHERE r.sender = :sender AND r.receiver = :receiver AND r.status.id = 1", Request.class)
                    .setParameter("sender", sender)
                    .setParameter("receiver", receiver)
                    .getSingleResult();
            return Optional.of(request);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    @Override
    public Optional<Request> getActiveRequest(User sender, User receiver) {
        try {
            Request request = em.createQuery("SELECT r FROM Request r WHERE r.sender = :sender AND r.receiver.user = :receiver AND r.status.id = 1", Request.class)
                    .setParameter("sender", sender)
                    .setParameter("receiver", receiver)
                    .getSingleResult();
            return Optional.of(request);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Request>> getReceivedRequests(User user) {
        try {
            List<Request> requests = em.createQuery("SELECT r FROM Request r WHERE r.receiver.user = :receiver", Request.class)
                    .setParameter("receiver", user)
                    .getResultList();
            return Optional.of(requests);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Request>> getSentRequests(User user) {
        try {
            List<Request> requests = em.createQuery("SELECT r FROM Request r WHERE r.sender = :sender", Request.class)
                    .setParameter("sender", user)
                    .getResultList();
            return Optional.of(requests);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Request> getSentRequest(User user, int requestId) {
        try {
            Request request = em.createQuery("SELECT r FROM Request r WHERE r.id = :id AND r.sender = :user", Request.class)
                    .setParameter("id", requestId)
                    .setParameter("user", user)
                    .getSingleResult();
            return Optional.of(request);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteSentRequest(Request request) {
        em.remove(request);
    }

    @Override
    public List<Skill> getOfferedSkills(Request request) {
         return em.createQuery("SELECT os.skill FROM OfferedSkill os WHERE os.request = :request", Skill.class)
                 .setParameter("request", request)
                 .getResultList();
    }

    @Override
    public Optional<Request> getReceivedRequestById(User user, int requestId) {
        try {
            Request request = em.createQuery("SELECT r FROM Request r WHERE r.id = :id AND r.status.id = 1 AND r.receiver.user = :user", Request.class)
                    .setParameter("id", requestId)
                    .setParameter("user", user)
                    .getSingleResult();
            return Optional.of(request);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void acceptRequest(Request request) {
        request.setStatus(em.getReference(RequestStatus.class, 3));
        em.merge(request);
    }

    @Override
    public void rejectRequest(Request request) {
        request.setStatus(em.getReference(RequestStatus.class, 2));
        em.merge(request);
        em.flush();
    }

    private RequestStatus getRequestStatus(int id) {
        return em.createQuery("SELECT rs FROM RequestStatus rs WHERE rs.id = :id", RequestStatus.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
