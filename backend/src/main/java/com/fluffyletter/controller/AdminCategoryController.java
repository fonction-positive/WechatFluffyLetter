package com.fluffyletter.controller;

import com.fluffyletter.dto.AdminCategoryDTO;
import com.fluffyletter.dto.AdminCategoryUpsertRequest;
import com.fluffyletter.entity.Category;
import com.fluffyletter.repository.CategoryRepository;
import com.fluffyletter.service.AuthHeaderService;
import com.fluffyletter.service.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;
    private final AuthHeaderService authHeaderService;

    public AdminCategoryController(CategoryRepository categoryRepository, AuthHeaderService authHeaderService) {
        this.categoryRepository = categoryRepository;
        this.authHeaderService = authHeaderService;
    }

    @GetMapping
    public List<AdminCategoryDTO> list(@RequestHeader("Authorization") String authorization) {
        authHeaderService.requireAdmin(authorization);

        var sort = Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("id"));
        return categoryRepository.findAll(sort).stream()
                .map(c -> new AdminCategoryDTO(c.getId(), c.getCode(), c.getNameZh(), c.getNameEn(), c.getSortOrder(), c.getActive()))
                .toList();
    }

    @PostMapping
    public AdminCategoryDTO create(@RequestHeader("Authorization") String authorization,
                                  @Valid @RequestBody AdminCategoryUpsertRequest request) {
        authHeaderService.requireAdmin(authorization);

        Category c = new Category();
        apply(c, request);
        c = categoryRepository.save(c);
        return new AdminCategoryDTO(c.getId(), c.getCode(), c.getNameZh(), c.getNameEn(), c.getSortOrder(), c.getActive());
    }

    @PutMapping("/{id}")
    public AdminCategoryDTO update(@RequestHeader("Authorization") String authorization,
                                  @PathVariable Long id,
                                  @Valid @RequestBody AdminCategoryUpsertRequest request) {
        authHeaderService.requireAdmin(authorization);

        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category not found"));
        apply(c, request);
        c = categoryRepository.save(c);
        return new AdminCategoryDTO(c.getId(), c.getCode(), c.getNameZh(), c.getNameEn(), c.getSortOrder(), c.getActive());
    }

    /**
     * 为避免产品引用 categoryId 出错，这里做软删除：仅设为 inactive。
     */
    @DeleteMapping("/{id}")
    public void deactivate(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        authHeaderService.requireAdmin(authorization);

        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category not found"));
        c.setActive(false);
        categoryRepository.save(c);
    }

    private static void apply(Category c, AdminCategoryUpsertRequest request) {
        c.setCode(request.getCode().trim());
        c.setNameZh(request.getNameZh().trim());
        c.setNameEn(request.getNameEn().trim());
        c.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        c.setActive(request.getActive() != null && request.getActive());
    }
}
