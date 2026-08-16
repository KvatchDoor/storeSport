package com.sportstore.domain.model;

import java.util.Objects;

public record ArticleStock(ArticleId articleId, ArticleName articleName, Stock stock) {

    public ArticleStock {
        Objects.requireNonNull(articleId, "L'identifiant de l'article est obligatoire");
        Objects.requireNonNull(articleName, "Le nom de l'article est obligatoire");
        Objects.requireNonNull(stock, "Le stock de l'article est obligatoire");
    }
}
