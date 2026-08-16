package com.sportstore.application.service;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.application.port.out.StockRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.domain.model.Quantity;
import com.sportstore.domain.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private InMemoryArticleRepository articleRepository;
    private InMemoryStockRepository stockRepository;

    @BeforeEach
    void setUp() {
        articleRepository = new InMemoryArticleRepository();
        stockRepository = new InMemoryStockRepository();
        articleRepository.seed(SOCCER_BALL, TENNIS_RACKET);
    }

    @Nested
    class ListNames {

        @Test
        @DisplayName("retourne les noms tries")
        void returnsSortedNames() {
            var names = new ListArticleNamesService(articleRepository).listNames();

            assertThat(names).extracting(ArticleName::value).containsExactly("Soccer Ball", "Tennis Racket");
        }
    }

    @Nested
    class ListArticles {

        @Test
        @DisplayName("sans categorie, retourne tout le catalogue")
        void returnsAllWithoutCategory() {
            var articles = new ListArticlesService(articleRepository).list(Optional.empty());

            assertThat(articles).containsExactly(SOCCER_BALL, TENNIS_RACKET);
        }

        @Test
        @DisplayName("avec categorie, filtre le catalogue")
        void filtersByCategory() {
            var articles = new ListArticlesService(articleRepository).list(Optional.of(new Category("Racket Sports")));

            assertThat(articles).containsExactly(TENNIS_RACKET);
        }
    }

    @Nested
    class GetArticle {

        @Test
        @DisplayName("retourne l'article recherche")
        void returnsArticle() {
            var article = new GetArticleService(articleRepository).getByName(new ArticleName("Soccer Ball"));

            assertThat(article).isEqualTo(SOCCER_BALL);
        }

        @Test
        @DisplayName("leve une exception metier si l'article est inconnu")
        void throwsWhenUnknown() {
            var service = new GetArticleService(articleRepository);

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

            Article created = new UpsertArticleService(articleRepository, stockRepository).upsert(command);

            assertThat(created.id()).isNotNull();
            assertThat(articleRepository.size()).isEqualTo(3);
            assertThat(articleRepository.findByName(new ArticleName("Insulated Water Bottle"))).contains(created);
        }

        @Test
        @DisplayName("remplace integralement l'article existant en conservant son identifiant")
        void replacesWhenAlreadyExists() {
            var command = new UpsertArticleCommand(
                    new ArticleName("Soccer Ball"), new Category("Outdoor"), Price.of("34.50"));

            Article updated = new UpsertArticleService(articleRepository, stockRepository).upsert(command);

            assertThat(updated.id()).isEqualTo(SOCCER_BALL.id());
            assertThat(updated.category().value()).isEqualTo("Outdoor");
            assertThat(updated.price().amount()).isEqualByComparingTo("34.50");
            assertThat(articleRepository.size()).isEqualTo(2);
        }
    }

    @Nested
    class DeleteArticle {

        @Test
        @DisplayName("supprime l'article existant")
        void deletesExisting() {
            new DeleteArticleService(articleRepository, stockRepository).deleteByName(new ArticleName("Tennis Racket"));

            assertThat(articleRepository.size()).isEqualTo(1);
            assertThat(articleRepository.findByName(new ArticleName("Tennis Racket"))).isEmpty();
        }

        @Test
        @DisplayName("leve une exception metier si l'article est inconnu")
        void throwsWhenUnknown() {
            var service = new DeleteArticleService(articleRepository, stockRepository);

            assertThatThrownBy(() -> service.deleteByName(new ArticleName("Bicycle")))
                    .isInstanceOf(ArticleNotFoundException.class)
                    .hasMessage("Article not found: Bicycle");
        }
    }

    /**
     * Fake implementation de ArticleRepository pour les tests.
     */
    static class InMemoryArticleRepository implements ArticleRepository {
        private final java.util.Map<ArticleName, Article> articles = new java.util.HashMap<>();

        void seed(Article... articles) {
            for (Article article : articles) {
                this.articles.put(article.name(), article);
            }
        }

        int size() {
            return articles.size();
        }

        @Override
        public List<Article> findAll() {
            return new java.util.ArrayList<>(articles.values());
        }

        @Override
        public List<ArticleName> findAllNames() {
            return articles.values().stream()
                    .map(Article::name)
                    .sorted((a, b) -> a.value().compareTo(b.value()))
                    .toList();
        }

        @Override
        public List<Article> findByCategory(Category category) {
            return articles.values().stream()
                    .filter(a -> a.belongsTo(category))
                    .toList();
        }

        @Override
        public Optional<Article> findByName(ArticleName name) {
            return Optional.ofNullable(articles.get(name));
        }

        @Override
        public Article save(Article article) {
            articles.put(article.name(), article);
            return article;
        }

        @Override
        public void delete(Article article) {
            articles.remove(article.name());
        }
    }

    /**
     * Fake implementation de StockRepository pour les tests.
     */
    static class InMemoryStockRepository implements StockRepository {
        private final java.util.Map<UUID, Stock> stocks = new java.util.HashMap<>();

        @Override
        public Optional<Stock> findByArticleId(ArticleId articleId) {
            return Optional.ofNullable(stocks.get(articleId.value()));
        }

        @Override
        public List<Stock> findAll() {
            return new java.util.ArrayList<>(stocks.values());
        }

        @Override
        public Stock save(Stock stock) {
            stocks.put(stock.articleId().value(), stock);
            return stock;
        }

        @Override
        public void delete(ArticleId articleId) {
            stocks.remove(articleId.value());
        }
    }
}
