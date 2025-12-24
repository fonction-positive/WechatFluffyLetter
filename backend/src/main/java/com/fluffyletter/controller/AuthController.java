package com.fluffyletter.controller;

import com.fluffyletter.dto.WechatLoginRequest;
import com.fluffyletter.dto.WechatLoginResponse;
import com.fluffyletter.entity.WechatUser;
import com.fluffyletter.service.JwtService;
import com.fluffyletter.service.WechatAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class AuthController {

    private final WechatAuthService wechatAuthService;
    private final JwtService jwtService;

    public AuthController(WechatAuthService wechatAuthService, JwtService jwtService) {
        this.wechatAuthService = wechatAuthService;
        this.jwtService = jwtService;
    }

    @PostMapping("/wechat")
    public WechatLoginResponse wechatLogin(@Valid @RequestBody WechatLoginRequest req) {
        WechatUser user = wechatAuthService.loginOrCreateByCode(req.getCode(), req.getNickname(), req.getAvatarUrl());
        String token = jwtService.issueUserToken(user.getId(), user.getOpenid());
        return new WechatLoginResponse(token, user.getOpenid(), user.getNickname(), user.getAvatarUrl());
    }
}
