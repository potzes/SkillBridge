package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.User;

import java.util.Optional;

public interface UserRepo {
    Optional<User> findUserById(int id);
    Double getAverageRating(User user);
}
