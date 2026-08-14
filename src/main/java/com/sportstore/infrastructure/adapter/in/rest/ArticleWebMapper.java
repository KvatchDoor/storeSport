package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.infrastructure.adapter.in.rest.dto.ArticleResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.UpsertArticleRequest;
import org.springframework.stereotype.Component;

/**
 * Traduction entre les DTO de l'API HTTP et les objets du domaine.
 */
@Component
public class ArticleWebMapper {

    ArticleResponse toResponse(Article article) {
        return new ArticleResponse(
                article.id().value(),
                article.name().value(),
                article.category().value(),
                article.price().amount()
        );
    }

    UpsertArticleCommand toCommand(UpsertArticleRequest request) {
        return new UpsertArticleCommand(
                new ArticleName(request.name()),
                new Category(request.category()),
                new Price(request.price())
        );
    }

    ArticleName toArticleName(String name) {
        return new ArticleName(name);
    }

    Category toCategory(String category) {
        return new Category(category);
    }
}
