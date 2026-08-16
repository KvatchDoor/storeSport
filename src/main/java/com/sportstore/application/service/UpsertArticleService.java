package com.sportstore.application.service;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.application.port.in.UpsertArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.application.port.out.StockRepository;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UpsertArticleService implements UpsertArticleUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpsertArticleService.class);

    private final ArticleRepository articleRepository;
    private final StockRepository stockRepository;

    public UpsertArticleService(ArticleRepository articleRepository, StockRepository stockRepository) {
        this.articleRepository = articleRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    public Article upsert(UpsertArticleCommand command) {
        Optional<Article> existing = articleRepository.findByName(command.name());

        Article article = existing
                .map(found -> found.replaceWith(command.category(), command.price()))
                .orElseGet(() -> Article.create(command.name(), command.category(), command.price()));

        Article saved = articleRepository.save(article);

        existing.ifPresentOrElse(
                previous -> log.info("Article remplace : {} (id={}, categorie {} -> {}, prix {} -> {})",
                        saved.name(), saved.id(), previous.category(), saved.category(), previous.price(), saved.price()),
                () -> {
                    Stock newStock = Stock.createNew(saved.id());
                    stockRepository.save(newStock);
                    log.info("Article cree : {} (id={}, categorie={}, prix={})",
                            saved.name(), saved.id(), saved.category(), saved.price());
                });

        return saved;
    }
}
