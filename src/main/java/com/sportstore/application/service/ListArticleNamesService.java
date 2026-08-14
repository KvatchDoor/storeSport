package com.sportstore.application.service;

import com.sportstore.application.port.in.ListArticleNamesUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.ArticleName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListArticleNamesService implements ListArticleNamesUseCase {

    private final ArticleRepository articleRepository;

    public ListArticleNamesService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<ArticleName> listNames() {
        return articleRepository.findAllNames();
    }
}
