package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminProductDetailDTO;
import com.fluffyletter.dto.AdminProductListItemDTO;
import com.fluffyletter.dto.AdminProductUpsertRequest;
import com.fluffyletter.service.AdminProductService;
import com.fluffyletter.service.AuthHeaderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final AuthHeaderService authHeaderService;

    public AdminProductController(AdminProductService adminProductService, AuthHeaderService authHeaderService) {
        this.adminProductService = adminProductService;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public List<AdminProductListItemDTO> list(@RequestHeader("Authorization") String authorization,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        authHeaderService.requireAdmin(authorization);
        return adminProductService.list(page, size);
    }

    @GetMapping("/{id}")
    public AdminProductDetailDTO detail(@RequestHeader("Authorization") String authorization,
                                        @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);
        return adminProductService.detail(id);
    }

    @PostMapping
    public AdminProductDetailDTO create(@RequestHeader("Authorization") String authorization,
                                        @Valid @RequestBody AdminProductUpsertRequest request) {
        authHeaderService.requireAdmin(authorization);
        return adminProductService.create(request);
    }

    @PutMapping("/{id}")
    public AdminProductDetailDTO update(@RequestHeader("Authorization") String authorization,
                                        @PathVariable Long id,
                                        @Valid @RequestBody AdminProductUpsertRequest request) {
        authHeaderService.requireAdmin(authorization);
        return adminProductService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);
        adminProductService.deactivate(id);
    }
}
