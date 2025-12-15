# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven wrapper en pom kopiëren
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Maven wrapper uitvoerbaar maken (belangrijk op Linux)
RUN chmod +x mvnw

# Dependencies alvast downloaden
RUN ./mvnw -q dependency:go-offline

# Source code kopiëren en jar bouwen
COPY src src
RUN ./mvnw -q package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Gebouwde jar uit de build-stage kopiëren (LET OP: /app/target)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
