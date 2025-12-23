package com.fluffyletter.controller;

import com.fluffyletter.dto.ProductDetailDTO;
import com.fluffyletter.dto.ProductListItemDTO;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.ProductService;
import com.fluffyletter.service.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ApiProductController {

    private final ProductService productService;
    private final AuthHeaderService authHeaderService;

    public ApiProductController(ProductService productService, AuthHeaderService authHeaderService) {
        this.productService = productService;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public List<ProductListItemDTO> list(@RequestParam(value = "category_id", required = false) Long categoryId,
                                         @RequestParam(defaultValue = "zh") String lang,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        UserContext user = authHeaderService.tryUser(authorization);
        return productService.list(categoryId, lang, page, size, user == null ? null : user.getUserId());
    }

    @GetMapping("/{id}")
    public ProductDetailDTO detail(@PathVariable Long id,
                                   @RequestParam(defaultValue = "zh") String lang,
                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        UserContext user = authHeaderService.tryUser(authorization);
        return productService.detail(id, lang, user == null ? null : user.getUserId());
    }
}
