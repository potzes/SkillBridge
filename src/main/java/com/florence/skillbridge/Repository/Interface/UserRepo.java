package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.User;
import com.florence.skillbridge.Util.UserCard;
import com.florence.skillbridge.Util.UserFilter;

import java.util.List;
import java.util.Optional;

public interface UserRepo {
    Optional<User> findUserById(int id);
    Double getAverageRating(User user);
    List<UserCard> getUserCards(UserFilter userFilter, User user);
    Long getUserCardsCount(UserFilter userFilter, User user);
}
