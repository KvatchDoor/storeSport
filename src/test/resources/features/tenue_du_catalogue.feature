# language: fr
Fonctionnalité: Tenue du catalogue
  En tant que gestionnaire du magasin
  Je veux créer, remplacer et retirer des articles
  Afin que le catalogue reflète ce qui est réellement vendu

  Le nom est l'identifiant naturel de l'article : un PUT sur un nom existant remplace
  l'article sans lui donner un nouvel identifiant, et répond 200 dans tous les cas.

  Contexte:
    Etant donné le catalogue suivant :
      | nom           | categorie     | prix  |
      | Soccer Ball   | Team Sports   | 29.99 |
      | Tennis Racket | Racket Sports | 89.50 |
      | Yoga Mat      | Fitness       | 24.90 |

  Scénario: Enregistrer un nom inconnu crée l'article
    Quand j'enregistre l'article suivant :
      | nom                    | categorie   | prix  |
      | Insulated Water Bottle | Accessories | 19.90 |
    Alors la reponse a le statut 200
    Et l'article retourne est :
      | nom                    | categorie   | prix  |
      | Insulated Water Bottle | Accessories | 19.90 |
    Et le catalogue compte 4 articles

  Scénario: L'article créé est immédiatement consultable
    Quand j'enregistre l'article suivant :
      | nom                    | categorie   | prix  |
      | Insulated Water Bottle | Accessories | 19.90 |
    Et je consulte l'article "Insulated Water Bottle"
    Alors la reponse a le statut 200
    Et l'article retourne est :
      | nom                    | categorie   | prix  |
      | Insulated Water Bottle | Accessories | 19.90 |

  Scénario: Enregistrer un nom existant remplace l'article sans changer son identifiant
    Etant donné que je note l'identifiant de l'article "Yoga Mat"
    Quand j'enregistre l'article suivant :
      | nom      | categorie | prix  |
      | Yoga Mat | Pilates   | 32.00 |
    Alors la reponse a le statut 200
    Et l'identifiant retourne est celui qui a ete note
    Et l'article retourne est :
      | nom      | categorie | prix  |
      | Yoga Mat | Pilates   | 32.00 |
    Et le catalogue compte 3 articles

  Plan du scénario: Le prix est arrondi à deux décimales au plus près
    Quand j'enregistre l'article suivant :
      | nom       | categorie   | prix     |
      | Jump Rope | Accessories | <soumis> |
    Alors la reponse a le statut 200
    Et l'article retourne est :
      | nom       | categorie   | prix     |
      | Jump Rope | Accessories | <retenu> |

    Exemples:
      | soumis | retenu |
      | 12.994 | 12.99  |
      | 12.995 | 13.00  |
      | 12     | 12.00  |
      | 0      | 0.00   |

  Scénario: Les espaces autour du nom ne créent pas de doublon
    Etant donné que je note l'identifiant de l'article "Yoga Mat"
    Quand j'enregistre le corps JSON suivant :
      """
      {"name": "   Yoga Mat   ", "category": "Pilates", "price": 32.00}
      """
    Alors la reponse a le statut 200
    Et l'identifiant retourne est celui qui a ete note
    Et l'article retourne est :
      | nom      | categorie | prix  |
      | Yoga Mat | Pilates   | 32.00 |
    Et le catalogue compte 3 articles

  Scénario: Supprimer un article le retire du catalogue
    Quand je supprime l'article "Yoga Mat"
    Alors la reponse a le statut 204
    Et la reponse n'a pas de corps
    Et le catalogue compte 2 articles

  Scénario: Un article supprimé n'est plus consultable
    Quand je supprime l'article "Yoga Mat"
    Et je consulte l'article "Yoga Mat"
    Alors la reponse a le statut 404
    Et le message d'erreur est "Article not found: Yoga Mat"

  Scénario: La suppression n'est pas idempotente
    Quand je supprime l'article "Yoga Mat"
    Et je supprime l'article "Yoga Mat"
    Alors la reponse a le statut 404
    Et le message d'erreur est "Article not found: Yoga Mat"

  Scénario: Supprimer un nom inconnu répond 404
    Quand je supprime l'article "Bicycle"
    Alors la reponse a le statut 404
    Et le message d'erreur est "Article not found: Bicycle"
    Et le catalogue compte 3 articles
