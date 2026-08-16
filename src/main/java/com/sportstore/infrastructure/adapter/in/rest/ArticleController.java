package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.in.DeleteArticleUseCase;
import com.sportstore.application.port.in.GetArticleUseCase;
import com.sportstore.application.port.in.ListArticleNamesUseCase;
import com.sportstore.application.port.in.ListArticleStocksUseCase;
import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.application.port.in.UpsertArticleUseCase;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.infrastructure.adapter.in.rest.api.ArticlesApi;
import com.sportstore.infrastructure.adapter.in.rest.dto.ArticleResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.StockResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.UpsertArticleRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ArticleController implements ArticlesApi {

    private final ListArticleNamesUseCase listArticleNamesUseCase;
    private final ListArticlesUseCase listArticlesUseCase;
    private final GetArticleUseCase getArticleUseCase;
    private final UpsertArticleUseCase upsertArticleUseCase;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final ListArticleStocksUseCase listArticleStocksUseCase;
    private final ArticleWebMapper mapper;

    public ArticleController(ListArticleNamesUseCase listArticleNamesUseCase,
                             ListArticlesUseCase listArticlesUseCase,
                             GetArticleUseCase getArticleUseCase,
                             UpsertArticleUseCase upsertArticleUseCase,
                             DeleteArticleUseCase deleteArticleUseCase,
                             ListArticleStocksUseCase listArticleStocksUseCase,
                             ArticleWebMapper mapper) {
        this.listArticleNamesUseCase = listArticleNamesUseCase;
        this.listArticlesUseCase = listArticlesUseCase;
        this.getArticleUseCase = getArticleUseCase;
        this.upsertArticleUseCase = upsertArticleUseCase;
        this.deleteArticleUseCase = deleteArticleUseCase;
        this.listArticleStocksUseCase = listArticleStocksUseCase;
        this.mapper = mapper;
    }

    @Override
    public List<String> listNames() {
        return listArticleNamesUseCase.listNames().stream()
                .map(ArticleName::value)
                .toList();
    }

    @Override
    public List<ArticleResponse> listArticles(String category) {
        Optional<Category> filter = Optional.ofNullable(category)
                .filter(value -> !value.isBlank())
                .map(mapper::toCategory);

        return listArticlesUseCase.list(filter).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ArticleResponse getArticle(String name) {
        return mapper.toResponse(getArticleUseCase.getByName(mapper.toArticleName(name)));
    }

    @Override
    public ArticleResponse upsertArticle(UpsertArticleRequest request) {
        return mapper.toResponse(upsertArticleUseCase.upsert(mapper.toCommand(request)));
    }

    @Override
    public List<StockResponse> listStocks() {
        return listArticleStocksUseCase.listStocks().stream()
                .map(mapper::toStockResponse)
                .toList();
    }

    @Override
    public void deleteArticle(String name) {
        deleteArticleUseCase.deleteByName(mapper.toArticleName(name));
    }
}
