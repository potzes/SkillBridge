package com.florence.skillbridge.Service.Implementation;

import com.florence.skillbridge.DTO.Response.CategoryDTO;
import com.florence.skillbridge.Domain.Category;
import com.florence.skillbridge.Repository.Interface.CategoryRepo;
import com.florence.skillbridge.Service.Interface.CategoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CategoryServiceImpl implements CategoryService {
    @Inject
    CategoryRepo categoryRepo;

    @Override
    public List<CategoryDTO> getCategories() {
        List<Category> categories = categoryRepo.getCategories();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        for(Category category : categories) {
            categoryDTOS.add(new CategoryDTO(
                    category.getId(), category.getName()
            ));
        }

        return categoryDTOS;
    }
}
