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

    @Override
    public Optional<User> findUserById(int id) {
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Double getAverageRating(User user) {
        return em.createQuery("SELECT AVG(f.rating) FROM Feedback f WHERE f.receiver = :user", Double.class)
                .setParameter("user", user)
                .getSingleResult();
    }

    @Override
    public List<UserCard> getUserCards(UserFilter userFilter, User userMe) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserCard> cq = cb.createQuery(UserCard.class);
        if(userFilter.getWho()) {
            QuerySetupTeach setupTeach = buildJoinsTeach(cq);
            List<Predicate> predicates = new ArrayList<>();
            buildPredicatesTeach(userFilter, userMe, predicates, cb, setupTeach.user(), setupTeach.skillCategory(), setupTeach.interestCategory(), setupTeach.expertise());

            cq.where(predicates.toArray(new Predicate[0]));
            cq.select(cb.construct(UserCard.class,
                    setupTeach.user().get("id"),
                    setupTeach.user().get("username"),
                    setupTeach.user().get("status"),
                    setupTeach.avgRating())).distinct(true);
        }
        else {
            QuerySetupLearn setupLearn = buildJoinsLearn(cq);
            List<Predicate> predicates = new ArrayList<>();
            buildPredicatesLearn(userFilter, userMe, predicates, cb, setupLearn.user(), setupLearn.interestCategory());

            cq.where(predicates.toArray(new Predicate[0]));
            cq.select(cb.construct(UserCard.class,
                    setupLearn.user().get("id"),
                    setupLearn.user().get("username"),
                    setupLearn.user().get("status"),
                    setupLearn.avgRating())).distinct(true);

        }

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

        if(userFilter.getWho()) {
            QuerySetupTeach setupTeach = buildJoinsTeach(cq);
            List<Predicate> predicates = new ArrayList<>();
            buildPredicatesTeach(userFilter, userMe, predicates, cb, setupTeach.user(), setupTeach.skillCategory(), setupTeach.interestCategory(), setupTeach.expertise());

            cq.select(cb.countDistinct(setupTeach.user().get("id")));
            cq.where(predicates.toArray(new Predicate[0]));

        }
        else {
            QuerySetupLearn setupLearn = buildJoinsLearn(cq);
            List<Predicate> predicates = new ArrayList<>();
            buildPredicatesLearn(userFilter, userMe, predicates, cb, setupLearn.user(), setupLearn.interestCategory());

            cq.select(cb.countDistinct(setupLearn.user().get("id")));
            cq.where(predicates.toArray(new Predicate[0]));

        }
        return em.createQuery(cq).getSingleResult();

    }

    // ----------------------------Helper Methods----------------------------------------------------------

    private <T> QuerySetupTeach buildJoinsTeach(CriteriaQuery<T> cq) {
        Root<UserSkill> userSkill = cq.from(UserSkill.class);
        Join<UserSkill, Expertise> expertise = userSkill.join("expertise");
        Join<UserSkill, User> user = userSkill.join("user");
        Join<UserSkill, Skill> userSkillSkill = userSkill.join("skill");
        Join<Skill, Category> skillCategory = userSkillSkill.join("category");

        CriteriaBuilder cb = em.getCriteriaBuilder();

        Subquery<Double> avgRating = cq.subquery(Double.class);
        Root<Feedback> feedback = avgRating.from(Feedback.class);
        avgRating.select(cb.avg(feedback.get("rating")))
                .where(cb.equal(feedback.get("receiver"), user));

        Subquery<Skill> userInterest = cq.subquery(Skill.class);
        Root<UserInterest> userInterestRoot = userInterest.from(UserInterest.class);
        Join<UserInterest, Skill> userInterestSkill = userInterestRoot.join("interest");
        Join<Skill, Category> interestCategory = userInterestSkill.join("category");
        userInterest.select(userInterestRoot.get("interest"))
                .where(cb.equal(userInterestRoot.get("user"), user));

        return new QuerySetupTeach(user, expertise, skillCategory, interestCategory, avgRating);
    }

    private <T> QuerySetupLearn buildJoinsLearn(CriteriaQuery<T> cq) {
        Root<UserInterest> userInterest = cq.from(UserInterest.class);
        Join<UserInterest, User> user = userInterest.join("user");
        Join<UserSkill, Skill> userInterestSkill = userInterest.join("interest");
        Join<Skill, Category> interestCategory = userInterestSkill.join("category");

        CriteriaBuilder cb = em.getCriteriaBuilder();

        Subquery<Double> avgRating = cq.subquery(Double.class);
        Root<Feedback> feedback = avgRating.from(Feedback.class);
        avgRating.select(cb.avg(feedback.get("rating")))
                .where(cb.equal(feedback.get("receiver"), user));

        return new QuerySetupLearn(user, interestCategory, avgRating);
    }

    private static void buildPredicatesTeach(
            UserFilter userFilter,
            User userMe,
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Join<UserSkill, User> user,
            Join<Skill, Category> skillCategory,
            Join<Skill, Category> interestCategory,
            Join<UserSkill, Expertise> expertise) {

        predicates.add(cb.notEqual(user.get("id"), userMe.getId()));

        if (userFilter.getWho()) {
            if (userFilter.getCategory() != null)
                predicates.add(cb.equal(skillCategory.get("id"), userFilter.getCategory()));
        } else {
            if (userFilter.getCategory() != null)
                predicates.add(cb.equal(interestCategory.get("id"), userFilter.getCategory()));
        }

        if (userFilter.getExpertise() != null)
            predicates.add(cb.equal(expertise.get("id"), userFilter.getExpertise()));
    }

    private static void buildPredicatesLearn(
            UserFilter userFilter,
            User userMe,
            List<Predicate> predicates,
            CriteriaBuilder cb,
            Join<UserInterest, User> user,
            Join<Skill, Category> interestCategory) {

        predicates.add(cb.notEqual(user.get("id"), userMe.getId()));

        if (!userFilter.getWho()) {
            if (userFilter.getCategory() != null)
                predicates.add(cb.equal(interestCategory.get("id"), userFilter.getCategory()));
        }
    }

    private Map<Integer, List<Skill>> fetchSkillsByUserIds(List<Integer> ids) {
        List<UserSkill> result = em.createQuery(
                        "SELECT us FROM UserSkill us WHERE us.user.id IN :ids", UserSkill.class)
                .setParameter("ids", ids)
                .getResultList();

        return result.stream().collect(Collectors.groupingBy(
                us -> us.getUser().getId(),
                Collectors.mapping(UserSkill::getSkill, Collectors.toList())
        ));
    }

    private Map<Integer, List<Skill>> fetchInterestsByUserIds(List<Integer> ids) {
        List<UserInterest> result = em.createQuery(
                        "SELECT ui FROM UserInterest ui WHERE ui.user.id IN :ids", UserInterest.class)
                .setParameter("ids", ids)
                .getResultList();

        return result.stream().collect(Collectors.groupingBy(
                ui -> ui.getUser().getId(),
                Collectors.mapping(UserInterest::getSkill, Collectors.toList())
        ));
    }

    private record QuerySetupTeach(
            Join<UserSkill, User> user,
            Join<UserSkill, Expertise> expertise,
            Join<Skill, Category> skillCategory,
            Join<Skill, Category> interestCategory,
            Subquery<Double> avgRating
    ) {}

    private record QuerySetupLearn(
            Join<UserInterest, User> user,
            Join<Skill, Category> interestCategory,
            Subquery<Double> avgRating
    ) {}
}