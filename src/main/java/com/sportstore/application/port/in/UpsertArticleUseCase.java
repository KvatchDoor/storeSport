package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;

/**
 * Port primaire : creer un article ou remplacer integralement celui qui porte deja ce nom.
 */
public interface UpsertArticleUseCase {

    Article upsert(UpsertArticleCommand command);
}
