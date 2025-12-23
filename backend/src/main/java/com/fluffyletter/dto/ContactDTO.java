package com.fluffyletter.dto;

public class ContactDTO {

    private String wechatId;
    private String qrcodeUrl;

    public ContactDTO() {
    }

    public ContactDTO(String wechatId, String qrcodeUrl) {
        this.wechatId = wechatId;
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getWechatId() {
        return wechatId;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }
}
