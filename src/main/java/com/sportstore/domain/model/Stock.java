package com.sportstore.domain.model;

import com.sportstore.domain.exception.OutOfStockException;

import java.util.Objects;

public record Stock(ArticleId articleId, Quantity quantity) {

    public Stock {
        Objects.requireNonNull(articleId, "L'identifiant de l'article est obligatoire");
        Objects.requireNonNull(quantity, "La quantité est obligatoire");
    }

    public static Stock createNew(ArticleId articleId) {
        return new Stock(articleId, Quantity.zero());
    }

    public Stock decrementIfAvailable() {
        if (quantity.isZero()) {
            throw new OutOfStockException(articleId);
        }
        return new Stock(articleId, quantity.decrement());
    }

    public boolean isOutOfStock() {
        return quantity.isZero();
    }
}
