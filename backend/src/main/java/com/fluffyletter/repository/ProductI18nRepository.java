package com.fluffyletter.repository;

import com.fluffyletter.entity.ProductI18n;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductI18nRepository extends JpaRepository<ProductI18n, Long> {

    Optional<ProductI18n> findByProductIdAndLang(Long productId, String lang);

    List<ProductI18n> findByProductIdInAndLang(Iterable<Long> productIds, String lang);

    List<ProductI18n> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}
