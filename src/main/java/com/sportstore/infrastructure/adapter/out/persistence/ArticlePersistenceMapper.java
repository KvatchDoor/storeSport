package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public class ArticlePersistenceMapper {

    Article toDomain(ArticleJpaEntity entity) {
        return new Article(
                ArticleId.of(entity.getId()),
                new ArticleName(entity.getName()),
                new Category(entity.getCategory()),
                new Price(entity.getPrice())
        );
    }

    ArticleJpaEntity toEntity(Article article) {
        return new ArticleJpaEntity(
                article.id().value(),
                article.name().value(),
                article.category().value(),
                article.price().amount()
        );
    }

    ArticleJpaEntity updateEntity(ArticleJpaEntity entity, Article article) {
        entity.setName(article.name().value());
        entity.setCategory(article.category().value());
        entity.setPrice(article.price().amount());

        return entity;
    }

    ArticleName toArticleName(String name) {
        return new ArticleName(name);
    }
}
