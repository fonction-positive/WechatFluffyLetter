package com.fluffyletter.dto;

import java.time.Instant;

public class AdminFavoriteListItemDTO {

    private Long id;
    private Long userId;
    private String openid;
    private Long productId;
    private String productNameZh;
    private String productNameEn;
    private Instant createdAt;

    public AdminFavoriteListItemDTO() {
    }

    public AdminFavoriteListItemDTO(Long id,
                                   Long userId,
                                   String openid,
                                   Long productId,
                                   String productNameZh,
                                   String productNameEn,
                                   Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.openid = openid;
        this.productId = productId;
        this.productNameZh = productNameZh;
        this.productNameEn = productNameEn;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOpenid() {
        return openid;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductNameZh() {
        return productNameZh;
    }

    public String getProductNameEn() {
        return productNameEn;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
