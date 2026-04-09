package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Response.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getCategories();
}
