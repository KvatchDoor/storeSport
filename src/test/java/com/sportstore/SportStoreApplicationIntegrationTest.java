package com.sportstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de bout en bout : application complete, base H2 alimentee par data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SportStoreApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("le jeu de donnees initial est charge au demarrage")
    void initialDataIsLoaded() throws Exception {
        mockMvc.perform(get("/store/article-names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0]").value("Mountain Bike Helmet"))
                .andExpect(jsonPath("$[4]").value("Yoga Mat"));
    }

    @Test
    @DisplayName("le catalogue peut etre filtre par categorie")
    void catalogCanBeFilteredByCategory() throws Exception {
        mockMvc.perform(get("/store/articles").param("category", "Team Sports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Soccer Ball"))
                .andExpect(jsonPath("$[0].price").value(29.99));

        mockMvc.perform(get("/store/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @DisplayName("un article inconnu retourne 404")
    void unknownArticleReturnsNotFound() throws Exception {
        mockMvc.perform(get("/store/articles/{name}", "Bicycle"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Article not found: Bicycle"));
    }

    @Test
    @DisplayName("PUT cree un article puis le remplace integralement")
    void putCreatesThenReplaces() throws Exception {
        mockMvc.perform(put("/store/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Insulated Water Bottle", "category": "Accessories", "price": 19.90}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Insulated Water Bottle"))
                .andExpect(jsonPath("$.category").value("Accessories"))
                .andExpect(jsonPath("$.price").value(19.90));

        mockMvc.perform(get("/store/articles/{name}", "Insulated Water Bottle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Accessories"));

        mockMvc.perform(put("/store/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Insulated Water Bottle", "category": "Hydration", "price": 22.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Hydration"))
                .andExpect(jsonPath("$.price").value(22.00));

        mockMvc.perform(get("/store/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    

    @Test
    @DisplayName("DELETE supprime l'article puis retourne 404")
    void deleteRemovesArticle() throws Exception {
        mockMvc.perform(delete("/store/articles/{name}", "Yoga Mat"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/store/articles/{name}", "Yoga Mat"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Article not found: Yoga Mat"));

        mockMvc.perform(delete("/store/articles/{name}", "Yoga Mat"))
                .andExpect(status().isNotFound());
    }
}

