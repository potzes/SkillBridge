package com.florence.skillbridge.DTO.Response;

import com.florence.skillbridge.Domain.Skill;

public class SkillDTO {
    private int id;

    private String name;

    private String category;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public SkillDTO(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public SkillDTO(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.category = skill.getCategory().getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
