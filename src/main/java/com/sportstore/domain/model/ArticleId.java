package com.sportstore.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifiant d'un article. L'identite est generee par le domaine, jamais par la couche de persistance.
 */
public record ArticleId(UUID value) {

    public ArticleId {
        Objects.requireNonNull(value, "L'identifiant d'un article est obligatoire");
    }

    public static ArticleId newId() {
        return new ArticleId(UUID.randomUUID());
    }

    public static ArticleId of(UUID value) {
        return new ArticleId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
