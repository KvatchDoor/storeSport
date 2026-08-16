package com.sportstore.domain.model;

import com.sportstore.domain.exception.InvalidArticleException;

public record ArticleName(String value) {

    private static final int MAX_LENGTH = 120;

    public ArticleName {
        if (value == null || value.isBlank()) {
            throw new InvalidArticleException("Le nom de l'article est obligatoire");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new InvalidArticleException("Le nom de l'article ne peut pas depasser " + MAX_LENGTH + " caracteres");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
