package com.sportstore.application.port.out;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.ArticleStock;
import com.sportstore.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    List<Article> findAll();

    List<ArticleName> findAllNames();

    List<Article> findByCategory(Category category);

    Optional<Article> findByName(ArticleName name);

    Article save(Article article);

    void delete(Article article);

    List<ArticleStock> findAllStocks();
}
