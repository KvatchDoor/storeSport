package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.model.StockWithArticleName;
import com.sportstore.application.service.GetStocksWithArticlesService;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Quantity;
import com.sportstore.infrastructure.adapter.in.rest.dto.StockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de l'endpoint GET /store/articles/stocks.
 */
@WebMvcTest(ArticleController.class)
@Import({ArticleWebMapperImpl.class})
class StockControllerTest {

    private static final UUID SOCCER_BALL_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID TENNIS_RACKET_ID = UUID.fromString("f0e1d2c3-b4a5-6978-89ab-cdef01234567");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetStocksWithArticlesService getStocksWithArticlesService;

    @Test
    @DisplayName("GET /store/articles/stocks retourne la liste des stocks")
    void getStocks() throws Exception {
        StockWithArticleName soccerBall = new StockWithArticleName(
                ArticleId.of(SOCCER_BALL_ID),
                new ArticleName("Soccer Ball"),
                Quantity.of(12)
        );
        StockWithArticleName tennisRacket = new StockWithArticleName(
                ArticleId.of(TENNIS_RACKET_ID),
                new ArticleName("Tennis Racket"),
                Quantity.zero()
        );

        given(getStocksWithArticlesService.getAll())
                .willReturn(List.of(soccerBall, tennisRacket));

        mockMvc.perform(get("/store/articles/stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].articleId").value(SOCCER_BALL_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Soccer Ball"))
                .andExpect(jsonPath("$[0].quantity").value(12))
                .andExpect(jsonPath("$[1].articleId").value(TENNIS_RACKET_ID.toString()))
                .andExpect(jsonPath("$[1].name").value("Tennis Racket"))
                .andExpect(jsonPath("$[1].quantity").value(0));
    }

    @Test
    @DisplayName("GET /store/articles/stocks retourne une liste vide s'il n'y a pas de stocks")
    void getStocksEmpty() throws Exception {
        given(getStocksWithArticlesService.getAll())
                .willReturn(List.of());

        mockMvc.perform(get("/store/articles/stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
