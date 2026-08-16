package com.sportstore.domain.model;

import java.util.Objects;
import java.util.UUID;

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
