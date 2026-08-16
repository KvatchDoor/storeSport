package com.sportstore.application.port.in;

import com.sportstore.domain.model.ArticleStock;

import java.util.List;

public interface ListArticleStocksUseCase {

    List<ArticleStock> listStocks();
}
