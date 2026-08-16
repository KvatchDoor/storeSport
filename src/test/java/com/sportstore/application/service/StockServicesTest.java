package com.sportstore.application.service;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.domain.exception.OutOfStockException;
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
 * Tests des services applicatifs pour les stocks : les ports secondaires sont remplacés par des fakes, aucun contexte Spring.
 */
class StockServicesTest {

    private static final Article SOCCER_BALL = Article.create(
            new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99"));

    private InMemoryArticleRepository articleRepository;
    private InMemoryStockRepository stockRepository;

    @BeforeEach
    void setUp() {
        articleRepository = new InMemoryArticleRepository();
        stockRepository = new InMemoryStockRepository();
    }

    @Nested
    class UpsertArticleWithStock {

        @Test
        @DisplayName("crée un stock zéro pour un nouvel article")
        void createsZeroStockForNewArticle() {
            var command = new UpsertArticleCommand(
                    new ArticleName("New Ball"), new Category("Sports"), Price.of("19.99"));

            Article created = new UpsertArticleService(articleRepository, stockRepository).upsert(command);

            Stock stock = stockRepository.findByArticleId(created.id()).orElse(null);
            assertThat(stock).isNotNull();
            assertThat(stock.quantity().value()).isZero();
        }

        @Test
        @DisplayName("ne modifie pas le stock lors du remplacement d'un article")
        void doesNotChangeStockWhenReplacing() {
            articleRepository.seed(SOCCER_BALL);
            stockRepository.save(new Stock(SOCCER_BALL.id(), Quantity.of(50)));

            var command = new UpsertArticleCommand(
                    new ArticleName("Soccer Ball"), new Category("Outdoor"), Price.of("34.50"));

            new UpsertArticleService(articleRepository, stockRepository).upsert(command);

            Stock stock = stockRepository.findByArticleId(SOCCER_BALL.id()).orElse(null);
            assertThat(stock).isNotNull();
            assertThat(stock.quantity().value()).isEqualTo(50);
        }
    }

    @Nested
    class ConsultArticleWithStockDecrement {

        @Test
        @DisplayName("décrémente le stock lors de la consultation")
        void decrementsStockWhenConsulting() {
            articleRepository.seed(SOCCER_BALL);
            stockRepository.save(new Stock(SOCCER_BALL.id(), Quantity.of(10)));

            new ConsultArticleService(articleRepository, stockRepository)
                    .consultByName(SOCCER_BALL.name());

            Stock stock = stockRepository.findByArticleId(SOCCER_BALL.id()).orElse(null);
            assertThat(stock).isNotNull();
            assertThat(stock.quantity().value()).isEqualTo(9);
        }

        @Test
        @DisplayName("lève une exception si le stock est à zéro")
        void throwsWhenOutOfStock() {
            articleRepository.seed(SOCCER_BALL);
            stockRepository.save(new Stock(SOCCER_BALL.id(), Quantity.zero()));

            var service = new ConsultArticleService(articleRepository, stockRepository);

            assertThatThrownBy(() -> service.consultByName(SOCCER_BALL.name()))
                    .isInstanceOf(OutOfStockException.class)
                    .hasMessageContaining(SOCCER_BALL.id().value().toString());
        }
    }

    @Nested
    class DeleteArticleWithStock {

        @Test
        @DisplayName("supprime le stock associé lors de la suppression de l'article")
        void deletesStockWhenDeletingArticle() {
            articleRepository.seed(SOCCER_BALL);
            stockRepository.save(new Stock(SOCCER_BALL.id(), Quantity.of(100)));

            new DeleteArticleService(articleRepository, stockRepository)
                    .deleteByName(SOCCER_BALL.name());

            Optional<Stock> stock = stockRepository.findByArticleId(SOCCER_BALL.id());
            assertThat(stock).isEmpty();
        }
    }

    @Nested
    class GetStocks {

        @Test
        @DisplayName("retourne la liste des stocks")
        void returnsList() {
            Article ball = Article.create(new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99"));
            Article racket = Article.create(new ArticleName("Tennis Racket"), new Category("Racket Sports"), Price.of("89.50"));

            articleRepository.seed(ball, racket);
            stockRepository.save(new Stock(ball.id(), Quantity.of(12)));
            stockRepository.save(new Stock(racket.id(), Quantity.of(0)));

            List<Stock> stocks = new GetStocksService(stockRepository).getAll();

            assertThat(stocks).hasSize(2);
            assertThat(stocks.stream().filter(s -> s.quantity().value() == 12)).hasSize(1);
            assertThat(stocks.stream().filter(s -> s.quantity().isZero())).hasSize(1);
        }
    }

    /**
     * Fake implementation de ArticleRepository pour les tests.
     */
    static class InMemoryArticleRepository implements com.sportstore.application.port.out.ArticleRepository {
        private final java.util.Map<ArticleName, Article> articles = new java.util.HashMap<>();

        void seed(Article... articles) {
            for (Article article : articles) {
                this.articles.put(article.name(), article);
            }
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
    static class InMemoryStockRepository implements com.sportstore.application.port.out.StockRepository {
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
