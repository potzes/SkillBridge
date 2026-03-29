package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Expertise;

import java.util.Optional;

public interface ExpertiseRepo {
    Optional<Expertise> findExpertiseById(int id);
}
