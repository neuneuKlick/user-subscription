
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/user-subscription-0.0.1-SNAPSHOT.jar user-subscription.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "user-subscription.jar"]
