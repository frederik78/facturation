# ── Étape 1 : Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
# Télécharge les dépendances séparément → cache Docker réutilisé si pom.xml inchangé
RUN apk add --no-cache maven && mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B package -DskipTests

# ── Étape 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Utilisateur non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /app/data /app/certs && \
    chown -R appuser:appgroup /app

# Répertoire pour la base SQLite (monter un volume ici en production)
RUN mkdir -p /app/data /app/certs && \
    chown -R appuser:appgroup /app

COPY --from=build /app/target/facturation-1.0.0.jar app.jar
USER appuser

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
