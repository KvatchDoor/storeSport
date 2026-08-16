package com.sportstore.domain.model;

import com.sportstore.domain.exception.InvalidArticleException;
import com.sportstore.domain.exception.OutOfStockException;
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

    @Test
    @DisplayName("decrementStock reduit la quantite d'une unite")
    void decrementStockReducesQuantity() {
        Article article = new Article(ArticleId.newId(), new ArticleName("Soccer Ball"),
                new Category("Team Sports"), Price.of("29.99"), new Stock(5));

        Article decremented = article.decrementStock();

        assertThat(decremented.stock().quantity()).isEqualTo(4);
        assertThat(decremented.id()).isEqualTo(article.id());
        assertThat(decremented.name()).isEqualTo(article.name());
    }

    @Test
    @DisplayName("decrementStock leve OutOfStockException si stock = 0")
    void decrementStockThrowsWhenOutOfStock() {
        Article article = new Article(ArticleId.newId(), new ArticleName("Tennis Racket"),
                new Category("Racket Sports"), Price.of("89.50"), new Stock(0));

        assertThatThrownBy(article::decrementStock)
                .isInstanceOf(OutOfStockException.class)
                .hasMessage("Out of stock: Tennis Racket");
    }

    @Test
    @DisplayName("decrementStock cree un nouvel objet immutable")
    void decrementStockCreatesNewInstance() {
        Article article = new Article(ArticleId.newId(), new ArticleName("Basketball"),
                new Category("Ball Sports"), Price.of("25.00"), new Stock(3));

        Article decremented = article.decrementStock();

        assertThat(decremented).isNotSameAs(article);
        assertThat(article.stock().quantity()).isEqualTo(3);
        assertThat(decremented.stock().quantity()).isEqualTo(2);
    }
}
