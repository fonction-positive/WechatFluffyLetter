package com.fluffyletter.dto;

public class AdminProductI18nDTO {

    private String lang;
    private String name;
    private String brief;
    private String description;

    public AdminProductI18nDTO() {
    }

    public AdminProductI18nDTO(String lang, String name, String brief, String description) {
        this.lang = lang;
        this.name = name;
        this.brief = brief;
        this.description = description;
    }

    public String getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }

    public String getBrief() {
        return brief;
    }

    public String getDescription() {
        return description;
    }
}
