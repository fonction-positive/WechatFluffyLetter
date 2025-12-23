package com.fluffyletter.repository;

import com.fluffyletter.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByCoverDescSortOrderAscIdAsc(Long productId);

    List<ProductImage> findByProductIdIn(Iterable<Long> productIds);

    void deleteByProductId(Long productId);
}
