package com.fluffyletter.repository;

import com.fluffyletter.entity.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WechatUserRepository extends JpaRepository<WechatUser, Long> {
    Optional<WechatUser> findByOpenid(String openid);
}
