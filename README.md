# facturation

Description

Application de facturation minimaliste en Java (Spring Boot). Permet de gérer un profil émetteur, des clients et des factures. Fournit une interface web avec Bootstrap et persistance SQLite.

Principales fonctionnalités

- Gestion du profil émetteur (settings)
- Création et gestion des clients
- Création, édition et export basique des factures
- Persisté dans une base SQLite embarquée

Prérequis

- Java 11+ (ou version définie dans pom.xml)
- Maven
- Docker (optionnel)

Installation et exécution

1. Construire le projet :

   mvn clean package

2. Lancer l'application :

   java -jar target/*.jar

Lancement en développement (Spring Boot) :

   mvn spring-boot:run

Configuration

- Fichier de configuration : src/main/resources/application.properties
- Base SQLite par défaut : ./data/facturation.db
- Le schéma SQL est appliqué depuis src/main/resources/schema.sql au démarrage

Docker

1. Construire l'image :

   docker build -t facturation .

2. Lancer le conteneur (expose le port 8080) :

   docker run -p 8080:8080 -v $(pwd)/data:/app/data facturation

Remarques

- Aucun profil émetteur par défaut n'est inséré automatiquement. Configurez votre profil via /settings après le premier démarrage.
- Les placeholders éventuels dans la base (ex: [COMPANY_NAME]) proviennent de données présentes dans ./data/facturation.db et doivent être supprimés ou remplacés via /settings.

Contribuer

Forkez, créez une branche feature/bugfix, puis ouvrez une pull request. Respectez les conventions du projet et ajoutez des tests si pertinent.

Licence

Voir le fichier LICENSE si présent.

Contact

Ouvrez une issue pour signaler un bug ou proposer une amélioration.


Description

Application de facturation développée en Java. Ce dépôt contient le code source, la configuration Maven et un Dockerfile pour construire et exécuter l'application.

Prérequis

- Java 11+ (ou la version requise dans le pom.xml)
- Maven
- (Optionnel) Docker

Installation et exécution

1. Construire avec Maven :

   mvn clean package

2. Lancer le jar généré :

   java -jar target/*.jar

Ou utiliser Maven (si l'application le supporte) :

   mvn spring-boot:run

Utilisation via Docker

1. Construire l'image :

   docker build -t facturation .

2. Lancer le conteneur :

   docker run -p 8080:8080 facturation

(Adaptez le port si l'application écoute sur un autre port.)

Structure du projet

- src/ : code source Java
- data/ : jeux de données et fichiers liés
- Dockerfile : instructions de build Docker
- pom.xml : configuration Maven

Contribuer

Forkez le dépôt, créez une branche pour votre fonctionnalité ou correctif, puis ouvrez une pull request. Respectez les conventions de code et les tests existants.

Licence

Voir le fichier LICENSE si présent dans le dépôt.

Contact

Pour toute question, ouvrir une issue dans ce dépôt.