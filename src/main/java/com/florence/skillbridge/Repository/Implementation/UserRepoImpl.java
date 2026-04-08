package com.florence.skillbridge.Repository.Implementation;

import com.florence.skillbridge.Domain.*;
import com.florence.skillbridge.Repository.Interface.UserRepo;
import com.florence.skillbridge.Util.UserCard;
import com.florence.skillbridge.Util.UserFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<UserCard> getUserCards(UserFilter userFilter, User userMe) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserCard> cq = cb.createQuery(UserCard.class);

        Root<UserSkill> userSkill = cq.from(UserSkill.class);
        Join<UserSkill, Expertise> expertise = userSkill.join("expertise");
        Join<UserSkill, User> user = userSkill.join("user");
        Join<UserSkill, Skill> userSkillSkill = userSkill.join("skill");
        Join<Skill, Category> skillCategory = userSkillSkill.join("category");

        Subquery<Double> avgRating = cq.subquery(Double.class);
        Root<Feedback> feedback = avgRating.from(Feedback.class);
        avgRating
                .select(cb.avg(feedback.get("rating")))
                .where(cb.equal(feedback.get("receiver"), user));

        Subquery<Skill> userInterest = cq.subquery(Skill.class);
        Root<UserInterest> userInterestRoot = userInterest.from(UserInterest.class);
        Join<UserInterest, Skill> userInterestSkill = userInterestRoot.join("interest");
        Join<Skill, Category> interestCategory = userInterestSkill.join("category");
        userInterest
                .select(userInterestRoot.get("interest"))
                .where(cb.equal(userInterestRoot.get("user"), user));

        List<Predicate> predicates = new ArrayList<>();

        buildPredicates(userFilter, userMe, predicates, cb, user, skillCategory, interestCategory, expertise);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.select(cb.construct(UserCard.class, user.get("id"), user.get("username"), user.get("status"), avgRating)).distinct(true);

        List<UserCard> content = em.createQuery(cq)
                .setFirstResult(userFilter.getPage())
                .setMaxResults(userFilter.getLimit())
                .getResultList();

        List<Integer> userIds = content.stream().map(UserCard::getId).toList();

        Map<Integer, List<Skill>> skillsMap = fetchSkillsByUserIds(userIds);
        Map<Integer, List<Skill>> interestsMap = fetchInterestsByUserIds(userIds);

        content.forEach(u -> {
            u.setSkills(skillsMap.getOrDefault(u.getId(), List.of()));
            u.setInterests(interestsMap.getOrDefault(u.getId(), List.of()));
        });

        return content;
    }

    @Override
    public Long getUserCardsCount(UserFilter userFilter, User userMe) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<UserSkill> userSkill = cq.from(UserSkill.class);
        Join<UserSkill, Expertise> expertise = userSkill.join("expertise");
        Join<UserSkill, User> user = userSkill.join("user");
        Join<UserSkill, Skill> userSkillSkill = userSkill.join("skill");
        Join<Skill, Category> skillCategory = userSkillSkill.join("category");

        Subquery<Double> avgRating = cq.subquery(Double.class);
        Root<Feedback> feedback = avgRating.from(Feedback.class);
        avgRating
                .select(cb.avg(feedback.get("rating")))
                .where(cb.equal(feedback.get("receiver"), user));

        Subquery<Skill> userInterest = cq.subquery(Skill.class);
        Root<UserInterest> userInterestRoot = userInterest.from(UserInterest.class);
        Join<UserInterest, Skill> userInterestSkill = userInterestRoot.join("interest");
        Join<Skill, Category> interestCategory = userInterestSkill.join("category");
        userInterest
                .select(userInterestRoot.get("interest"))
                .where(cb.equal(userInterestRoot.get("user"), user));

        List<Predicate> predicates = new ArrayList<>();

        buildPredicates(userFilter, userMe, predicates, cb, user, skillCategory, interestCategory, expertise);

        if(userFilter.getExpertise() != null) {
            predicates.add(cb.equal(expertise.get("id"), userFilter.getExpertise()));
        }

        cq.select(cb.countDistinct(user.get("id")));
        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    private static void buildPredicates(UserFilter userFilter, User userMe, List<Predicate> predicates, CriteriaBuilder cb, Join<UserSkill, User> user, Join<Skill, Category> skillCategory, Join<Skill, Category> interestCategory, Join<UserSkill, Expertise> expertise) {
        predicates.add(cb.notEqual(user.get("id"), userMe.getId()));
        if(userFilter.getWho() != null) {
            if(userFilter.getWho()) {
                if(userFilter.getCategory() != null) predicates.add(cb.equal(skillCategory.get("id"), userFilter.getCategory()));
            }
            else {
                if(userFilter.getCategory() != null) predicates.add(cb.equal(interestCategory.get("id"), userFilter.getCategory()));
            }
        } else {
            if(userFilter.getCategory() != null) predicates.add(cb.equal(skillCategory.get("id"), userFilter.getCategory()));
        }

        if(userFilter.getExpertise() != null) {
            predicates.add(cb.equal(expertise.get("id"), userFilter.getExpertise()));
        }
    }

//----------------------------Helper Methods----------------------------------------------------------

    private Map<Integer, List<Skill>> fetchSkillsByUserIds(List<Integer> ids) {
        List<UserSkill> result = em.createQuery("SELECT us FROM UserSkill us WHERE us.user.id IN :ids", UserSkill.class)
                .setParameter("ids", ids)
                .getResultList();

        return result.stream().collect(Collectors.groupingBy(
                        us -> us.getUser().getId(),
                        Collectors.mapping(UserSkill::getSkill, Collectors.toList())
                ));
    }

    private Map<Integer, List<Skill>> fetchInterestsByUserIds(List<Integer> ids) {
        List<UserInterest> result = em.createQuery("SELECT ui FROM UserInterest ui WHERE ui.user.id IN :ids", UserInterest.class)
                .setParameter("ids", ids)
                .getResultList();

        return result.stream().collect(Collectors.groupingBy(
                ui -> ui.getUser().getId(),
                Collectors.mapping(UserInterest::getSkill, Collectors.toList())
        ));
    }
}
