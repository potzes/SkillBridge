package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Expertise;
import com.florence.skillbridge.Repository.Interface.ExpertiseRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class ExpertiseRepoImpl implements ExpertiseRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Expertise> findExpertiseById(int id) {
        try {
            Expertise expertise = em.createQuery("SELECT e FROM Expertise e WHERE e.id = :id", Expertise.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(expertise);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
