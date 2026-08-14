package com.sportstore.domain.model;

import com.sportstore.domain.exception.InvalidArticleException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Prix de vente d'un article, normalise a deux decimales.
 */
public record Price(BigDecimal amount) {

    private static final int SCALE = 2;

    public Price {
        if (amount == null) {
            throw new InvalidArticleException("Le prix de l'article est obligatoire");
        }
        if (amount.signum() < 0) {
            throw new InvalidArticleException("Le prix de l'article ne peut pas etre negatif");
        }
        amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Price of(String amount) {
        try {
            return new Price(new BigDecimal(amount));
        } catch (NumberFormatException e) {
            throw new InvalidArticleException("Prix invalide : " + amount);
        }
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
