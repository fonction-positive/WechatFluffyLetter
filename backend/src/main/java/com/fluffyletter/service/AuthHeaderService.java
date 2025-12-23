package com.fluffyletter.service;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class AuthHeaderService {

    private final JwtService jwtService;

    public AuthHeaderService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public UserContext requireUser(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("missing Authorization header");
        }

        String token = authorizationHeader;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring("Bearer ".length()).trim();
        }

        if (token.isBlank()) {
            throw new IllegalArgumentException("empty token");
        }

        Claims claims = jwtService.parse(token);

        Object typeObj = claims.get("type");
        String type = typeObj == null ? "" : String.valueOf(typeObj);
        if (!type.isBlank() && !"user".equals(type)) {
            throw new IllegalArgumentException("token type mismatch");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Object openidObj = claims.get("openid");
        String openid = openidObj == null ? "" : String.valueOf(openidObj);
        return new UserContext(userId, openid);
    }

    public AdminContext requireAdmin(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("missing Authorization header");
        }

        String token = authorizationHeader;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring("Bearer ".length()).trim();
        }

        if (token.isBlank()) {
            throw new IllegalArgumentException("empty token");
        }

        Claims claims = jwtService.parse(token);
        Object typeObj = claims.get("type");
        String type = typeObj == null ? "" : String.valueOf(typeObj);
        if (!"admin".equals(type)) {
            throw new IllegalArgumentException("token type mismatch");
        }

        Long adminId = Long.valueOf(claims.getSubject());
        Object roleObj = claims.get("role");
        String role = roleObj == null ? "" : String.valueOf(roleObj);
        return new AdminContext(adminId, role);
    }

    /**
     * 可选解析用户身份：没有/非法 token 时返回 null。
     * 用于商品列表/详情等“可带登录态、也可匿名访问”的接口。
     */
    public UserContext tryUser(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) return null;
        try {
            return requireUser(authorizationHeader);
        } catch (Exception ignored) {
            return null;
        }
    }
}
