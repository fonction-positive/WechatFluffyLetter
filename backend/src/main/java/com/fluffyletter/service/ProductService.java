package com.fluffyletter.service;

import com.fluffyletter.dto.ProductDetailDTO;
import com.fluffyletter.dto.ProductListItemDTO;
import com.fluffyletter.entity.Favorite;
import com.fluffyletter.entity.Product;
import com.fluffyletter.entity.ProductI18n;
import com.fluffyletter.entity.ProductImage;
import com.fluffyletter.repository.FavoriteRepository;
import com.fluffyletter.repository.ProductI18nRepository;
import com.fluffyletter.repository.ProductImageRepository;
import com.fluffyletter.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductI18nRepository productI18nRepository;
    private final ProductImageRepository productImageRepository;
    private final FavoriteRepository favoriteRepository;

    public ProductService(ProductRepository productRepository,
                          ProductI18nRepository productI18nRepository,
                          ProductImageRepository productImageRepository,
                          FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.productI18nRepository = productI18nRepository;
        this.productImageRepository = productImageRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<ProductListItemDTO> list(Long categoryId, String lang, int page, int size, Long userId) {
        String l = LangUtil.normalize(lang);
        int p = Math.max(1, page);
        int s = Math.min(100, Math.max(1, size));

        PageRequest pageable = PageRequest.of(p - 1, s, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = (categoryId == null)
                ? productRepository.findByActiveTrue(pageable)
                : productRepository.findByActiveTrueAndCategoryId(categoryId, pageable);

        List<Product> products = productPage.getContent();
        List<Long> ids = products.stream().map(Product::getId).toList();

        Map<Long, ProductI18n> i18n = loadI18n(ids, l);
        Map<Long, String> cover = loadCoverImages(ids);
        Set<Long> favorited = loadFavoritedSet(userId, ids);

        return products.stream().map(pdt -> {
            ProductI18n t = i18n.get(pdt.getId());
            ProductListItemDTO dto = new ProductListItemDTO();
            dto.setId(pdt.getId());
            dto.setName(t == null ? "" : t.getName());
            dto.setBrief(t == null ? "" : t.getBrief());
            dto.setPrice(pdt.getPrice());
            dto.setDiscountPrice(pdt.getDiscountPrice());
            dto.setHot(Boolean.TRUE.equals(pdt.getHot()));
            dto.setCoverImage(cover.getOrDefault(pdt.getId(), ""));
            dto.setFavorited(favorited.contains(pdt.getId()));
            return dto;
        }).toList();
    }

    public ProductDetailDTO detail(Long productId, String lang, Long userId) {
        String l = LangUtil.normalize(lang);

        Product product = productRepository.findById(productId)
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .orElseThrow(() -> new NotFoundException("product not found"));

        ProductI18n i18n = productI18nRepository.findByProductIdAndLang(productId, l)
                .orElseGet(() -> productI18nRepository.findByProductIdAndLang(productId, "zh").orElse(null));

        List<String> images = productImageRepository.findByProductIdOrderByCoverDescSortOrderAscIdAsc(productId)
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        boolean favorited = false;
        if (userId != null) {
            favorited = favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent();
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setName(i18n == null ? "" : i18n.getName());
        dto.setBrief(i18n == null ? "" : i18n.getBrief());
        dto.setDescription(i18n == null ? "" : i18n.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setHot(Boolean.TRUE.equals(product.getHot()));
        dto.setFavorited(favorited);
        dto.setImages(images);
        return dto;
    }

    public List<ProductListItemDTO> listByIdsPreserveOrder(List<Long> productIds, String lang, Long userId) {
        if (productIds == null || productIds.isEmpty()) return List.of();

        String l = LangUtil.normalize(lang);
        List<Product> products = productRepository.findAllById(productIds).stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .toList();

        Map<Long, Product> productMap = products.stream().collect(java.util.stream.Collectors.toMap(Product::getId, Function.identity()));

        Map<Long, ProductI18n> i18n = loadI18n(productIds, l);
        Map<Long, String> cover = loadCoverImages(productIds);
        Set<Long> favorited = loadFavoritedSet(userId, productIds);

        List<ProductListItemDTO> out = new ArrayList<>();
        for (Long id : productIds) {
            Product p = productMap.get(id);
            if (p == null) continue;
            ProductI18n t = i18n.get(id);

            ProductListItemDTO dto = new ProductListItemDTO();
            dto.setId(id);
            dto.setName(t == null ? "" : t.getName());
            dto.setBrief(t == null ? "" : t.getBrief());
            dto.setPrice(p.getPrice());
            dto.setDiscountPrice(p.getDiscountPrice());
            dto.setHot(Boolean.TRUE.equals(p.getHot()));
            dto.setCoverImage(cover.getOrDefault(id, ""));
            dto.setFavorited(favorited.contains(id));
            out.add(dto);
        }
        return out;
    }

    private Map<Long, ProductI18n> loadI18n(List<Long> productIds, String lang) {
        if (productIds == null || productIds.isEmpty()) return Map.of();

        Map<Long, ProductI18n> result = new HashMap<>();
        List<ProductI18n> primary = productI18nRepository.findByProductIdInAndLang(productIds, lang);
        for (ProductI18n t : primary) {
            result.put(t.getProductId(), t);
        }

        // fallback zh
        if (!"zh".equals(lang)) {
            List<Long> missing = productIds.stream().filter(id -> !result.containsKey(id)).toList();
            if (!missing.isEmpty()) {
                List<ProductI18n> fallback = productI18nRepository.findByProductIdInAndLang(missing, "zh");
                for (ProductI18n t : fallback) {
                    result.putIfAbsent(t.getProductId(), t);
                }
            }
        }

        return result;
    }

    private Map<Long, String> loadCoverImages(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return Map.of();

        List<ProductImage> images = productImageRepository.findByProductIdIn(productIds);
        Map<Long, List<ProductImage>> grouped = new HashMap<>();
        for (ProductImage img : images) {
            grouped.computeIfAbsent(img.getProductId(), k -> new ArrayList<>()).add(img);
        }

        Map<Long, String> cover = new HashMap<>();
        for (Map.Entry<Long, List<ProductImage>> e : grouped.entrySet()) {
            List<ProductImage> list = e.getValue();
            list.sort(Comparator
                    .comparing((ProductImage i) -> !Boolean.TRUE.equals(i.getCover()))
                    .thenComparing(i -> i.getSortOrder() == null ? 0 : i.getSortOrder())
                    .thenComparing(ProductImage::getId));
            if (!list.isEmpty()) {
                cover.put(e.getKey(), list.get(0).getImageUrl());
            }
        }
        return cover;
    }

    private Set<Long> loadFavoritedSet(Long userId, List<Long> productIds) {
        if (userId == null || productIds == null || productIds.isEmpty()) return Set.of();
        return favoriteRepository.findByUserIdAndProductIdIn(userId, productIds)
                .stream()
                .map(Favorite::getProductId)
                .collect(java.util.stream.Collectors.toSet());
    }
}
