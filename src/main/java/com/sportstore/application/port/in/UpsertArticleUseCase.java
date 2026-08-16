package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;

public interface UpsertArticleUseCase {

    Article upsert(UpsertArticleCommand command);
}
