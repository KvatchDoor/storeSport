package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

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
    @DisplayName("delete retire l'article du catalogue")
    void deleteRemovesArticle() {
        Article existing = articleRepository.findByName(new ArticleName("Tennis Racket")).orElseThrow();

        articleRepository.delete(existing);

        assertThat(articleRepository.findByName(new ArticleName("Tennis Racket"))).isEmpty();
        assertThat(articleRepository.findAll()).hasSize(1);
    }
}
