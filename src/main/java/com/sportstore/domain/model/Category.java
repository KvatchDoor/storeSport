package com.sportstore.domain.model;

import com.sportstore.domain.exception.InvalidArticleException;

public record Category(String value) {

    private static final int MAX_LENGTH = 80;

    public Category {
        if (value == null || value.isBlank()) {
            throw new InvalidArticleException("La categorie de l'article est obligatoire");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new InvalidArticleException("La categorie ne peut pas depasser " + MAX_LENGTH + " caracteres");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
