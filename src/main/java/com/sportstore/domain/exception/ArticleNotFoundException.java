package com.sportstore.domain.exception;

import com.sportstore.domain.model.ArticleName;

public class ArticleNotFoundException extends RuntimeException {

    private final transient ArticleName articleName;

    public ArticleNotFoundException(ArticleName articleName) {
        super("Article not found: " + articleName.value());
        this.articleName = articleName;
    }

    public ArticleName articleName() {
        return articleName;
    }
}
