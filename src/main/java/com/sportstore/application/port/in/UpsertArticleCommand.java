package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;

import java.util.Objects;

/**
 * Intention metier de creation ou de remplacement complet d'un article.
 * Ne transporte que des objets du domaine : aucun DTO d'API ne traverse le port.
 */
public record UpsertArticleCommand(ArticleName name, Category category, Price price) {

    public UpsertArticleCommand {
        Objects.requireNonNull(name, "Le nom de l'article est obligatoire");
        Objects.requireNonNull(category, "La categorie de l'article est obligatoire");
        Objects.requireNonNull(price, "Le prix de l'article est obligatoire");
    }
}
