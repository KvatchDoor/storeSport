# language: fr
Fonctionnalité: Consultation du catalogue
  En tant que client du magasin de sport
  Je veux consulter les articles proposés
  Afin de choisir ce que je vais acheter

  Contexte:
    Etant donné le catalogue suivant :
      | nom                  | categorie     | prix   |
      | Soccer Ball          | Team Sports   | 29.99  |
      | Tennis Racket        | Racket Sports | 89.50  |
      | Yoga Mat             | Fitness       | 24.90  |
      | Mountain Bike Helmet | Cycling       | 59.95  |

  Scénario: Les noms du catalogue sont listés par ordre alphabétique
    Quand je demande la liste des noms d'articles
    Alors la reponse a le statut 200
    Et les noms retournes sont, dans cet ordre :
      | Mountain Bike Helmet |
      | Soccer Ball          |
      | Tennis Racket        |
      | Yoga Mat             |

  Scénario: Un catalogue vide se lit sans erreur
    Etant donné un catalogue vide
    Quand je demande la liste des noms d'articles
    Alors la reponse a le statut 200
    Et aucun nom n'est retourne

  Scénario: Le catalogue complet est retourné trié par nom
    Quand je demande la liste des articles
    Alors la reponse a le statut 200
    Et les articles retournes sont :
      | nom                  | categorie     | prix  |
      | Mountain Bike Helmet | Cycling       | 59.95 |
      | Soccer Ball          | Team Sports   | 29.99 |
      | Tennis Racket        | Racket Sports | 89.50 |
      | Yoga Mat             | Fitness       | 24.90 |

  Scénario: Le catalogue peut être filtré sur une catégorie
    Quand je demande les articles de la categorie "Team Sports"
    Alors la reponse a le statut 200
    Et les articles retournes sont :
      | nom         | categorie   | prix  |
      | Soccer Ball | Team Sports | 29.99 |

  Scénario: Une catégorie inconnue n'est pas une erreur
    Quand je demande les articles de la categorie "Curling"
    Alors la reponse a le statut 200
    Et aucun article n'est retourne

  Scénario: Un filtre de catégorie vide équivaut à une absence de filtre
    Quand je demande les articles avec un filtre de categorie vide
    Alors la reponse a le statut 200
    Et les articles retournes sont :
      | nom                  | categorie     | prix  |
      | Mountain Bike Helmet | Cycling       | 59.95 |
      | Soccer Ball          | Team Sports   | 29.99 |
      | Tennis Racket        | Racket Sports | 89.50 |
      | Yoga Mat             | Fitness       | 24.90 |

  Scénario: Un article se consulte par son nom, espaces compris
    Quand je consulte l'article "Soccer Ball"
    Alors la reponse a le statut 200
    Et l'article retourne est :
      | nom         | categorie   | prix  |
      | Soccer Ball | Team Sports | 29.99 |

  Scénario: Consulter un article absent du catalogue répond 404
    Quand je consulte l'article "Bicycle"
    Alors la reponse a le statut 404
    Et le message d'erreur est "Article not found: Bicycle"
