package com.fluffyletter.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "favorite", uniqueConstraints = {
        @UniqueConstraint(name = "uk_favorite_user_product", columnNames = {"user_id", "product_id"})
}, indexes = {
        @Index(name = "idx_favorite_user", columnList = "user_id"),
        @Index(name = "idx_favorite_product", columnList = "product_id")
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    public Favorite() {
    }

    public Favorite(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
