package com.sportstore.application.service;

import com.sportstore.application.port.in.ConsultArticleUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.application.port.out.StockRepository;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConsultArticleService implements ConsultArticleUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultArticleService.class);

    private final ArticleRepository articleRepository;
    private final StockRepository stockRepository;

    public ConsultArticleService(ArticleRepository articleRepository, StockRepository stockRepository) {
        this.articleRepository = articleRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    public Article consultByName(ArticleName name) {
        Article article = articleRepository.findByName(name)
                .orElseThrow(() -> new ArticleNotFoundException(name));

        var stock = stockRepository.findByArticleId(article.id())
                .orElseThrow(() -> new IllegalStateException("Stock introuvable pour l'article: " + name.value()));

        var updatedStock = stock.decrementIfAvailable();
        stockRepository.save(updatedStock);

        log.debug("Consultation de l'article {} - stock décrémenté à {}", article.name(), updatedStock.quantity().value());

        return article;
    }
}
