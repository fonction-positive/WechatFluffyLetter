package com.fluffyletter.controller;

import com.fluffyletter.dto.ApiResponse;
import com.fluffyletter.dto.ProductListItemDTO;
import com.fluffyletter.entity.Favorite;
import com.fluffyletter.repository.FavoriteRepository;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.ProductService;
import com.fluffyletter.service.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final AuthHeaderService authHeaderService;
    private final ProductService productService;

    public FavoriteController(FavoriteRepository favoriteRepository,
                              AuthHeaderService authHeaderService,
                              ProductService productService) {
        this.favoriteRepository = favoriteRepository;
        this.authHeaderService = authHeaderService;
        this.productService = productService;
    }

    @PostMapping("/{productId}")
    public ApiResponse add(@RequestHeader(value = "Authorization", required = false) String authorization,
                           @PathVariable Long productId) {
        UserContext user = authHeaderService.requireUser(authorization);

        favoriteRepository.findByUserIdAndProductId(user.getUserId(), productId)
                .orElseGet(() -> favoriteRepository.save(new Favorite(user.getUserId(), productId)));

        return ApiResponse.ok();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse remove(@RequestHeader(value = "Authorization", required = false) String authorization,
                              @PathVariable Long productId) {
        UserContext user = authHeaderService.requireUser(authorization);

        favoriteRepository.findByUserIdAndProductId(user.getUserId(), productId)
                .ifPresent(favoriteRepository::delete);

        return ApiResponse.ok();
    }

    @GetMapping
    public Object list(@RequestHeader(value = "Authorization", required = false) String authorization,
                       @RequestParam(defaultValue = "zh") String lang,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "false") boolean idsOnly) {
        UserContext user = authHeaderService.requireUser(authorization);
        List<Long> ids = favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getUserId())
                .stream().map(Favorite::getProductId).toList();

        if (idsOnly) {
            return ids;
        }

        int p = Math.max(1, page);
        int s = Math.min(100, Math.max(1, size));
        int from = Math.min(ids.size(), (p - 1) * s);
        int to = Math.min(ids.size(), from + s);
        List<Long> pageIds = ids.subList(from, to);

        List<ProductListItemDTO> items = productService.listByIdsPreserveOrder(pageIds, lang, user.getUserId());
        return items;
    }
}
