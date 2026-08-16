package com.sportstore.domain.model;

import java.util.Objects;

public record Article(ArticleId id, ArticleName name, Category category, Price price) {

    public Article {
        Objects.requireNonNull(id, "L'identifiant de l'article est obligatoire");
        Objects.requireNonNull(name, "Le nom de l'article est obligatoire");
        Objects.requireNonNull(category, "La categorie de l'article est obligatoire");
        Objects.requireNonNull(price, "Le prix de l'article est obligatoire");
    }

    public static Article create(ArticleName name, Category category, Price price) {
        return new Article(ArticleId.newId(), name, category, price);
    }

    public Article replaceWith(Category newCategory, Price newPrice) {
        return new Article(id, name, newCategory, newPrice);
    }

    public boolean belongsTo(Category otherCategory) {
        return category.equals(otherCategory);
    }
}
