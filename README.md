
# SAE-5.A.1-API

Ce projet contient tout le backend de la SAE.

## Installation

**Important :** Le projet fonctionne avec Java 21, veuillez vérifier que votre JVM ET votre compilateur Gradle sont en Java 21. Si vous êtes sur Intellij IDEA, modifiez dans `Project Structure` la JVM et dans les paramètres allez dans `Build, Execution, Deployment > Build Tools > Gradle` et vérifier la Gradle JVM. 

### 1. Configuration de la base de données

Le projet utilise un fichier `application.properties` (Spring Boot) pour stocker la configuration de la base de données.

1. Si le fichier n’existe pas, il faut le créer dans `src/main/resources/application.properties`.
2. Ajoutez les propriétés suivantes avec les informations de ta base de données :

```properties
spring.application.name=R5A12-API

db.host=VOTRE_HOST
db.port=VOTRE_PORT
db.name=VOTRE_NOM_DE_BASE
db.user=VOTRE_UTILISATEUR
db.password=VOTRE_MOT_DE_PASSE
```

> **Remarque** : Ce fichier est très risqué, il contient l'accès à votre base de données, nous vous recommandons de ne JAMAIS le stocker autre part que sur votre environnement de développement et de production.

> ### Développement / Production : 
> Pour démarrer le projet, il vous suffit de cloner ce dépot et aller dans le fichier `SAE5A1ApiApplication` et démarrer le `main()`. Si vous êtes en production, il faudra compiler avec Gradle et lancer le fichier générer.

> ### Production simplifiée :
> Nous avons simplifier la mise en production, vous avez un fichier Dockerfile, il suffit de construire et de démarrer un containeur avec cette image. Celui-ci va compiler manuellement et démarrer le projet.

## Authors

- [@Curiosow](https://www.github.com/Curiosow)
- [@8rubi](https://www.github.com/8rubi)
- [@MatisT05](https://www.github.com/MatisT05)
- [@NoaBaj](https://www.github.com/NoaBaj)
- [@TWP444](https://www.github.com/TWP444)

## Tech Stack

### Langage et Framework

**Spring Boot 3.5.5**
- Framework principal qui simplifie considérablement le développement d'applications Java
- Configuration automatique réduisant le code
- Intégration native avec de nombreux outils et bibliothèques
- Support natif pour la création d'APIs REST
- Facilité de déploiement avec des exécutables JAR

### Modules Spring

**Spring Web**
- Création d'APIs REST avec des annotations simples (`@RestController`, `@RequestMapping`)
- Gestion automatique de la sérialisation/désérialisation JSON
- Gestion des exceptions centralisée

**Spring Boot DevTools**
- Rechargement automatique de l'application lors des modifications
- Améliore considérablement la productivité en développement
- Redémarrage automatique du serveur lors des changements de code
- Configuration par défaut optimisée pour le développement

### Base de données

**PostgreSQL**
- Base de données relationnelle robuste et open-source
- Excellentes performances pour les applications
- Conformité ACID garantissant l'intégrité des données

**HikariCP 4.0.3**
- Pool de connexions haute performance pour la gestion des connexions à la base de données
- Réduit la surcharge liée à l'ouverture/fermeture de connexions
- Améliore significativement les performances de l'application
- Configuration optimisée par défaut
- Considéré comme l'un des pools de connexions les plus rapides pour Java

**H2 Database**
- Base de données en mémoire utilisée pour les tests
- Permet d'exécuter les tests sans dépendre d'une base de données externe
- Démarrage rapide et configuration minimale
- Idéal pour les tests unitaires et d'intégration

### Bibliothèques utilitaires

**Lombok**
- Réduit drastiquement le code inutile Java
- Génération automatique de getters, setters, constructeurs, `equals()`, `hashCode()`, `toString()`
- Améliore la lisibilité du code en se concentrant sur la logique métier
- Annotations comme `@Data`, `@Builder`, `@NoArgsConstructor` simplifient le développement

**BCrypt (at.favre.lib:bcrypt:0.10.2)**
- Algorithme de hachage de mots de passe sécurisé
- Résistant aux attaques par force brute grâce au salage automatique
- Standard de l'industrie pour le stockage sécurisé des mots de passe
- Implémentation Java moderne et performante
- Garantit la sécurité des données utilisateurs

### Outils de build

**Gradle**
- Système de build moderne et flexible
- Gestion automatique des dépendances
- Builds incrémentaux pour des compilations plus rapides
- Intégration native avec Spring Boot