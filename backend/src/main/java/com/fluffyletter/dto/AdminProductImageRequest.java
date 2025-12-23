package com.fluffyletter.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminProductImageRequest {

    @NotBlank
    private String imageUrl;

    private Integer sortOrder = 0;

    private Boolean cover = false;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getCover() {
        return cover;
    }

    public void setCover(Boolean cover) {
        this.cover = cover;
    }
}
