package com.sportstore.application.service;

import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.ArticleStock;
import com.sportstore.domain.model.Category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake du port secondaire : permet de tester les services applicatifs sans infrastructure.
 */
class InMemoryArticleRepository implements ArticleRepository {

    private final Map<UUID, Article> articles = new LinkedHashMap<>();

    void seed(Article... seeded) {
        for (Article article : seeded) {
            articles.put(article.id().value(), article);
        }
    }

    @Override
    public List<Article> findAll() {
        List<Article> all = new ArrayList<>(articles.values());
        all.sort(Comparator.comparing(article -> article.name().value()));
        return List.copyOf(all);
    }

    @Override
    public List<ArticleName> findAllNames() {
        return findAll().stream().map(Article::name).toList();
    }

    @Override
    public List<Article> findByCategory(Category category) {
        return findAll().stream().filter(article -> article.belongsTo(category)).toList();
    }

    @Override
    public Optional<Article> findByName(ArticleName name) {
        return articles.values().stream()
                .filter(article -> article.name().equals(name))
                .findFirst();
    }

    @Override
    public Article save(Article article) {
        articles.put(article.id().value(), article);
        return article;
    }

    @Override
    public void delete(Article article) {
        articles.remove(article.id().value());
    }

    @Override
    public List<ArticleStock> findAllStocks() {
        return findAll().stream()
                .map(article -> new ArticleStock(article.id(), article.name(), article.stock()))
                .toList();
    }

    int size() {
        return articles.size();
    }
}
