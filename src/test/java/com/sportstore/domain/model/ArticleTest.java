package com.sportstore.domain.model;

import com.sportstore.domain.exception.InvalidArticleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests du domaine : aucun contexte Spring, aucune infrastructure.
 */
class ArticleTest {

    @Test
    @DisplayName("un article cree recoit une identite generee par le domaine")
    void createGeneratesIdentity() {
        Article article = Article.create(new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99"));

        assertThat(article.id()).isNotNull();
        assertThat(article.id().value()).isNotNull();
        assertThat(article.name().value()).isEqualTo("Soccer Ball");
    }

    @Test
    @DisplayName("le remplacement conserve l'identite et le nom")
    void replaceWithKeepsIdentityAndName() {
        Article article = Article.create(new ArticleName("Yoga Mat"), new Category("Fitness"), Price.of("24.90"));

        Article replaced = article.replaceWith(new Category("Wellness"), Price.of("19.90"));

        assertThat(replaced.id()).isEqualTo(article.id());
        assertThat(replaced.name()).isEqualTo(article.name());
        assertThat(replaced.category().value()).isEqualTo("Wellness");
        assertThat(replaced.price().amount()).isEqualByComparingTo("19.90");
    }

    @Test
    @DisplayName("le nom est normalise et ne peut pas etre vide")
    void nameIsMandatoryAndTrimmed() {
        assertThat(new ArticleName("  Tennis Racket  ").value()).isEqualTo("Tennis Racket");

        assertThatThrownBy(() -> new ArticleName("   "))
                .isInstanceOf(InvalidArticleException.class)
                .hasMessageContaining("nom");
    }

    @Test
    @DisplayName("le prix est normalise a deux decimales et refuse les valeurs negatives")
    void priceIsNormalizedAndNonNegative() {
        assertThat(new Price(new BigDecimal("19.9")).amount()).isEqualTo(new BigDecimal("19.90"));

        assertThatThrownBy(() -> Price.of("-1.00"))
                .isInstanceOf(InvalidArticleException.class)
                .hasMessageContaining("negatif");
    }

    @Test
    @DisplayName("la categorie est obligatoire")
    void categoryIsMandatory() {
        assertThatThrownBy(() -> new Category(null))
                .isInstanceOf(InvalidArticleException.class);
    }
}
