package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleName;

/**
 * Port primaire : retirer un article du catalogue.
 */
public interface DeleteArticleUseCase {

    /**
     * @throws com.sportstore.domain.exception.ArticleNotFoundException si aucun article ne porte ce nom
     */
    void deleteByName(ArticleName name);
}
