package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;

public interface GetArticleUseCase {

    Article getByName(ArticleName name);
}
