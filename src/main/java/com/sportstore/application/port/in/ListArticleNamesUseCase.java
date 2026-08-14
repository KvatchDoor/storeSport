package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleName;

import java.util.List;

/**
 * Port primaire : lister les noms des articles du catalogue.
 */
public interface ListArticleNamesUseCase {

    List<ArticleName> listNames();
}
