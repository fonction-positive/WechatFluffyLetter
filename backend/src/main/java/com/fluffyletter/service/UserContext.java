package com.fluffyletter.service;

public class UserContext {

    private final Long userId;
    private final String openid;

    public UserContext(Long userId, String openid) {
        this.userId = userId;
        this.openid = openid;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOpenid() {
        return openid;
    }
}
