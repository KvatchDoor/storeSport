package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleName;

public interface DeleteArticleUseCase {

    void deleteByName(ArticleName name);
}
