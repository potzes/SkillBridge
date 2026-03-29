package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.Category;
import com.florence.skillbridge.Repository.Interface.CategoryRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepoImpl implements CategoryRepo {
    @PersistenceContext
    EntityManager em;

    @Override
    public List<Category> getCategories() {
        return em.createQuery("SELECT c FROM Category c", Category.class)
                .getResultList();
    }

    @Override
    public Optional<Category> findCategoryById(int id) {
        try {
            Category category = em.createQuery("SELECT c FROM Category c WHERE c.id = :id", Category.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(category);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
