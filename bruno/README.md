# Tests d'API Bruno — sportStore

Collection [Bruno](https://www.usebruno.com/) couvrant les 5 endpoints de `/store/articles`
en boite noire, sur une application demarree.

## Prerequis

L'application doit tourner :

```bash
mvn spring-boot:run          # http://localhost:8080
```

La base H2 est en memoire et recreee a chaque demarrage (`ddl-auto=create-drop` +
`data.sql`) : la collection retrouve toujours les 5 articles du jeu de donnees initial.

## Execution

**Application Bruno (GUI)** : *Open Collection* → dossier `bruno/`, puis selectionner
l'environnement **local** en haut a droite.

**Ligne de commande** :

```bash
cd bruno
npx --yes @usebruno/cli run --env local -r
```

`-r` parcourt les sous-dossiers de facon recursive. Ajouter `--reporter-html rapport.html`
pour un rapport, ou `--bail` pour s'arreter a la premiere requete en echec.

## Structure

| Dossier | Contenu |
|---|---|
| `contrat/` | publication du contrat OpenAPI et de Swagger UI |
| `catalogue/` | lectures seules : `GET article-names`, `GET articles` (avec et sans filtre), `GET articles/{name}`, 404 |
| `cycle-de-vie/` | scenario ordonne creation → relecture → mise a jour → suppression → 404 |
| `validation/` | corps invalides (400), invariants du domaine, normalisation (trim / arrondi) |

## Conventions

- **Environnement** : `environments/local.bru` definit `baseUrl` et le nom de l'article de test.
  Pour viser une autre instance, dupliquer ce fichier plutot que de modifier les requetes.
- **Ordre** : les requetes de `cycle-de-vie/` sont numerotees et **doivent** s'executer en
  sequence — `01-create-article` publie l'`id` genere dans la variable d'execution
  `createdArticleId`, que les etapes suivantes verifient pour prouver que l'upsert conserve
  l'identite. Les autres dossiers sont independants.
- **Rejouabilite** : chaque article cree est supprime par la collection (`cycle-de-vie/05`,
  `validation/cleanup`). Un second passage sur la meme instance doit repasser au vert.
- **Assertions** : le bloc `assert` porte le statut HTTP attendu, le bloc `tests` la forme et
  le contenu du corps. Les listes sont verifiees par inclusion (`include.members`), jamais par
  egalite stricte, pour ne pas casser si le catalogue evolue.
- **Messages d'erreur** : les 400 issus du contrat sont verifies par le prefixe du champ fautif
  (`/^price : /`) plutot que par leur phrase complete. Le texte vient de Bean Validation, il
  n'appartient pas au code du projet.

## Perimetre

Ces tests completent, sans les remplacer, les tests Maven (`mvn test`) : ils s'executent sur
l'application reellement demarree et valident le contrat HTTP publie (statuts, format
`{"error": "..."}`, encodage des noms dans l'URL, serialisation des prix a deux decimales).

Depuis le passage en contract-first, ils jouent un role supplementaire : le compilateur garantit
que le controleur respecte les **signatures** du contrat, mais rien ne garantit que les **corps**
de reponse correspondent aux schemas declares. C'est cette collection qui le verifie, sur
l'application reelle.
