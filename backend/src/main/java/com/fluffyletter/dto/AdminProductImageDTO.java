package com.fluffyletter.dto;

public class AdminProductImageDTO {

    private Long id;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean cover;

    public AdminProductImageDTO() {
    }

    public AdminProductImageDTO(Long id, String imageUrl, Integer sortOrder, Boolean cover) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.cover = cover;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public Boolean getCover() {
        return cover;
    }
}
