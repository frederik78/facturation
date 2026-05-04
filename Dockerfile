# ── Étape 1 : Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn -B package -DskipTests

# ── Étape 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Répertoire pour la base SQLite (monter un volume ici en production)
RUN mkdir -p /app/data

COPY --from=build /app/target/facturation-1.0.0.jar app.jar

EXPOSE 8443 8080

# Variable d'environnement pour surcharger le chemin DB en cloud
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/facturation.db

# Variables TLS — à surcharger avec de vrais secrets en production :
#   -e SSL_KEY_STORE=/run/secrets/keystore.p12
#   -e SSL_KEY_STORE_PASSWORD=...
#   -e APP_USERNAME=admin
#   -e APP_PASSWORD={bcrypt}$2a$10$...
ENV SSL_KEY_STORE=classpath:keystore-dev.p12
ENV SSL_KEY_STORE_PASSWORD=changeit
ENV SSL_KEY_ALIAS=facturation

ENTRYPOINT ["java", "-jar", "app.jar"]
