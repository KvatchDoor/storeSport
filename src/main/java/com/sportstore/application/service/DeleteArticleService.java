package com.sportstore.application.service;

import com.sportstore.application.port.in.DeleteArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteArticleService implements DeleteArticleUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteArticleService.class);

    private final ArticleRepository articleRepository;

    public DeleteArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public void deleteByName(ArticleName name) {
        Article article = articleRepository.findByName(name)
                .orElseThrow(() -> new ArticleNotFoundException(name));

        articleRepository.delete(article);

        log.info("Article supprime : {} (id={})", article.name(), article.id());
    }
}
