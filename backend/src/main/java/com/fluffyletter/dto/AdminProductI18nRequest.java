package com.fluffyletter.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminProductI18nRequest {

    @NotBlank
    private String name;

    private String brief;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
