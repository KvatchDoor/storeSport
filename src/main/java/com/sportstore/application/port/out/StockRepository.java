package com.sportstore.application.port.out;

import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository {

    Optional<Stock> findByArticleId(ArticleId articleId);

    List<Stock> findAll();

    Stock save(Stock stock);

    void delete(ArticleId articleId);
}
