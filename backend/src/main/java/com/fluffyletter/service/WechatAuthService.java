package com.fluffyletter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluffyletter.config.FluffyProperties;
import com.fluffyletter.entity.WechatUser;
import com.fluffyletter.repository.WechatUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WechatAuthService {

    private final FluffyProperties properties;
    private final WechatUserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WechatAuthService(FluffyProperties properties, WechatUserRepository userRepository) {
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public WechatUser loginOrCreateByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }

        String appid = properties.getWechat().getAppid();
        String secret = properties.getWechat().getSecret();

        if (appid == null || appid.isBlank() || secret == null || secret.isBlank()) {
            throw new IllegalStateException("wechat appid/secret not configured");
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        final ResponseEntity<String> resp;
        try {
            // WeChat sometimes returns text/plain (or is intercepted), so don't bind directly to Map.
            resp = restTemplate.getForEntity(url, String.class);
        } catch (RestClientResponseException ex) {
            // 非 2xx 时不要抛 500，改为 400 并带上响应体便于排查
            String msg = "wechat code2Session http " + ex.getRawStatusCode();
            String bodyStr = ex.getResponseBodyAsString();
            if (bodyStr != null && !bodyStr.isBlank()) {
                msg += ": " + bodyStr;
            }
            throw new IllegalArgumentException(msg);
        } catch (Exception ex) {
            // 例如：网络不可达、TLS/代理问题等
            throw new IllegalArgumentException("wechat code2Session request failed: " + ex.getMessage());
        }

        String bodyStr = resp.getBody();
        if (bodyStr == null || bodyStr.isBlank()) {
            throw new IllegalArgumentException("wechat code2Session empty response");
        }

        final Map<String, Object> body;
        try {
            body = objectMapper.readValue(bodyStr, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("wechat code2Session returned non-JSON: " + snippet(bodyStr));
        }

        Object openidObj = body.get("openid");
        if (openidObj == null) {
            Object errcode = body.get("errcode");
            Object errmsg = body.get("errmsg");
            String msg = "wechat code2Session failed";
            if (errcode != null || errmsg != null) {
                msg += ": errcode=" + String.valueOf(errcode) + ", errmsg=" + String.valueOf(errmsg);
            }
            throw new IllegalArgumentException(msg);
        }

        String openid = String.valueOf(openidObj);

        return userRepository.findByOpenid(openid)
                .orElseGet(() -> {
                    WechatUser u = new WechatUser();
                    u.setOpenid(openid);
                    return userRepository.save(u);
                });
    }

    private static String snippet(String s) {
        if (s == null) return "";
        String oneLine = s.replaceAll("\\s+", " ").trim();
        if (oneLine.length() <= 240) return oneLine;
        return oneLine.substring(0, 240) + "...";
    }
}
