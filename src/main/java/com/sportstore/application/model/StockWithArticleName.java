package com.sportstore.application.model;

import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Quantity;

public record StockWithArticleName(ArticleId articleId, ArticleName articleName, Quantity quantity) {
}
