package com.sportstore.application.port.in;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;

public interface ConsultArticleUseCase {

    Article consultByName(ArticleName name);
}
