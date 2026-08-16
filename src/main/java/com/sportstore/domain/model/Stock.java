package com.sportstore.domain.model;

import java.util.Objects;

public record Stock(int quantity) {

    public Stock {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
    }
}
