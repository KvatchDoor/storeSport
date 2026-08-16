# Architecture — sportStore

Référence technique du projet. Tout code produit ici doit s'y conformer.

Java 25 / Spring Boot 4.0 / Maven / H2 en mémoire. Architecture **hexagonale (ports & adapters)**.

## 1. Sens de dépendance

```
infrastructure  ──▶  application  ──▶  domain
   (adaptateurs)        (ports + services)      (aucune dépendance technique)
```

La règle ne se discute pas et n'est pas déclarative : `HexagonalArchitectureTest` analyse les
imports du code source, sans contexte Spring, et échoue à la moindre inversion.

## 2. Conventions de nommage

| Élément                  | Convention                                | Exemple                    |
|--------------------------|-------------------------------------------|----------------------------|
| Port entrant             | `*UseCase`, une capacité par port         | `ListArticlesUseCase`      |
| Entrée d'un port entrant | `*Command`                                | `UpsertArticleCommand`     |
| Service applicatif       | `*Service`, implémente un port `in`       | `UpsertArticleService`     |
| Port sortant             | `*Repository`                             | `ArticleRepository`        |
| Adaptateur sortant       | `Jpa*Repository`                          | `JpaArticleRepository`     |
| Entité JPA               | `*JpaEntity`                              | `ArticleJpaEntity`         |
| DTO HTTP                 | `ArticleResponse`, `UpsertArticleRequest` |
| Value Object             | record, nom du concept métier             | `ArticleName`, `Price`     |
| Exception métier         | `*Exception` dans `domain/exception/`     | `ArticleNotFoundException` |

## 3. Trois modèles distincts, un par couche

| Couche          | Modèle                                                          |
|-----------------|-----------------------------------------------------------------|
| API HTTP        | `ArticleResponse` / `UpsertArticleRequest`                      |
| Domaine / ports | `Article` + Value Objects (`ArticleName`, `Category`, `Price`)  |
| Persistance     | `ArticleJpaEntity` — ne quitte jamais `adapter/out/persistence` |

Aucun type technique ne traverse un port : `ArticleRepository` n'expose que des objets du domaine.

## 4. Checklist

1. **`domain/` sans Spring ni JPA** — vérifié par `HexagonalArchitectureTest` (imports + absence de `@Entity`, `@Service`, `@Component`, `@Repository`, `@Autowired`).
2. **Un port = une capacité** — un port `in` par cas d'usage ; pas de CRUD fourre-tout.
3. **Adaptateurs dans `infrastructure/`** — `application/` ne contient ni contrôleur ni code JPA.
4. **Aucun type technique à travers un port** — l'entité JPA et les DTO REST restent dans leur adaptateur.
5. **Exceptions techniques mappées** — aucune exception de framework au-dessus de l'adaptateur.
6. **Tests du domaine sans contexte Spring** — JUnit pur.
