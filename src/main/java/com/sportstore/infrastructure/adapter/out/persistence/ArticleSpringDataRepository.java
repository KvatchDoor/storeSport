package com.sportstore.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data, detail technique interne a l'adaptateur.
 * Il n'est jamais injecte dans un service applicatif : seul {@link JpaArticleRepository} l'utilise.
 */
interface ArticleSpringDataRepository extends JpaRepository<ArticleJpaEntity, UUID> {

    Optional<ArticleJpaEntity> findByName(String name);

    List<ArticleJpaEntity> findAllByOrderByNameAsc();

    List<ArticleJpaEntity> findByCategoryOrderByNameAsc(String category);

    @Query("select a.name from ArticleJpaEntity a order by a.name asc")
    List<String> findAllNamesOrderByNameAsc();
}
