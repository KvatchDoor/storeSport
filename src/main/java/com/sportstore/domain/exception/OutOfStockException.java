package com.sportstore.domain.exception;

import com.sportstore.domain.model.ArticleName;

public class OutOfStockException extends RuntimeException {

    private final transient ArticleName articleName;

    public OutOfStockException(ArticleName articleName) {
        super("Out of stock: " + articleName.value());
        this.articleName = articleName;
    }

    public ArticleName articleName() {
        return articleName;
    }
}
