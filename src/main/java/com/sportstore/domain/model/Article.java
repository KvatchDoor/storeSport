package com.sportstore.domain.model;

import java.util.Objects;

/**
 * Entite du domaine : un article du catalogue.
 * <p>
 * Aucune annotation ni dependance technique : cette classe est instanciable sans Spring ni JPA.
 * L'invariant metier (nom, categorie et prix toujours valides) est garanti par les Value Objects.
 */
public record Article(ArticleId id, ArticleName name, Category category, Price price) {

    public Article {
        Objects.requireNonNull(id, "L'identifiant de l'article est obligatoire");
        Objects.requireNonNull(name, "Le nom de l'article est obligatoire");
        Objects.requireNonNull(category, "La categorie de l'article est obligatoire");
        Objects.requireNonNull(price, "Le prix de l'article est obligatoire");
    }

    /**
     * Cree un nouvel article : le domaine porte la generation de son identite.
     */
    public static Article create(ArticleName name, Category category, Price price) {
        return new Article(ArticleId.newId(), name, category, price);
    }

    /**
     * Remplacement complet des caracteristiques de l'article : l'identite et le nom sont conserves.
     */
    public Article replaceWith(Category newCategory, Price newPrice) {
        return new Article(id, name, newCategory, newPrice);
    }

    public boolean belongsTo(Category otherCategory) {
        return category.equals(otherCategory);
    }
}
