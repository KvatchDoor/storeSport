# language: fr
Fonctionnalité: Gestion des stocks
  En tant que responsable de stock
  Je veux que la consultation d'un article décrémente son stock
  Afin de suivre les niveaux de stock et d'éviter les ruptures

  Contexte:
    Etant donné le catalogue suivant :
      | nom           | categorie     | prix  |
      | Soccer Ball   | Team Sports   | 29.99 |
      | Tennis Racket | Racket Sports | 89.50 |

  Scénario: La consultation d'un article décrémente son stock
    Etant donné l'article "Soccer Ball" initialise avec un stock de 1
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 0

  Scénario: Plusieurs consultations décrémentes successivement le stock
    Etant donné l'article "Soccer Ball" initialise avec un stock de 5
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 4
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 4
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 4

  Scénario: Consulter un article en rupture lève une exception
    Etant donné l'article "Soccer Ball" initialise avec un stock de 0
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 400
    Et le message d'erreur est "Out of stock: Soccer Ball"

  Scénario: Un nouvel article initiale le stock à 0
    Quand j'enregistre l'article suivant :
      | nom                    | categorie   | prix  |
      | Insulated Water Bottle | Accessories | 19.90 |
    Alors la reponse a le statut 200
    Et le stock retourne est 0
    Quand je consulte l'article "Insulated Water Bottle"
    Alors la reponse a le statut 400
    Et le message d'erreur est "Out of stock: Insulated Water Bottle"

  Scénario: Remplacement d'article conserve le stock
    Etant donné l'article "Soccer Ball" initialise avec un stock de 5
    Et je note l'identifiant de l'article "Soccer Ball"
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 4
    Quand j'enregistre l'article suivant :
      | nom         | categorie | prix |
      | Soccer Ball | Outdoor   | 34.50 |
    Alors la reponse a le statut 200
    Et l'identifiant retourne est celui qui a ete note
    Et le stock retourne est 5

  Scénario: La liste des stocks retourne tous les articles avec leur quantite
    Etant donné l'article "Soccer Ball" initialise avec un stock de 3
    Etant donné l'article "Tennis Racket" initialise avec un stock de 7
    Quand je demande la liste des stocks
    Alors la reponse a le statut 200
    Et la liste des stocks contient :
      | nom           | quantity |
      | Soccer Ball   | 3        |
      | Tennis Racket | 7        |

  Scénario: La suppression d'un article retire aussi son stock
    Etant donné l'article "Soccer Ball" initialise avec un stock de 5
    Quand je supprime l'article "Soccer Ball"
    Alors la reponse a le statut 204
    Quand je demande la liste des stocks
    Alors la reponse a le statut 200
    Et la liste des stocks ne contient pas "Soccer Ball"

  Scénario: Les stocks de différents articles sont isolés
    Etant donné l'article "Soccer Ball" initialise avec un stock de 5
    Etant donné l'article "Tennis Racket" initialise avec un stock de 3
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et le stock retourne est 4
    Quand je demande la liste des stocks
    Alors la reponse a le statut 200
    Et les stocks pour "Soccer Ball" et "Tennis Racket" sont respectivement 5 et 3
