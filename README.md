# sportStore

Catalogue d'articles de sport — Java 25 / Spring Boot 4.0 / Maven / H2 en mémoire.

## Démarrage

```bash
mvn spring-boot:run          # http://localhost:8080
mvn test
cd bruno && npx --yes @usebruno/cli run --env local -r
```

|            |                                                                                                      |
|------------|------------------------------------------------------------------------------------------------------|
| Swagger UI | <http://localhost:8080/openapi/index.html>                                                           |
| Console H2 | <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:sportstore`, user `sa`, sans mot de passe) |

## API

```bash
curl http://localhost:8080/store/article-names
curl "http://localhost:8080/store/articles?category=Fitness"
curl "http://localhost:8080/store/articles/Soccer%20Ball"
curl -X PUT http://localhost:8080/store/articles -H "Content-Type: application/json" \
     -d '{"name":"Insulated Water Bottle","category":"Accessories","price":19.90}'
curl -X DELETE "http://localhost:8080/store/articles/Insulated%20Water%20Bottle"
```

Le nom étant l'identifiant naturel, `PUT` conserve l'`id` de l'article existant et ne remplace que
ses caractéristiques. La liste des noms est exposée sur sa propre ressource
`/store/article-names` : sous `/store/articles/{name}`, `{name}` désigne toujours un article,
sans nom réservé.

`src/main/resources/data.sql` insère 5 articles au démarrage : Soccer Ball, Tennis Racket,
Running Shoes, Yoga Mat, Mountain Bike Helmet.

## Architecture

Tout le détail technique — packages, conventions de nommage, règles du contrat, mapping, gestion
des erreurs, persistance, journalisation, emplacement des tests — est dans **[CLEAN_ARCHI.md](CLEAN_ARCHI.md)**.
