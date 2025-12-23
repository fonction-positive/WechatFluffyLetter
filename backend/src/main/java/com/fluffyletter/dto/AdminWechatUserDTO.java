package com.fluffyletter.dto;

import java.time.Instant;

public class AdminWechatUserDTO {

    private Long id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public AdminWechatUserDTO() {
    }

    public AdminWechatUserDTO(Long id, String openid, String nickname, String avatarUrl, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.openid = openid;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getOpenid() {
        return openid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
