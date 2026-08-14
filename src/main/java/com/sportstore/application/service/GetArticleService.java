package com.sportstore.application.service;

import com.sportstore.application.port.in.GetArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetArticleService implements GetArticleUseCase {

    private final ArticleRepository articleRepository;

    public GetArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article getByName(ArticleName name) {
        return articleRepository.findByName(name)
                .orElseThrow(() -> new ArticleNotFoundException(name));
    }
}
