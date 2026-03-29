package com.florence.skillbridge.DTO.Request;

import jakarta.validation.constraints.*;

public class AddUserSkillDTO {

    @NotBlank(message = "Skill name is required")
    @Size(min = 3, message = "Skill must be at least 3 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Skill must not contain number or symbol")
    private String name;

    @NotNull(message = "Category is required")
    private int category;

    @NotNull(message = "Expertise is required")
    private int expertise;

    @NotBlank(message = "Proof link is required")
    private String proofLink;

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setExpertise(int expertise) {
        this.expertise = expertise;
    }

    public void setProofLink(String proofLink) {
        this.proofLink = proofLink;
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }

    public int getExpertise() {
        return expertise;
    }

    public String getProofLink() {
        return proofLink;
    }
}
