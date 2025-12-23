package com.fluffyletter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_i18n", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_i18n_product_lang", columnNames = {"product_id", "lang"})
}, indexes = {
        @Index(name = "idx_product_i18n_product", columnList = "product_id")
})
public class ProductI18n {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "lang", length = 10, nullable = false)
    private String lang;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "brief", length = 500)
    private String brief;

    @Lob
    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

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
