package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.User;

import java.util.Optional;

public interface AuthRepo {
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
