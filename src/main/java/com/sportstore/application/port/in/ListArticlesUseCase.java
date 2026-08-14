package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Port primaire : consulter le catalogue, eventuellement restreint a une categorie.
 */
public interface ListArticlesUseCase {

    List<Article> list(Optional<Category> category);
}
