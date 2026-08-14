package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;

/**
 * Port primaire : consulter un article par son nom.
 */
public interface GetArticleUseCase {

    /**
     * @throws com.sportstore.domain.exception.ArticleNotFoundException si aucun article ne porte ce nom
     */
    Article getByName(ArticleName name);
}
