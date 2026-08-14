# sportStore

Catalogue d'articles de sport — Java 25 / Spring Boot 4.0 / Maven / H2 en mémoire,
implémenté en **architecture hexagonale (ports & adapters)**.

## Démarrage

```bash
mvn spring-boot:run          # http://localhost:8080
mvn test                     # 41 tests
```

Console H2 : <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:sportstore`, user `sa`, pas de mot de passe).

## Structure

```
src/main/java/com/sportstore
├── SportStoreApplication.java
├── domain/                                     # AUCUNE dépendance Spring / JPA / Jackson
│   ├── model/          Article, ArticleId, ArticleName, Category, Price
│   └── exception/      ArticleNotFoundException, InvalidArticleException
├── application/
│   ├── port/in/        ListArticleNamesUseCase, ListArticlesUseCase, GetArticleUseCase,
│   │                   UpsertArticleUseCase (+ UpsertArticleCommand), DeleteArticleUseCase
│   ├── port/out/       ArticleRepository, ArticleStorageException
│   └── service/        ListArticleNamesService, ListArticlesService, GetArticleService,
│                       UpsertArticleService, DeleteArticleService
└── infrastructure/
    ├── adapter/in/rest/            ArticleController, GlobalExceptionHandler,
    │                               ArticleWebMapper, dto/{ArticleResponse, UpsertArticleRequest, ErrorResponse}
    └── adapter/out/persistence/    JpaArticleRepository (impl. du port),
                                    ArticleSpringDataRepository, ArticleJpaEntity, ArticlePersistenceMapper

src/main/resources/  application.properties, data.sql
```

Sens de dépendance : `infrastructure → application → domain`. Il est **vérifié automatiquement** par
`HexagonalArchitectureTest` (analyse des imports du code source, sans contexte Spring).

### Trois modèles distincts, un par couche

| Couche | Modèle |
|---|---|
| API HTTP | `ArticleResponse` / `UpsertArticleRequest` (DTO) |
| Domaine / ports | `Article` + Value Objects (`ArticleName`, `Category`, `Price`) |
| Persistance | `ArticleJpaEntity` (ne quitte jamais le package `adapter/out/persistence`) |

## API

Base : `/store/articles`

| Verbe | URI | Réponse |
|---|---|---|
| GET | `/store/articles/names` | `200` — `["Soccer Ball", "Tennis Racket"]` |
| GET | `/store/articles?category={category}` | `200` — liste d'articles (paramètre optionnel) |
| GET | `/store/articles/{name}` | `200` — article, `404` `{"error": "Article not found: Bicycle"}` |
| PUT | `/store/articles` | `200` — upsert par nom (création ou remplacement complet) |
| DELETE | `/store/articles/{name}` | `204` sans corps, `404` si inconnu |

```bash
curl http://localhost:8080/store/articles/names
curl "http://localhost:8080/store/articles?category=Fitness"
curl "http://localhost:8080/store/articles/Soccer%20Ball"
curl -X PUT http://localhost:8080/store/articles -H "Content-Type: application/json" \
     -d '{"name":"Insulated Water Bottle","category":"Accessories","price":19.90}'
curl -X DELETE "http://localhost:8080/store/articles/Insulated%20Water%20Bottle"
```

> Le nom étant l'identifiant naturel, `PUT` conserve l'`id` de l'article existant et ne remplace que
> ses caractéristiques. Le chemin littéral `/store/articles/names` a priorité sur `/store/articles/{name}` :
> un article ne peut pas s'appeler « names ».

### Codes d'erreur

| Situation | Statut | Corps |
|---|---|---|
| Article inconnu (`ArticleNotFoundException`) | 404 | `{"error": "Article not found: ..."}` |
| Corps invalide (Bean Validation ou invariant du domaine) | 400 | `{"error": "price : price must not be negative"}` |
| Panne de stockage (`ArticleStorageException`) | 500 | `{"error": "Storage failure: ..."}` |

## Jeu de données initial

`src/main/resources/data.sql` insère 5 articles au démarrage (`spring.sql.init.mode=always` +
`spring.jpa.defer-datasource-initialization=true`, pour que le script s'exécute après la création du
schéma par Hibernate) : Soccer Ball, Tennis Racket, Running Shoes, Yoga Mat, Mountain Bike Helmet.

## Tests (41)

| Test | Portée |
|---|---|
| `domain/model/ArticleTest` | domaine pur, **sans** contexte Spring |
| `application/service/ArticleServicesTest` | services applicatifs sur un fake du port `out` (`InMemoryArticleRepository`) |
| `infrastructure/.../ArticleControllerTest` | `@WebMvcTest` — ports `in` **mockés**, aucun use case réel |
| `infrastructure/.../JpaArticleRepositoryIntegrationTest` | `@DataJpaTest` — adaptateur `out` sur H2 |
| `SportStoreApplicationIntegrationTest` | bout en bout, `data.sql` inclus |
| `architecture/HexagonalArchitectureTest` | règles de dépendance de l'architecture |

## Checklist d'architecture

1. **`domain/` sans Spring ni JPA** — vérifié par `HexagonalArchitectureTest` (imports + absence de `@Entity`, `@Service`, `@Component`, `@Repository`, `@Autowired`).
2. **Un port = une capacité** — 5 ports `in`, un par cas d'usage ; pas de CRUD fourre-tout.
3. **Adaptateurs dans `infrastructure/`** — `application/` ne contient ni contrôleur ni code JPA.
4. **Aucun type technique à travers un port** — `ArticleRepository` n'expose que des objets du domaine ; l'entité JPA et les DTO REST sont confinés à leur adaptateur.
5. **Exceptions techniques mappées** — `JpaArticleRepository` convertit toute `DataAccessException` en `ArticleStorageException` ; aucune exception de framework ne remonte au-dessus de l'adaptateur.
6. **Tests du domaine sans contexte Spring** — JUnit pur.

Points complémentaires : le repository Spring Data est *package-private* et n'est utilisé que par
`JpaArticleRepository`, jamais injecté dans un service applicatif ; l'identité (`ArticleId`) est générée
par le domaine, pas par la base.

## Notes de version

Spring Boot 4 a modularisé les slices de test : `@WebMvcTest` / `@AutoConfigureMockMvc` viennent de
`spring-boot-starter-webmvc-test` (`org.springframework.boot.webmvc.test.autoconfigure`) et `@DataJpaTest`
de `spring-boot-starter-data-jpa-test` (`org.springframework.boot.data.jpa.test.autoconfigure`).
Sur JDK 25, Mockito est déclaré comme `-javaagent` via `maven-dependency-plugin:properties`.
