package com.sportstore.domain.model;

import java.util.Objects;

public record Quantity(long value) {

    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
    }

    public static Quantity zero() {
        return new Quantity(0);
    }

    public static Quantity of(long value) {
        return new Quantity(value);
    }

    public Quantity decrement() {
        return new Quantity(value - 1);
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isPositive() {
        return value > 0;
    }
}
