package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminFavoriteListItemDTO;
import com.fluffyletter.entity.Favorite;
import com.fluffyletter.repository.FavoriteRepository;
import com.fluffyletter.repository.ProductI18nRepository;
import com.fluffyletter.repository.WechatUserRepository;
import com.fluffyletter.service.AuthHeaderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/favorites")
public class AdminFavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final WechatUserRepository wechatUserRepository;
    private final ProductI18nRepository productI18nRepository;
    private final AuthHeaderService authHeaderService;

    public AdminFavoriteController(FavoriteRepository favoriteRepository,
                                   WechatUserRepository wechatUserRepository,
                                   ProductI18nRepository productI18nRepository,
                                   AuthHeaderService authHeaderService) {
        this.favoriteRepository = favoriteRepository;
        this.wechatUserRepository = wechatUserRepository;
        this.productI18nRepository = productI18nRepository;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public List<AdminFavoriteListItemDTO> list(@RequestHeader("Authorization") String authorization,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) Long userId,
                                              @RequestParam(required = false) Long productId) {
        authHeaderService.requireAdmin(authorization);

        int p = Math.max(1, page);
        int s = Math.min(100, Math.max(1, size));
        var pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

        List<Favorite> favorites;
        if (userId != null && productId != null) {
            favorites = favoriteRepository.findByUserIdAndProductId(userId, productId, pageable);
        } else if (userId != null) {
            favorites = favoriteRepository.findByUserId(userId, pageable);
        } else if (productId != null) {
            favorites = favoriteRepository.findByProductId(productId, pageable);
        } else {
            favorites = favoriteRepository.findAll(pageable).getContent();
        }

        Set<Long> userIds = new HashSet<>();
        Set<Long> productIds = new HashSet<>();
        for (Favorite f : favorites) {
            userIds.add(f.getUserId());
            productIds.add(f.getProductId());
        }

        Map<Long, String> openidByUserId = wechatUserRepository.findAllById(userIds).stream()
                .collect(java.util.stream.Collectors.toMap(x -> x.getId(), x -> x.getOpenid()));

        Map<Long, String> nameZhByProductId = productI18nRepository.findByProductIdInAndLang(productIds, "zh").stream()
                .collect(java.util.stream.Collectors.toMap(x -> x.getProductId(), x -> x.getName(), (a, b) -> a));

        Map<Long, String> nameEnByProductId = productI18nRepository.findByProductIdInAndLang(productIds, "en").stream()
                .collect(java.util.stream.Collectors.toMap(x -> x.getProductId(), x -> x.getName(), (a, b) -> a));

        List<AdminFavoriteListItemDTO> out = new ArrayList<>();
        for (Favorite f : favorites) {
            out.add(new AdminFavoriteListItemDTO(
                    f.getId(),
                    f.getUserId(),
                    openidByUserId.getOrDefault(f.getUserId(), ""),
                    f.getProductId(),
                    nameZhByProductId.getOrDefault(f.getProductId(), ""),
                    nameEnByProductId.getOrDefault(f.getProductId(), ""),
                    f.getCreatedAt()
            ));
        }
        return out;
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);
        favoriteRepository.deleteById(id);
    }
}
