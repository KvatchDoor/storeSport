package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.in.DeleteArticleUseCase;
import com.sportstore.application.port.in.GetArticleUseCase;
import com.sportstore.application.port.in.ListArticleNamesUseCase;
import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.application.port.in.UpsertArticleUseCase;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.infrastructure.adapter.in.rest.dto.ArticleResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.UpsertArticleRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Adaptateur primaire HTTP. Il ne connait que les ports {@code in} (injection par interface),
 * ne contient aucune regle metier et se limite a traduire HTTP en intentions metier.
 */
@RestController
@RequestMapping("/store/articles")
public class ArticleController {

    private final ListArticleNamesUseCase listArticleNamesUseCase;
    private final ListArticlesUseCase listArticlesUseCase;
    private final GetArticleUseCase getArticleUseCase;
    private final UpsertArticleUseCase upsertArticleUseCase;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final ArticleWebMapper mapper;

    public ArticleController(ListArticleNamesUseCase listArticleNamesUseCase,
                             ListArticlesUseCase listArticlesUseCase,
                             GetArticleUseCase getArticleUseCase,
                             UpsertArticleUseCase upsertArticleUseCase,
                             DeleteArticleUseCase deleteArticleUseCase,
                             ArticleWebMapper mapper) {
        this.listArticleNamesUseCase = listArticleNamesUseCase;
        this.listArticlesUseCase = listArticlesUseCase;
        this.getArticleUseCase = getArticleUseCase;
        this.upsertArticleUseCase = upsertArticleUseCase;
        this.deleteArticleUseCase = deleteArticleUseCase;
        this.mapper = mapper;
    }

    /** GET /store/articles/names */
    @GetMapping("/names")
    public List<String> listNames() {
        return listArticleNamesUseCase.listNames().stream()
                .map(ArticleName::value)
                .toList();
    }

    /** GET /store/articles?category={category} */
    @GetMapping
    public List<ArticleResponse> listArticles(@RequestParam(name = "category", required = false) String category) {
        Optional<Category> filter = Optional.ofNullable(category)
                .filter(value -> !value.isBlank())
                .map(mapper::toCategory);

        return listArticlesUseCase.list(filter).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** GET /store/articles/{name} */
    @GetMapping("/{name}")
    public ArticleResponse getArticle(@PathVariable("name") String name) {
        return mapper.toResponse(getArticleUseCase.getByName(mapper.toArticleName(name)));
    }

    /** PUT /store/articles */
    @PutMapping
    public ArticleResponse upsertArticle(@Valid @RequestBody UpsertArticleRequest request) {
        return mapper.toResponse(upsertArticleUseCase.upsert(mapper.toCommand(request)));
    }

    /** DELETE /store/articles/{name} */
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable("name") String name) {
        deleteArticleUseCase.deleteByName(mapper.toArticleName(name));
    }
}
