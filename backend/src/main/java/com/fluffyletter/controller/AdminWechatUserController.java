package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminWechatUserDTO;
import com.fluffyletter.dto.AdminWechatUserUpdateRequest;
import com.fluffyletter.repository.FavoriteRepository;
import com.fluffyletter.repository.WechatUserRepository;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/wechat-users")
public class AdminWechatUserController {

    private final WechatUserRepository wechatUserRepository;
    private final FavoriteRepository favoriteRepository;
    private final AuthHeaderService authHeaderService;

    public AdminWechatUserController(WechatUserRepository wechatUserRepository,
                                     FavoriteRepository favoriteRepository,
                                     AuthHeaderService authHeaderService) {
        this.wechatUserRepository = wechatUserRepository;
        this.favoriteRepository = favoriteRepository;
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

    @GetMapping("/{id}")
    public AdminWechatUserDTO get(@RequestHeader("Authorization") String authorization,
                                  @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);

        var u = wechatUserRepository.findById(id).orElseThrow(() -> new NotFoundException("wechat user not found"));
        return new AdminWechatUserDTO(u.getId(), u.getOpenid(), u.getNickname(), u.getAvatarUrl(), u.getCreatedAt(), u.getUpdatedAt());
    }

    /**
     * 管理端只允许修改展示昵称；openid 为唯一标识不可修改。
     */
    @PutMapping("/{id}")
    public AdminWechatUserDTO update(@RequestHeader("Authorization") String authorization,
                                     @PathVariable Long id,
                                     @Valid @RequestBody AdminWechatUserUpdateRequest request) {
        authHeaderService.requireAdmin(authorization);

        var u = wechatUserRepository.findById(id).orElseThrow(() -> new NotFoundException("wechat user not found"));

        if (request.getNickname() == null) {
            throw new IllegalArgumentException("nickname is required");
        }

        String nn = request.getNickname().trim();
        if (nn.isBlank()) {
            u.setNickname(null);
        } else {
            u.setNickname(nn);
        }

        u = wechatUserRepository.save(u);
        return new AdminWechatUserDTO(u.getId(), u.getOpenid(), u.getNickname(), u.getAvatarUrl(), u.getCreatedAt(), u.getUpdatedAt());
    }

    /**
     * 删除微信用户：先删除该用户的收藏记录，避免遗留脏数据。
     */
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization,
                       @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);

        var u = wechatUserRepository.findById(id).orElseThrow(() -> new NotFoundException("wechat user not found"));
        favoriteRepository.deleteByUserId(u.getId());
        wechatUserRepository.delete(u);
    }
}
