package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleName;

import java.util.List;

public interface ListArticleNamesUseCase {

    List<ArticleName> listNames();
}
