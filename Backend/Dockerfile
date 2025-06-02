# Build Stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Runtime Stage
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /build/target/E-Commerce-0.0.1-SNAPSHOT.jar /app/E-Commerce.jar

EXPOSE 8087

# Variables d'environnement pour la connexion MySQL conteneur
ENV DB_URL=jdbc:mysql://mysql:3306/ecommerce

CMD ["java", "-jar", "-Dspring.profiles.active=dev", "-Dspring.datasource.url=${DB_URL}", "E-Commerce.jar"]
