package com.sportstore.domain.model;

import com.sportstore.domain.exception.OutOfStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Stock")
class StockTest {

    private static final ArticleId ARTICLE_ID = ArticleId.of(UUID.randomUUID());

    @Nested
    class CreateNew {

        @Test
        @DisplayName("crée un stock à zéro")
        void createsZeroStock() {
            Stock stock = Stock.createNew(ARTICLE_ID);

            assertThat(stock.articleId()).isEqualTo(ARTICLE_ID);
            assertThat(stock.quantity().value()).isZero();
            assertThat(stock.isOutOfStock()).isTrue();
        }
    }

    @Nested
    class DecrementIfAvailable {

        @Test
        @DisplayName("décrémente le stock d'une unité")
        void decrementsStock() {
            Stock stock = new Stock(ARTICLE_ID, Quantity.of(5));

            Stock decremented = stock.decrementIfAvailable();

            assertThat(decremented.quantity().value()).isEqualTo(4);
            assertThat(decremented.articleId()).isEqualTo(ARTICLE_ID);
        }

        @Test
        @DisplayName("lève une exception si le stock est à zéro")
        void throwsWhenOutOfStock() {
            Stock stock = new Stock(ARTICLE_ID, Quantity.zero());

            assertThatThrownBy(() -> stock.decrementIfAvailable())
                    .isInstanceOf(OutOfStockException.class)
                    .hasMessageContaining(ARTICLE_ID.value().toString());
        }

        @Test
        @DisplayName("décrémente correctement de 1 à 0")
        void decrementsFromOneToZero() {
            Stock stock = new Stock(ARTICLE_ID, Quantity.of(1));

            Stock decremented = stock.decrementIfAvailable();

            assertThat(decremented.quantity().isZero()).isTrue();
        }
    }

    @Nested
    class IsOutOfStock {

        @Test
        @DisplayName("retourne true quand la quantité est zéro")
        void returnsTrueWhenZero() {
            Stock stock = new Stock(ARTICLE_ID, Quantity.zero());

            assertThat(stock.isOutOfStock()).isTrue();
        }

        @Test
        @DisplayName("retourne false quand la quantité est positive")
        void returnsFalseWhenPositive() {
            Stock stock = new Stock(ARTICLE_ID, Quantity.of(5));

            assertThat(stock.isOutOfStock()).isFalse();
        }
    }
}
