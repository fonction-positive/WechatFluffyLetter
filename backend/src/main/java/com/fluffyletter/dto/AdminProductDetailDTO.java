package com.fluffyletter.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminProductDetailDTO {

    private Long id;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean hot;
    private Boolean active;
    private AdminProductI18nDTO zh;
    private AdminProductI18nDTO en;
    private List<AdminProductImageDTO> images;

    public AdminProductDetailDTO() {
    }

    public AdminProductDetailDTO(Long id, Long categoryId, BigDecimal price, BigDecimal discountPrice, Boolean hot, Boolean active,
                                 AdminProductI18nDTO zh, AdminProductI18nDTO en, List<AdminProductImageDTO> images) {
        this.id = id;
        this.categoryId = categoryId;
        this.price = price;
        this.discountPrice = discountPrice;
        this.hot = hot;
        this.active = active;
        this.zh = zh;
        this.en = en;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public Boolean getHot() {
        return hot;
    }

    public Boolean getActive() {
        return active;
    }

    public AdminProductI18nDTO getZh() {
        return zh;
    }

    public AdminProductI18nDTO getEn() {
        return en;
    }

    public List<AdminProductImageDTO> getImages() {
        return images;
    }
}
