package com.sportstore.application.port.out;

import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Port secondaire de persistance du catalogue.
 * <p>
 * Le contrat est exprime uniquement avec des objets du domaine : aucune entite JPA, aucun type
 * technique ne traverse cette interface. Les erreurs techniques des adaptateurs sont remontees
 * sous la forme d'une {@link ArticleStorageException}.
 */
public interface ArticleRepository {

    List<Article> findAll();

    List<ArticleName> findAllNames();

    List<Article> findByCategory(Category category);

    Optional<Article> findByName(ArticleName name);

    Article save(Article article);

    void delete(Article article);
}
