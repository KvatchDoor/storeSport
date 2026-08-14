package com.sportstore.application.service;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.application.port.in.UpsertArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpsertArticleService implements UpsertArticleUseCase {

    private final ArticleRepository articleRepository;

    public UpsertArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Le nom est l'identifiant naturel de l'article : s'il existe deja, ses caracteristiques sont
     * integralement remplacees et son identite est conservee ; sinon un nouvel article est cree.
     */
    @Override
    public Article upsert(UpsertArticleCommand command) {
        Article article = articleRepository.findByName(command.name())
                .map(existing -> existing.replaceWith(command.category(), command.price()))
                .orElseGet(() -> Article.create(command.name(), command.category(), command.price()));

        return articleRepository.save(article);
    }
}
