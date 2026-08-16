package com.sportstore.domain.model;

import com.sportstore.domain.exception.OutOfStockException;
import java.util.Objects;

public record Article(ArticleId id, ArticleName name, Category category, Price price, Stock stock) {

    public Article {
        Objects.requireNonNull(id, "L'identifiant de l'article est obligatoire");
        Objects.requireNonNull(name, "Le nom de l'article est obligatoire");
        Objects.requireNonNull(category, "La categorie de l'article est obligatoire");
        Objects.requireNonNull(price, "Le prix de l'article est obligatoire");
        Objects.requireNonNull(stock, "Le stock de l'article est obligatoire");
    }

    public static Article create(ArticleName name, Category category, Price price) {
        return new Article(ArticleId.newId(), name, category, price, new Stock(0));
    }

    public Article replaceWith(Category newCategory, Price newPrice) {
        return new Article(id, name, newCategory, newPrice, stock);
    }

    public Article decrementStock() {
        if (stock.quantity() == 0) {
            throw new OutOfStockException(name);
        }
        return new Article(id, name, category, price, new Stock(stock.quantity() - 1));
    }

    public boolean belongsTo(Category otherCategory) {
        return category.equals(otherCategory);
    }
}
