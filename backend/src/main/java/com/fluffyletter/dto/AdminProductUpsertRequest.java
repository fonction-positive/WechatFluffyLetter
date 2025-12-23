package com.fluffyletter.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminProductUpsertRequest {

    @NotNull
    private Long categoryId;

    @NotNull
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotNull
    private Boolean hot = false;

    @NotNull
    private Boolean active = true;

    @NotNull
    @Valid
    private AdminProductI18nRequest zh;

    @NotNull
    @Valid
    private AdminProductI18nRequest en;

    @Valid
    private List<AdminProductImageRequest> images = new ArrayList<>();

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Boolean getHot() {
        return hot;
    }

    public void setHot(Boolean hot) {
        this.hot = hot;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AdminProductI18nRequest getZh() {
        return zh;
    }

    public void setZh(AdminProductI18nRequest zh) {
        this.zh = zh;
    }

    public AdminProductI18nRequest getEn() {
        return en;
    }

    public void setEn(AdminProductI18nRequest en) {
        this.en = en;
    }

    public List<AdminProductImageRequest> getImages() {
        return images;
    }

    public void setImages(List<AdminProductImageRequest> images) {
        this.images = images;
    }
}
