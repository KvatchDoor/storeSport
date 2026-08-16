package com.sportstore.application.service;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.exception.OutOfStockException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.ArticleStock;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.domain.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests des services applicatifs : le port secondaire est remplace par un fake, aucun contexte Spring.
 */
class ArticleServicesTest {

    private static final Article SOCCER_BALL =
            Article.create(new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99"));
    private static final Article TENNIS_RACKET =
            Article.create(new ArticleName("Tennis Racket"), new Category("Racket Sports"), Price.of("89.50"));

    private InMemoryArticleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleRepository();
        repository.seed(SOCCER_BALL, TENNIS_RACKET);
    }

    @Nested
    class ListNames {

        @Test
        @DisplayName("retourne les noms tries")
        void returnsSortedNames() {
            var names = new ListArticleNamesService(repository).listNames();

            assertThat(names).extracting(ArticleName::value).containsExactly("Soccer Ball", "Tennis Racket");
        }
    }

    @Nested
    class ListArticles {

        @Test
        @DisplayName("sans categorie, retourne tout le catalogue")
        void returnsAllWithoutCategory() {
            var articles = new ListArticlesService(repository).list(Optional.empty());

            assertThat(articles).containsExactly(SOCCER_BALL, TENNIS_RACKET);
        }

        @Test
        @DisplayName("avec categorie, filtre le catalogue")
        void filtersByCategory() {
            var articles = new ListArticlesService(repository).list(Optional.of(new Category("Racket Sports")));

            assertThat(articles).containsExactly(TENNIS_RACKET);
        }
    }

    @Nested
    class GetArticle {

        @Test
        @DisplayName("retourne l'article recherche avec stock decrement")
        void returnsArticleWithDecrementedStock() {
            var articleWithStock = new Article(SOCCER_BALL.id(), SOCCER_BALL.name(), SOCCER_BALL.category(), SOCCER_BALL.price(), new Stock(5));
            repository.save(articleWithStock);

            var article = new GetArticleService(repository).getByName(new ArticleName("Soccer Ball"));

            assertThat(article.id()).isEqualTo(SOCCER_BALL.id());
            assertThat(article.name()).isEqualTo(SOCCER_BALL.name());
            assertThat(article.stock().quantity()).isEqualTo(4);
        }

        @Test
        @DisplayName("decrement le stock lors de la consultation")
        void decrementsStock() {
            var articleWithStock = Article.create(new ArticleName("Basketball"), new Category("Ball Sports"), Price.of("25.00"));
            var incremented = new Article(articleWithStock.id(), articleWithStock.name(), articleWithStock.category(), articleWithStock.price(), new Stock(5));
            repository.save(incremented);

            var result = new GetArticleService(repository).getByName(new ArticleName("Basketball"));

            assertThat(result.stock().quantity()).isEqualTo(4);
            var persisted = repository.findByName(new ArticleName("Basketball")).orElseThrow();
            assertThat(persisted.stock().quantity()).isEqualTo(4);
        }

        @Test
        @DisplayName("leve OutOfStockException si le stock est a 0")
        void throwsOutOfStockException() {
            var service = new GetArticleService(repository);

            assertThatThrownBy(() -> service.getByName(new ArticleName("Soccer Ball")))
                    .isInstanceOf(OutOfStockException.class)
                    .hasMessage("Out of stock: Soccer Ball");
        }

        @Test
        @DisplayName("leve une exception metier si l'article est inconnu")
        void throwsWhenUnknown() {
            var service = new GetArticleService(repository);

            assertThatThrownBy(() -> service.getByName(new ArticleName("Bicycle")))
                    .isInstanceOf(ArticleNotFoundException.class)
                    .hasMessage("Article not found: Bicycle");
        }
    }

    @Nested
    class UpsertArticle {

        @Test
        @DisplayName("cree l'article quand le nom est inconnu")
        void createsWhenUnknown() {
            var command = new UpsertArticleCommand(
                    new ArticleName("Insulated Water Bottle"), new Category("Accessories"), Price.of("19.90"));

            Article created = new UpsertArticleService(repository).upsert(command);

            assertThat(created.id()).isNotNull();
            assertThat(repository.size()).isEqualTo(3);
            assertThat(repository.findByName(new ArticleName("Insulated Water Bottle"))).contains(created);
        }

        @Test
        @DisplayName("remplace integralement l'article existant en conservant son identifiant")
        void replacesWhenAlreadyExists() {
            var command = new UpsertArticleCommand(
                    new ArticleName("Soccer Ball"), new Category("Outdoor"), Price.of("34.50"));

            Article updated = new UpsertArticleService(repository).upsert(command);

            assertThat(updated.id()).isEqualTo(SOCCER_BALL.id());
            assertThat(updated.category().value()).isEqualTo("Outdoor");
            assertThat(updated.price().amount()).isEqualByComparingTo("34.50");
            assertThat(repository.size()).isEqualTo(2);
        }
    }

    @Nested
    class DeleteArticle {

        @Test
        @DisplayName("supprime l'article existant")
        void deletesExisting() {
            new DeleteArticleService(repository).deleteByName(new ArticleName("Tennis Racket"));

            assertThat(repository.size()).isEqualTo(1);
            assertThat(repository.findByName(new ArticleName("Tennis Racket"))).isEmpty();
        }

        @Test
        @DisplayName("leve une exception metier si l'article est inconnu")
        void throwsWhenUnknown() {
            var service = new DeleteArticleService(repository);

            assertThatThrownBy(() -> service.deleteByName(new ArticleName("Bicycle")))
                    .isInstanceOf(ArticleNotFoundException.class)
                    .hasMessage("Article not found: Bicycle");
        }
    }

    @Nested
    class ListStocks {

        @Test
        @DisplayName("retourne tous les stocks avec les quantites actuelles")
        void returnsAllStocks() {
            var stocks = new ListArticleStocksService(repository).listStocks();

            assertThat(stocks).hasSize(2);
            assertThat(stocks).extracting(ArticleStock::articleName)
                    .extracting(ArticleName::value)
                    .containsExactly("Soccer Ball", "Tennis Racket");
            assertThat(stocks).extracting(ArticleStock::stock)
                    .extracting(Stock::quantity)
                    .containsExactly(0, 0);
        }

        @Test
        @DisplayName("retourne les stocks reflechissant les decrmentations")
        void returnsUpdatedStocks() {
            var article = Article.create(new ArticleName("Volleyball"), new Category("Ball Sports"), Price.of("15.50"));
            var withStock = new Article(article.id(), article.name(), article.category(), article.price(), new Stock(7));
            repository.save(withStock);

            var stocks = new ListArticleStocksService(repository).listStocks();

            assertThat(stocks).hasSize(3);
            assertThat(stocks).extracting(ArticleStock::stock)
                    .extracting(Stock::quantity)
                    .containsExactly(0, 0, 7);
        }
    }
}
