package com.sportstore.domain.exception;

import com.sportstore.domain.model.ArticleId;

public class OutOfStockException extends RuntimeException {

    private final transient ArticleId articleId;

    public OutOfStockException(ArticleId articleId) {
        super("Article out of stock: " + articleId);
        this.articleId = articleId;
    }

    public ArticleId articleId() {
        return articleId;
    }
}
