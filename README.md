
# SAE-5.A.1-API

Ce projet contient tout le backend de la SAE.

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


## Authors

- [@Curiosow](https://www.github.com/Curiosow)
- [@8rubi](https://www.github.com/8rubi)
- [@MatisT05](https://www.github.com/MatisT05)
- [@NoaBaj](https://www.github.com/NoaBaj)
- [@TWP444](https://www.github.com/TWP444)
## Appendix

### - I - Auth System
Le système d'authentification utilise pleinement les technologies prévues par SpringBoot.
## Tech Stack

**Server:** Java

**Librairies** Spring Boot (JPA, Web, DevTools, Security), Lombok, Apache POI, HikariCP, BCrypt