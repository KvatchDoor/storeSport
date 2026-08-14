package com.sportstore.application.service;

import com.sportstore.application.port.in.DeleteArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteArticleService implements DeleteArticleUseCase {

    private final ArticleRepository articleRepository;

    public DeleteArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public void deleteByName(ArticleName name) {
        Article article = articleRepository.findByName(name)
                .orElseThrow(() -> new ArticleNotFoundException(name));

        articleRepository.delete(article);
    }
}
