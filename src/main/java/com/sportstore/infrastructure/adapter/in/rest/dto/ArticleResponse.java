package com.sportstore.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representation d'un article exposee par l'API HTTP.
 */
public record ArticleResponse(UUID id, String name, String category, BigDecimal price) {
}
