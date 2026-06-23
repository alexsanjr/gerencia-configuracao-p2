# syntax=docker/dockerfile:1

# Compila a aplicacao com a mesma versao de Java definida no pom.xml.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src/ src/
# application-test.properties veio do projeto original em ISO-8859-1.
RUN ./mvnw -B clean package -DskipTests -Dproject.build.sourceEncoding=ISO-8859-1

# Imagem final minima, contendo somente o artefato executavel.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /workspace/target/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
