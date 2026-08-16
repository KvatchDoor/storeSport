package com.sportstore.application.service;

import com.sportstore.application.port.in.GetArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetArticleService implements GetArticleUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetArticleService.class);

    private final ArticleRepository articleRepository;

    public GetArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article getByName(ArticleName name) {
        Article article = articleRepository.findByName(name)
                .orElseThrow(() -> new ArticleNotFoundException(name));

        log.debug("Consultation de l'article {}", article.name());

        return article;
    }
}
