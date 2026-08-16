package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.domain.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class ArticlePersistenceMapper {

    Article toDomain(ArticleJpaEntity entity) {
        return new Article(
                ArticleId.of(entity.getId()),
                new ArticleName(entity.getName()),
                new Category(entity.getCategory()),
                new Price(entity.getPrice()),
                toStock(entity.getStock())
        );
    }

    ArticleJpaEntity toEntity(Article article) {
        ArticleJpaEntity entity = new ArticleJpaEntity(
                article.id().value(),
                article.name().value(),
                article.category().value(),
                article.price().amount()
        );
        entity.setStock(fromStock(article.stock()));
        return entity;
    }

    ArticleJpaEntity updateEntity(ArticleJpaEntity entity, Article article) {
        entity.setName(article.name().value());
        entity.setCategory(article.category().value());
        entity.setPrice(article.price().amount());
        entity.setStock(fromStock(article.stock()));

        return entity;
    }

    Stock toStock(int quantity) {
        return new Stock(quantity);
    }

    int fromStock(Stock stock) {
        return stock.quantity();
    }

    ArticleName toArticleName(String name) {
        return new ArticleName(name);
    }
}
