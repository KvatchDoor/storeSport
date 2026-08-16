package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.ArticleStock;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.domain.model.Stock;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'integration de l'adaptateur secondaire, sur la base H2 embarquee.
 */
@DataJpaTest
@Import({JpaArticleRepository.class, ArticlePersistenceMapper.class})
@TestPropertySource(properties = "spring.sql.init.mode=never")
class JpaArticleRepositoryIntegrationTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleSpringDataRepository springDataRepository;

    @Autowired
    private EntityManager entityManager;

    private ArticleJpaEntity entityOf(String name) {
        entityManager.flush();
        entityManager.clear();

        return springDataRepository.findByName(name).orElseThrow();
    }

    @BeforeEach
    void setUp() {
        articleRepository.save(Article.create(
                new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99")));
        articleRepository.save(Article.create(
                new ArticleName("Tennis Racket"), new Category("Racket Sports"), Price.of("89.50")));
    }

    @Test
    @DisplayName("findAll retourne les articles du domaine tries par nom")
    void findAllReturnsDomainObjects() {
        assertThat(articleRepository.findAll())
                .extracting(article -> article.name().value())
                .containsExactly("Soccer Ball", "Tennis Racket");
    }

    @Test
    @DisplayName("findAllNames retourne uniquement les noms")
    void findAllNames() {
        assertThat(articleRepository.findAllNames())
                .extracting(ArticleName::value)
                .containsExactly("Soccer Ball", "Tennis Racket");
    }

    @Test
    @DisplayName("findByCategory filtre sur la categorie")
    void findByCategory() {
        assertThat(articleRepository.findByCategory(new Category("Racket Sports")))
                .singleElement()
                .satisfies(article -> assertThat(article.name().value()).isEqualTo("Tennis Racket"));
    }

    @Test
    @DisplayName("findByName retourne l'article correspondant, vide sinon")
    void findByName() {
        assertThat(articleRepository.findByName(new ArticleName("Soccer Ball")))
                .hasValueSatisfying(article -> assertThat(article.price().amount()).isEqualByComparingTo("29.99"));

        assertThat(articleRepository.findByName(new ArticleName("Bicycle"))).isEmpty();
    }

    @Test
    @DisplayName("save met a jour l'article existant sans changer son identifiant")
    void saveUpdatesExistingArticle() {
        Article existing = articleRepository.findByName(new ArticleName("Soccer Ball")).orElseThrow();

        Article saved = articleRepository.save(existing.replaceWith(new Category("Outdoor"), Price.of("34.50")));

        assertThat(saved.id()).isEqualTo(existing.id());
        assertThat(articleRepository.findAll()).hasSize(2);
        assertThat(articleRepository.findByName(new ArticleName("Soccer Ball")))
                .hasValueSatisfying(article -> assertThat(article.category().value()).isEqualTo("Outdoor"));
    }

    @Test
    @DisplayName("les horodatages sont renseignes a la creation")
    void auditTimestampsAreSetOnInsert() {
        ArticleJpaEntity entity = entityOf("Soccer Ball");

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    @DisplayName("une mise a jour conserve created_at et avance updated_at")
    void updatePreservesCreatedAtAndAdvancesUpdatedAt() throws InterruptedException {
        ArticleJpaEntity before = entityOf("Soccer Ball");
        Instant createdAt = before.getCreatedAt();
        Instant updatedAt = before.getUpdatedAt();

        // l'horloge doit avancer pour que la comparaison soit deterministe
        Thread.sleep(10);

        Article existing = articleRepository.findByName(new ArticleName("Soccer Ball")).orElseThrow();
        articleRepository.save(existing.replaceWith(new Category("Outdoor"), Price.of("34.50")));

        ArticleJpaEntity after = entityOf("Soccer Ball");

        assertThat(after.getCreatedAt())
                .as("created_at ne doit jamais etre reecrit par une mise a jour")
                .isEqualTo(createdAt);
        assertThat(after.getUpdatedAt())
                .as("updated_at doit refleter la derniere ecriture")
                .isAfter(updatedAt);
    }

    @Test
    @DisplayName("les horodatages restent confines a la persistance")
    void auditTimestampsNeverCrossThePort() {
        Article article = articleRepository.findByName(new ArticleName("Soccer Ball")).orElseThrow();

        assertThat(article.getClass().getRecordComponents())
                .as("Article ne connait ni created_at ni updated_at")
                .extracting(java.lang.reflect.RecordComponent::getName)
                .containsExactly("id", "name", "category", "price", "stock");
    }

    @Test
    @DisplayName("delete retire l'article du catalogue")
    void deleteRemovesArticle() {
        Article existing = articleRepository.findByName(new ArticleName("Tennis Racket")).orElseThrow();

        articleRepository.delete(existing);

        assertThat(articleRepository.findByName(new ArticleName("Tennis Racket"))).isEmpty();
        assertThat(articleRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("findAllStocks retourne tous les stocks avec les quantites")
    void findAllStocks() {
        var stocks = articleRepository.findAllStocks();

        assertThat(stocks).hasSize(2);
        assertThat(stocks).extracting(ArticleStock::articleName)
                .extracting(ArticleName::value)
                .containsExactly("Soccer Ball", "Tennis Racket");
        assertThat(stocks).extracting(ArticleStock::stock)
                .extracting(Stock::quantity)
                .containsExactly(0, 0);
    }

    @Test
    @DisplayName("findAllStocks reflechit les decrmentations de stock")
    void findAllStocksReflectsDecrements() {
        Article existing = articleRepository.findByName(new ArticleName("Soccer Ball")).orElseThrow();
        Article withStock = new Article(existing.id(), existing.name(), existing.category(), existing.price(), new Stock(5));
        articleRepository.save(withStock);

        var stocks = articleRepository.findAllStocks();

        assertThat(stocks).hasSize(2);
        var soccerBallStock = stocks.stream()
                .filter(s -> s.articleName().value().equals("Soccer Ball"))
                .findFirst()
                .orElseThrow();
        assertThat(soccerBallStock.stock().quantity()).isEqualTo(5);
    }
}
