package com.fluffyletter.dto;

import java.math.BigDecimal;

public class AdminProductListItemDTO {

    private Long id;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean hot;
    private Boolean active;
    private String nameZh;
    private String nameEn;
    private String coverImageUrl;

    public AdminProductListItemDTO() {
    }

    public AdminProductListItemDTO(Long id, Long categoryId, BigDecimal price, BigDecimal discountPrice, Boolean hot, Boolean active,
                                  String nameZh, String nameEn, String coverImageUrl) {
        this.id = id;
        this.categoryId = categoryId;
        this.price = price;
        this.discountPrice = discountPrice;
        this.hot = hot;
        this.active = active;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.coverImageUrl = coverImageUrl;
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

    public String getNameZh() {
        return nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }
}
