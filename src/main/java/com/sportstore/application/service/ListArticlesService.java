package com.sportstore.application.service;

import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ListArticlesService implements ListArticlesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListArticlesService.class);

    private final ArticleRepository articleRepository;

    public ListArticlesService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> list(Optional<Category> category) {
        List<Article> articles = category
                .map(articleRepository::findByCategory)
                .orElseGet(articleRepository::findAll);

        log.debug("Listage du catalogue (categorie={}) : {} article(s)",
                category.map(Category::value).orElse("toutes"), articles.size());

        return articles;
    }
}
