package com.florence.skillbridge.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddUserInterestDTO {
    @NotBlank(message = "Skill name is required")
    @Size(min = 3, message = "Skill must be at least 3 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Skill must not contain number or symbol")
    private String name;

    @NotNull(message = "Category is required")
    private int category;

    public AddUserInterestDTO(String name, int category) {
        this.name = name;
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }
}
