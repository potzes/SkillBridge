package com.florence.skillbridge.Repository.Interface;

import com.florence.skillbridge.Domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo {
    List<Category> getCategories();

    Optional<Category> findCategoryById(int id);
}
