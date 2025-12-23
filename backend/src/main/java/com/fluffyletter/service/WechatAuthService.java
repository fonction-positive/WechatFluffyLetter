package com.fluffyletter.service;

import com.fluffyletter.config.FluffyProperties;
import com.fluffyletter.entity.WechatUser;
import com.fluffyletter.repository.WechatUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WechatAuthService {

    private final FluffyProperties properties;
    private final WechatUserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public WechatAuthService(FluffyProperties properties, WechatUserRepository userRepository) {
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public WechatUser loginOrCreateByCode(String code) {
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

        ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
        Map body = resp.getBody();
        if (body == null) {
            throw new IllegalStateException("wechat code2Session empty response");
        }

        Object openidObj = body.get("openid");
        if (openidObj == null) {
            Object err = body.get("errmsg");
            throw new IllegalStateException("wechat code2Session failed: " + err);
        }

        String openid = String.valueOf(openidObj);

        return userRepository.findByOpenid(openid)
                .orElseGet(() -> {
                    WechatUser u = new WechatUser();
                    u.setOpenid(openid);
                    return userRepository.save(u);
                });
    }
}
