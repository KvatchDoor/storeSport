# language: fr
Fonctionnalité: Refus des articles invalides
  En tant que gestionnaire du magasin
  Je veux que le catalogue refuse une saisie incohérente
  Afin qu'aucun article invalide ne soit publié

  Toute erreur sort au même format, celui du contrat : un objet portant le seul champ "error".
  Le contrat filtre la forme du corps ; les invariants qu'il ne sait pas exprimer restent gardés
  par le domaine, qui répond lui aussi en 400.

  Contexte:
    Etant donné le catalogue suivant :
      | nom         | categorie   | prix  |
      | Soccer Ball | Team Sports | 29.99 |

  Plan du scénario: Le contrat refuse un article dont un champ est vide ou hors bornes
    Quand j'enregistre l'article suivant :
      | nom   | categorie   | prix   |
      | <nom> | <categorie> | <prix> |
    Alors la reponse a le statut 400
    Et le message d'erreur est "<message>"
    Et le catalogue compte 1 article

    Exemples:
      | nom          | categorie   | prix  | message                                    |
      |              | Accessories | 19.90 | name : size must be between 1 and 120      |
      | Water Bottle |             | 19.90 | category : size must be between 1 and 80   |
      | Water Bottle | Accessories | -1    | price : must be greater than or equal to 0 |

  Scénario: Le contrat refuse un nom plus long que la limite publiée
    Quand j'enregistre un article dont le nom fait 121 caracteres
    Alors la reponse a le statut 400
    Et le message d'erreur est "name : size must be between 1 and 120"

  Scénario: Un nom de la longueur maximale publiée est accepté
    Quand j'enregistre un article dont le nom fait 120 caracteres
    Alors la reponse a le statut 200
    Et le catalogue compte 2 articles

  Scénario: Le contrat refuse un article sans prix
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "Insulated Water Bottle", "category": "Accessories"}
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "price : must not be null"

  Scénario: Le contrat refuse un article sans nom
    Quand j'enregistre le corps JSON suivant :
      """
      {"category": "Accessories", "price": 19.90}
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "name : must not be null"

  Scénario: Un corps JSON illisible est refusé au format d'erreur du contrat
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "Insulated Water Bottle", "category": "Accessories", "price":
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "Invalid request payload"
    Et le catalogue compte 1 article

  Scénario: Un prix qui n'est pas un nombre ne fait pas fuir le détail technique de l'analyseur JSON
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "Insulated Water Bottle", "category": "Accessories", "price": "gratuit"}
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "Invalid request payload"
    Et le catalogue compte 1 article

  Scénario: Un nom fait uniquement d'espaces satisfait le contrat mais viole l'invariant du domaine
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "   ", "category": "Accessories", "price": 19.90}
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "Le nom de l'article est obligatoire"
    Et le catalogue compte 1 article

  Scénario: Une catégorie faite uniquement d'espaces est refusée par le domaine
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "Insulated Water Bottle", "category": "   ", "price": 19.90}
      """
    Alors la reponse a le statut 400
    Et le message d'erreur est "La categorie de l'article est obligatoire"
    Et le catalogue compte 1 article

  Scénario: Une panne de stockage est signalée en 500, au format d'erreur du contrat
    Etant donné que le stockage du catalogue est en panne
    Quand je demande la liste des noms d'articles
    Alors la reponse a le statut 500
    Et le message d'erreur est "Storage failure: Lecture des noms d'articles impossible"
