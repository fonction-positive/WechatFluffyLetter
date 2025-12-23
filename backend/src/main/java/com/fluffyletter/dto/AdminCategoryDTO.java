package com.fluffyletter.dto;

public class AdminCategoryDTO {

    private Long id;
    private String code;
    private String nameZh;
    private String nameEn;
    private Integer sortOrder;
    private Boolean active;

    public AdminCategoryDTO() {
    }

    public AdminCategoryDTO(Long id, String code, String nameZh, String nameEn, Integer sortOrder, Boolean active) {
        this.id = id;
        this.code = code;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameZh() {
        return nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public Boolean getActive() {
        return active;
    }
}
