package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminWechatUserDTO;
import com.fluffyletter.repository.WechatUserRepository;
import com.fluffyletter.service.AuthHeaderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/wechat-users")
public class AdminWechatUserController {

    private final WechatUserRepository wechatUserRepository;
    private final AuthHeaderService authHeaderService;

    public AdminWechatUserController(WechatUserRepository wechatUserRepository, AuthHeaderService authHeaderService) {
        this.wechatUserRepository = wechatUserRepository;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public List<AdminWechatUserDTO> list(@RequestHeader("Authorization") String authorization,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        authHeaderService.requireAdmin(authorization);

        int p = Math.max(1, page);
        int s = Math.min(100, Math.max(1, size));
        var pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("id")));

        return wechatUserRepository.findAll(pageable)
                .stream()
                .map(u -> new AdminWechatUserDTO(u.getId(), u.getOpenid(), u.getNickname(), u.getAvatarUrl(), u.getCreatedAt(), u.getUpdatedAt()))
                .toList();
    }
}
