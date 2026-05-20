# facturation

Application de facturation minimaliste en Java (Spring Boot). Permet de gérer un profil émetteur, des clients et des factures.

L'interface utilisateur est construite avec **Thymeleaf** et Bootstrap. Ce choix technologique permet de garder l'application simple, légère et facile à maintenir en évitant la complexité d'un framework front-end moderne (SPA), tout en offrant une expérience fluide.

## Principales fonctionnalités

- Gestion du profil émetteur (settings)
- Création et gestion des clients
- Création, édition et export basique des factures
- Persistance dans une base SQLite embarquée

## Prérequis

- Java 25 (selon la version définie dans `pom.xml`)
- Maven
- Docker (optionnel)

## Installation et exécution

1.  **Construire le projet :**

    ```bash
    mvn clean package
    ```
2.  **Lancer l'application :**

    ```bash
    java -jar target/*.jar
    ```
    Pour un lancement en mode développement (avec Spring Boot DevTools) :
    ```bash
    mvn spring-boot:run
    ```

## Configuration

-   Fichier de configuration principal : `src/main/resources/application.properties`
-   Base de données SQLite par défaut : `./data/facturation.db`
-   Le schéma SQL est appliqué automatiquement depuis `src/main/resources/schema.sql` au démarrage de l'application.

## Sécurité et Accès

L'application utilise Spring Security pour protéger l'accès aux données.

**Identifiants par défaut :**
- **Utilisateur :** `admin`
- **Mot de passe :** `admin`

Les mots de passe dans la base de données (table `app_user`) doivent être hachés. Pour changer un mot de passe, vous pouvez mettre à jour la colonne `password` en générant un nouveau hash avec la commande suivante :

```bash
java -jar target/facturation-1.0.0.jar \
  --spring.profiles.active=encode-password \
  --password=monmotdepasse
```

Remplacez `monmotdepasse` par le texte souhaité pour obtenir le hash `{bcrypt}...` à insérer en base.

## Utilisation via Docker

1.  **Construire l'image Docker :**
    ```bash
    docker build -t facturation .
    ```
2.  **Lancer le conteneur :**
    ```bash
    docker run -p 8080:8080 -v $(pwd)/data:/app/data facturation
    ```
    (Le port 8080 est exposé par défaut. Le volume `$(pwd)/data` est monté pour persister la base de données SQLite.)

## Remarques importantes

-   Aucun profil émetteur par défaut n'est inséré automatiquement. Configurez votre profil via `/settings` après le premier démarrage de l'application.
-   Les placeholders éventuels dans la base de données (ex: `[COMPANY_NAME]`) proviennent de données initiales présentes dans `./data/facturation.db` et doivent être supprimés ou remplacés via la page `/settings`.

## Contribuer

Forkez le dépôt, créez une branche `feature/bugfix`, puis ouvrez une pull request. Respectez les conventions du projet et ajoutez des tests si pertinent.

## Licence

Voir le fichier `LICENSE` si présent à la racine du dépôt.

## Contact

Ouvrez une issue sur ce dépôt pour signaler un bug, proposer une amélioration ou poser une question.