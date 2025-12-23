package com.fluffyletter.dto;

public class WechatLoginResponse {

    private String userToken;
    private String openid;

    public WechatLoginResponse() {
    }

    public WechatLoginResponse(String userToken, String openid) {
        this.userToken = userToken;
        this.openid = openid;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getOpenid() {
        return openid;
    }
}
