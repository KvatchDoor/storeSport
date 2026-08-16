package com.sportstore.application.service;

import com.sportstore.application.port.in.ListArticleStocksUseCase;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.domain.model.ArticleStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListArticleStocksService implements ListArticleStocksUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListArticleStocksService.class);

    private final ArticleRepository articleRepository;

    public ListArticleStocksService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<ArticleStock> listStocks() {
        List<ArticleStock> stocks = articleRepository.findAllStocks();
        log.info("Consultation de la liste des stocks: {} article(s)", stocks.size());
        return stocks;
    }
}
