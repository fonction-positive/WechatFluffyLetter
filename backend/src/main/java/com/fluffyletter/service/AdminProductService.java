package com.fluffyletter.service;

import com.fluffyletter.dto.*;
import com.fluffyletter.entity.Product;
import com.fluffyletter.entity.ProductI18n;
import com.fluffyletter.entity.ProductImage;
import com.fluffyletter.repository.ProductI18nRepository;
import com.fluffyletter.repository.ProductImageRepository;
import com.fluffyletter.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductI18nRepository productI18nRepository;
    private final ProductImageRepository productImageRepository;

    public AdminProductService(ProductRepository productRepository,
                              ProductI18nRepository productI18nRepository,
                              ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productI18nRepository = productI18nRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<AdminProductListItemDTO> list(int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.min(Math.max(1, size), 100);

        var pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Order.desc("id")));
        var productPage = productRepository.findAll(pageable);

        List<Product> products = productPage.getContent();
        List<Long> productIds = products.stream().map(Product::getId).toList();

        Map<Long, ProductI18n> zhMap = new HashMap<>();
        Map<Long, ProductI18n> enMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            productI18nRepository.findByProductIdInAndLang(productIds, "zh")
                    .forEach(i -> zhMap.put(i.getProductId(), i));
            productI18nRepository.findByProductIdInAndLang(productIds, "en")
                    .forEach(i -> enMap.put(i.getProductId(), i));
        }

        Map<Long, String> coverUrlMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            var images = productImageRepository.findByProductIdIn(productIds);
            // 简单挑一个 cover=true 的作为封面，否则取最小 sortOrder 的
            Map<Long, List<ProductImage>> grouped = new HashMap<>();
            for (ProductImage img : images) {
                grouped.computeIfAbsent(img.getProductId(), k -> new ArrayList<>()).add(img);
            }
            for (var e : grouped.entrySet()) {
                String cover = pickCover(e.getValue());
                coverUrlMap.put(e.getKey(), cover);
            }
        }

        List<AdminProductListItemDTO> out = new ArrayList<>();
        for (Product p : products) {
            ProductI18n zh = zhMap.get(p.getId());
            ProductI18n en = enMap.get(p.getId());
            out.add(new AdminProductListItemDTO(
                    p.getId(),
                    p.getCategoryId(),
                    p.getPrice(),
                    p.getDiscountPrice(),
                    p.getHot(),
                    p.getActive(),
                    zh == null ? "" : zh.getName(),
                    en == null ? "" : en.getName(),
                    coverUrlMap.getOrDefault(p.getId(), "")
            ));
        }
        return out;
    }

    public AdminProductDetailDTO detail(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));

        ProductI18n zh = productI18nRepository.findByProductIdAndLang(id, "zh").orElse(null);
        ProductI18n en = productI18nRepository.findByProductIdAndLang(id, "en").orElse(null);
        var images = productImageRepository.findByProductIdOrderByCoverDescSortOrderAscIdAsc(id)
                .stream()
                .map(img -> new AdminProductImageDTO(img.getId(), img.getImageUrl(), img.getSortOrder(), img.getCover()))
                .toList();

        return new AdminProductDetailDTO(
                p.getId(),
                p.getCategoryId(),
                p.getPrice(),
                p.getDiscountPrice(),
                p.getHot(),
                p.getActive(),
                zh == null ? new AdminProductI18nDTO("zh", "", null, null) : new AdminProductI18nDTO("zh", zh.getName(), zh.getBrief(), zh.getDescription()),
                en == null ? new AdminProductI18nDTO("en", "", null, null) : new AdminProductI18nDTO("en", en.getName(), en.getBrief(), en.getDescription()),
                images
        );
    }

    @Transactional
    public AdminProductDetailDTO create(AdminProductUpsertRequest request) {
        Product p = new Product();
        apply(p, request);
        p = productRepository.save(p);

        upsertI18n(p.getId(), "zh", request.getZh());
        upsertI18n(p.getId(), "en", request.getEn());
        replaceImages(p.getId(), request.getImages());

        return detail(p.getId());
    }

    @Transactional
    public AdminProductDetailDTO update(Long id, AdminProductUpsertRequest request) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));
        apply(p, request);
        productRepository.save(p);

        upsertI18n(id, "zh", request.getZh());
        upsertI18n(id, "en", request.getEn());
        replaceImages(id, request.getImages());

        return detail(id);
    }

    @Transactional
    public void deactivate(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));
        p.setActive(false);
        productRepository.save(p);
    }

    private static void apply(Product p, AdminProductUpsertRequest request) {
        p.setCategoryId(request.getCategoryId());
        p.setPrice(request.getPrice());
        p.setDiscountPrice(request.getDiscountPrice());
        p.setHot(request.getHot() != null && request.getHot());
        p.setActive(request.getActive() != null && request.getActive());
    }

    private void upsertI18n(Long productId, String lang, AdminProductI18nRequest req) {
        ProductI18n row = productI18nRepository.findByProductIdAndLang(productId, lang).orElseGet(ProductI18n::new);
        row.setProductId(productId);
        row.setLang(lang);
        row.setName(req.getName().trim());
        row.setBrief(req.getBrief());
        row.setDescription(req.getDescription());
        productI18nRepository.save(row);
    }

    private void replaceImages(Long productId, List<AdminProductImageRequest> images) {
        productImageRepository.deleteByProductId(productId);
        if (images == null || images.isEmpty()) return;

        for (AdminProductImageRequest imgReq : images) {
            ProductImage img = new ProductImage();
            img.setProductId(productId);
            img.setImageUrl(imgReq.getImageUrl().trim());
            img.setSortOrder(imgReq.getSortOrder() == null ? 0 : imgReq.getSortOrder());
            img.setCover(imgReq.getCover() != null && imgReq.getCover());
            productImageRepository.save(img);
        }
    }

    private static String pickCover(List<ProductImage> images) {
        if (images == null || images.isEmpty()) return "";

        ProductImage best = null;
        for (ProductImage img : images) {
            if (best == null) {
                best = img;
                continue;
            }
            boolean imgCover = Boolean.TRUE.equals(img.getCover());
            boolean bestCover = Boolean.TRUE.equals(best.getCover());
            if (imgCover != bestCover) {
                if (imgCover) best = img;
                continue;
            }
            int imgSort = img.getSortOrder() == null ? 0 : img.getSortOrder();
            int bestSort = best.getSortOrder() == null ? 0 : best.getSortOrder();
            if (imgSort != bestSort) {
                if (imgSort < bestSort) best = img;
                continue;
            }
            if (img.getId() != null && best.getId() != null && img.getId() < best.getId()) {
                best = img;
            }
        }
        return best == null ? "" : best.getImageUrl();
    }
}
