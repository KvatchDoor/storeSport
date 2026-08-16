package com.sportstore.application.service;

import com.sportstore.application.model.StockWithArticleName;
import com.sportstore.application.port.in.GetStocksUseCase;
import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetStocksWithArticlesService {

    private final GetStocksUseCase getStocksUseCase;
    private final ListArticlesUseCase listArticlesUseCase;

    public GetStocksWithArticlesService(GetStocksUseCase getStocksUseCase, ListArticlesUseCase listArticlesUseCase) {
        this.getStocksUseCase = getStocksUseCase;
        this.listArticlesUseCase = listArticlesUseCase;
    }

    public List<StockWithArticleName> getAll() {
        List<Stock> stocks = getStocksUseCase.getAll();
        Map<Object, Article> articlesById = listArticlesUseCase.list(Optional.empty())
                .stream()
                .collect(Collectors.toMap(article -> article.id().value(), article -> article));

        return stocks.stream()
                .map(stock -> {
                    Article article = articlesById.get(stock.articleId().value());
                    return new StockWithArticleName(stock.articleId(), article.name(), stock.quantity());
                })
                .toList();
    }
}
