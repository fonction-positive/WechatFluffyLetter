package com.fluffyletter.dto;

import jakarta.validation.constraints.NotBlank;

public class WechatLoginRequest {

    @NotBlank
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
