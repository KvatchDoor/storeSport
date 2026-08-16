package com.sportstore.infrastructure.adapter.in.rest.cucumber;

import com.jayway.jsonpath.JsonPath;
import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Steps des scenarios du catalogue. Une instance par scenario : cucumber-spring place les classes
 * de glue dans le scope {@code cucumber-glue}, aucun etat ne fuit d'un scenario a l'autre.
 */
public class ArticleCatalogueSteps {

    private static final String NOM = "nom";
    private static final String CATEGORIE = "categorie";
    private static final String PRIX = "prix";

    private final MockMvc mockMvc;
    private final ArticleRepository articleRepository;
    private final JdbcTemplate jdbcTemplate;

    private ResultActions response;
    private String notedId;

    public ArticleCatalogueSteps(MockMvc mockMvc, ArticleRepository articleRepository, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.articleRepository = articleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Chaque scenario part d'un catalogue vide et d'un port sortant qui delegue au vrai adaptateur :
     * ni le jeu de donnees de {@code data.sql}, ni un stub pose par le scenario precedent ne
     * survivent ici.
     */
    @Before
    public void resetCatalogue() {
        Mockito.reset(articleRepository);
        jdbcTemplate.update("DELETE FROM article");
    }

    // ------------------------------------------------------------------ Etant donne

    @Given("le catalogue suivant :")
    public void leCatalogueSuivant(DataTable articles) {
        articles.asMaps().forEach(row -> articleRepository.save(Article.create(
                new ArticleName(row.get(NOM)), new Category(row.get(CATEGORIE)), Price.of(row.get(PRIX)))));
    }

    @Given("un catalogue vide")
    public void unCatalogueVide() {
        jdbcTemplate.update("DELETE FROM article");
    }

    @Given("je note l'identifiant de l'article {string}")
    public void jeNoteIdentifiant(String name) {
        notedId = articleRepository.findByName(new ArticleName(name))
                .orElseThrow(() -> new AssertionError("L'article " + name + " devrait exister avant ce scenario"))
                .id()
                .toString();
    }

    @Given("le stockage du catalogue est en panne")
    public void leStockageEstEnPanne() {
        willThrow(new ArticleStorageException("Lecture des noms d'articles impossible", connexionPerdue()))
                .given(articleRepository).findAllNames();
        willThrow(new ArticleStorageException("Lecture du catalogue impossible", connexionPerdue()))
                .given(articleRepository).findAll();
        willThrow(new ArticleStorageException("Enregistrement impossible", connexionPerdue()))
                .given(articleRepository).save(any(Article.class));
    }

    // ------------------------------------------------------------------ Quand

    @When("je demande la liste des noms d'articles")
    public void jeDemandeLesNoms() throws Exception {
        response = mockMvc.perform(get("/store/article-names"));
    }

    @When("je demande la liste des articles")
    public void jeDemandeLesArticles() throws Exception {
        response = mockMvc.perform(get("/store/articles"));
    }

    @When("je demande les articles de la categorie {string}")
    public void jeDemandeLesArticlesDeLaCategorie(String category) throws Exception {
        response = mockMvc.perform(get("/store/articles").param("category", category));
    }

    @When("je demande les articles avec un filtre de categorie vide")
    public void jeDemandeLesArticlesSansFiltre() throws Exception {
        response = mockMvc.perform(get("/store/articles").param("category", ""));
    }

    @When("je consulte l'article {string}")
    public void jeConsulteArticle(String name) throws Exception {
        response = mockMvc.perform(get("/store/articles/{name}", name));
    }

    @When("j'enregistre l'article suivant :")
    public void jEnregistreArticle(DataTable article) throws Exception {
        Map<String, String> row = article.asMaps().getFirst();
        response = performUpsert(payload(row.get(NOM), row.get(CATEGORIE), row.get(PRIX)));
    }

    @When("j'enregistre un article dont le nom fait {int} caracteres")
    public void jEnregistreUnNomTropLong(int length) throws Exception {
        response = performUpsert(payload("A".repeat(length), "Accessories", "19.90"));
    }

    @When("j'enregistre le corps JSON suivant :")
    public void jEnregistreLeCorpsJson(String body) throws Exception {
        response = performUpsert(body);
    }

    @When("je supprime l'article {string}")
    public void jeSupprimeArticle(String name) throws Exception {
        response = mockMvc.perform(delete("/store/articles/{name}", name));
    }

    // ------------------------------------------------------------------ Alors

    @Then("la reponse a le statut {int}")
    public void laReponseALeStatut(int status) throws Exception {
        assertThat(response.andReturn().getResponse().getStatus())
                .as("statut HTTP, corps recu : %s", body())
                .isEqualTo(status);
    }

    @Then("les noms retournes sont, dans cet ordre :")
    public void lesNomsRetournesSont(DataTable expected) throws Exception {
        assertThat(names()).as("noms du catalogue").containsExactlyElementsOf(expected.asList());
    }

    @Then("aucun nom n'est retourne")
    public void aucunNomRetourne() throws Exception {
        assertThat(names()).as("noms du catalogue").isEmpty();
    }

    @Then("les articles retournes sont :")
    public void lesArticlesRetournesSont(DataTable expected) throws Exception {
        assertThat(articles()).as("articles retournes").containsExactlyElementsOf(normalize(expected));
    }

    @Then("aucun article n'est retourne")
    public void aucunArticleRetourne() throws Exception {
        assertThat(articles()).as("articles retournes").isEmpty();
    }

    @Then("l'article retourne est :")
    public void articleRetourneEst(DataTable expected) throws Exception {
        Map<String, String> actual = new LinkedHashMap<>();
        actual.put(NOM, jsonPath("$.name"));
        actual.put(CATEGORIE, jsonPath("$.category"));
        actual.put(PRIX, twoDecimals(jsonPath("$.price")));

        assertThat(actual).as("article retourne").isEqualTo(normalize(expected).getFirst());
    }

    @Then("l'identifiant retourne est celui qui a ete note")
    public void identifiantInchange() throws Exception {
        assertThat(this.<String>jsonPath("$.id"))
                .as("un remplacement conserve l'identifiant de l'article")
                .isEqualTo(notedId);
    }

    @Then("le message d'erreur est {string}")
    public void leMessageErreurEst(String message) throws Exception {
        assertThat(body())
                .as("le contrat impose un corps ErrorResponse sur toute reponse d'erreur, celui-ci est vide")
                .isNotEmpty();
        assertThat(this.<String>jsonPath("$.error")).as("corps d'erreur de l'API").isEqualTo(message);
    }

    @Then("la reponse n'a pas de corps")
    public void laReponseNaPasDeCorps() throws Exception {
        assertThat(body()).as("corps de la reponse").isEmpty();
    }

    @Then("le catalogue compte {int} article(s)")
    public void leCatalogueCompte(int expected) {
        assertThat(articleRepository.findAll()).as("articles reellement persistes").hasSize(expected);
    }

    // ------------------------------------------------------------------ Outillage

    private ResultActions performUpsert(String body) throws Exception {
        return mockMvc.perform(put("/store/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    /**
     * Cucumber rend {@code null} une cellule vide de Data Table. Une cellule vide vaut donc ici
     * une valeur vide soumise a l'API, pas un champ absent : les champs absents sont exprimes
     * par les scenarios qui posent directement le corps JSON.
     */
    private static String payload(String name, String category, String price) {
        return "{\"name\": %s, \"category\": %s, \"price\": %s}"
                .formatted(quote(name), quote(category), price == null ? "null" : price);
    }

    private static String quote(String value) {
        String submitted = value == null ? "" : value;
        return "\"" + submitted.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private List<String> names() throws Exception {
        return this.<List<Object>>jsonPath("$[*]").stream().map(String::valueOf).toList();
    }

    private List<Map<String, String>> articles() throws Exception {
        return this.<List<Map<String, Object>>>jsonPath("$[*]").stream()
                .map(article -> {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put(NOM, String.valueOf(article.get("name")));
                    row.put(CATEGORIE, String.valueOf(article.get("category")));
                    row.put(PRIX, twoDecimals(article.get("price")));
                    return row;
                })
                .toList();
    }

    private static List<Map<String, String>> normalize(DataTable expected) {
        return expected.asMaps().stream()
                .map(row -> {
                    Map<String, String> normalized = new LinkedHashMap<>();
                    normalized.put(NOM, row.get(NOM));
                    normalized.put(CATEGORIE, row.get(CATEGORIE));
                    normalized.put(PRIX, twoDecimals(row.get(PRIX)));
                    return normalized;
                })
                .toList();
    }

    private static String twoDecimals(Object price) {
        return new BigDecimal(String.valueOf(price)).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    @SuppressWarnings("unchecked")
    private <T> T jsonPath(String path) throws Exception {
        return (T) JsonPath.read(body(), path);
    }

    private String body() throws UnsupportedEncodingException {
        return response.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    private static DataAccessResourceFailureException connexionPerdue() {
        return new DataAccessResourceFailureException("connexion a la base perdue");
    }
}
