package com.sportstore.infrastructure.adapter.in.rest.cucumber;

import com.sportstore.application.port.out.ArticleRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * Contexte Spring unique partage par tous les scenarios : l'application complete, sur MockMvc.
 * <p>
 * Deux ecarts assumes par rapport a {@code SportStoreApplicationIntegrationTest} :
 * <ul>
 *   <li>une base H2 dediee, pour que le nettoyage entre scenarios ne touche jamais le jeu de
 *       donnees des autres tests ;</li>
 *   <li>un <em>espion</em> sur le port sortant : il delegue au vrai adaptateur JPA pour tous les
 *       scenarios, et n'est stubbe que pour provoquer la panne de stockage attendue en 500, qui
 *       n'a aucun declencheur naturel a travers l'API.</li>
 * </ul>
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:h2:mem:sportstore-cucumber;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
public class CucumberSpringConfiguration {

    @MockitoSpyBean
    private ArticleRepository articleRepository;
}
