# facturation

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