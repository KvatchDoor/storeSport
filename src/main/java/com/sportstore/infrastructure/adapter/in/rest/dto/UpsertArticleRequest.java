package com.sportstore.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Corps de la requete PUT /store/articles.
 */
public record UpsertArticleRequest(

        @NotBlank(message = "name must not be blank")
        String name,

        @NotBlank(message = "category must not be blank")
        String category,

        @NotNull(message = "price must not be null")
        @DecimalMin(value = "0.00", message = "price must not be negative")
        BigDecimal price
) {
}
