package com.fluffyletter.repository;

import com.fluffyletter.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findByUserId(Long userId, Pageable pageable);

    List<Favorite> findByProductId(Long productId, Pageable pageable);

    List<Favorite> findByUserIdAndProductId(Long userId, Long productId, Pageable pageable);

    List<Favorite> findByUserIdAndProductIdIn(Long userId, Iterable<Long> productIds);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByProductId(Long productId);
}
