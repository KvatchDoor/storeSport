package com.sportstore.application.service;

import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ListArticlesService implements ListArticlesUseCase {

    private final ArticleRepository articleRepository;

    public ListArticlesService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> list(Optional<Category> category) {
        return category
                .map(articleRepository::findByCategory)
                .orElseGet(articleRepository::findAll);
    }
}
