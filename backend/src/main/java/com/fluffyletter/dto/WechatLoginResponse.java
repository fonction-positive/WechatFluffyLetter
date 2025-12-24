package com.fluffyletter.dto;

public class WechatLoginResponse {

    private String userToken;
    private String openid;
    private String nickname;
    private String avatarUrl;

    public WechatLoginResponse() {
    }

    public WechatLoginResponse(String userToken, String openid, String nickname, String avatarUrl) {
        this.userToken = userToken;
        this.openid = openid;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public String getUserToken() {
        return userToken;
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
}
