package com.fluffyletter.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    private Long id;

    @Column(name = "wechat_id", length = 100)
    private String wechatId;

    @Column(name = "qrcode_url", length = 500)
    private String qrcodeUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (this.id == null) this.id = 1L;
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }
}
