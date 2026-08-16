package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.in.DeleteArticleUseCase;
import com.sportstore.application.port.in.GetArticleUseCase;
import com.sportstore.application.port.in.ListArticleNamesUseCase;
import com.sportstore.application.port.in.ListArticlesUseCase;
import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.application.port.in.UpsertArticleUseCase;
import com.sportstore.application.service.GetStocksWithArticlesService;
import com.sportstore.domain.exception.ArticleNotFoundException;
import com.sportstore.domain.exception.OutOfStockException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de l'adaptateur primaire : les ports {@code in} sont mockes, aucun use case reel n'est appele.
 */
@WebMvcTest(ArticleController.class)
@Import(ArticleWebMapperImpl.class)
class ArticleControllerTest {

    private static final UUID SOCCER_BALL_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final Article SOCCER_BALL = new Article(
            ArticleId.of(SOCCER_BALL_ID), new ArticleName("Soccer Ball"), new Category("Team Sports"), Price.of("29.99"));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListArticleNamesUseCase listArticleNamesUseCase;
    @MockitoBean
    private ListArticlesUseCase listArticlesUseCase;
    @MockitoBean
    private GetArticleUseCase getArticleUseCase;
    @MockitoBean
    private UpsertArticleUseCase upsertArticleUseCase;
    @MockitoBean
    private DeleteArticleUseCase deleteArticleUseCase;
    @MockitoBean
    private GetStocksWithArticlesService getStocksWithArticlesService;

    @Test
    @DisplayName("GET /store/article-names retourne la liste des noms")
    void listNames() throws Exception {
        given(listArticleNamesUseCase.listNames())
                .willReturn(List.of(new ArticleName("Soccer Ball"), new ArticleName("Tennis Racket")));

        mockMvc.perform(get("/store/article-names"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Soccer Ball"))
                .andExpect(jsonPath("$[1]").value("Tennis Racket"));
    }

    @Test
    @DisplayName("GET /store/articles retourne le catalogue complet")
    void listArticlesWithoutCategory() throws Exception {
        given(listArticlesUseCase.list(Optional.empty())).willReturn(List.of(SOCCER_BALL));

        mockMvc.perform(get("/store/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(SOCCER_BALL_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Soccer Ball"))
                .andExpect(jsonPath("$[0].category").value("Team Sports"))
                .andExpect(jsonPath("$[0].price").value(29.99));
    }

    @Test
    @DisplayName("GET /store/articles?category=... transmet le filtre au use case")
    void listArticlesWithCategory() throws Exception {
        given(listArticlesUseCase.list(Optional.of(new Category("Team Sports")))).willReturn(List.of(SOCCER_BALL));

        mockMvc.perform(get("/store/articles").param("category", "Team Sports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Soccer Ball"));

        then(listArticlesUseCase).should().list(Optional.of(new Category("Team Sports")));
    }

    @Test
    @DisplayName("GET /store/articles/{name} retourne l'article")
    void getArticle() throws Exception {
        given(getArticleUseCase.getByName(new ArticleName("Soccer Ball"))).willReturn(SOCCER_BALL);

        mockMvc.perform(get("/store/articles/{name}", "Soccer Ball"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SOCCER_BALL_ID.toString()))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @DisplayName("GET /store/articles/{name} retourne 404 avec le corps d'erreur attendu")
    void getUnknownArticle() throws Exception {
        given(getArticleUseCase.getByName(new ArticleName("Bicycle")))
                .willThrow(new ArticleNotFoundException(new ArticleName("Bicycle")));

        mockMvc.perform(get("/store/articles/{name}", "Bicycle"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Article not found: Bicycle"));
    }

    @Test
    @DisplayName("PUT /store/articles cree ou remplace l'article")
    void upsertArticle() throws Exception {
        UUID id = UUID.fromString("e5f6a7b8-c9d0-1234-ef01-567890123456");
        Article bottle = new Article(ArticleId.of(id), new ArticleName("Insulated Water Bottle"),
                new Category("Accessories"), Price.of("19.90"));
        given(upsertArticleUseCase.upsert(any(UpsertArticleCommand.class))).willReturn(bottle);

        mockMvc.perform(put("/store/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Insulated Water Bottle",
                                  "category": "Accessories",
                                  "price": 19.90
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Insulated Water Bottle"))
                .andExpect(jsonPath("$.category").value("Accessories"))
                .andExpect(jsonPath("$.price").value(19.90));

        then(upsertArticleUseCase).should().upsert(new UpsertArticleCommand(
                new ArticleName("Insulated Water Bottle"), new Category("Accessories"), Price.of("19.90")));
    }

    @Test
    @DisplayName("PUT /store/articles refuse un corps invalide")
    void upsertInvalidArticle() throws Exception {
        mockMvc.perform(put("/store/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "category": "Accessories",
                                  "price": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @DisplayName("DELETE /store/articles/{name} retourne 204 sans corps")
    void deleteArticle() throws Exception {
        doNothing().when(deleteArticleUseCase).deleteByName(new ArticleName("Soccer Ball"));

        mockMvc.perform(delete("/store/articles/{name}", "Soccer Ball"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("DELETE /store/articles/{name} retourne 404 si l'article est inconnu")
    void deleteUnknownArticle() throws Exception {
        willThrow(new ArticleNotFoundException(new ArticleName("Bicycle")))
                .given(deleteArticleUseCase).deleteByName(new ArticleName("Bicycle"));

        mockMvc.perform(delete("/store/articles/{name}", "Bicycle"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Article not found: Bicycle"));
    }

    @Test
    @DisplayName("GET /store/articles/{name} retourne 400 si le stock est épuisé")
    void getArticleOutOfStock() throws Exception {
        given(getArticleUseCase.getByName(new ArticleName("Soccer Ball")))
                .willThrow(new OutOfStockException(ArticleId.of(SOCCER_BALL_ID)));

        mockMvc.perform(get("/store/articles/{name}", "Soccer Ball"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.error").value(containsString("out of stock")));
    }
}
