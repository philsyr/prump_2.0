FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean verify

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/algo_train.jar app.jar
USER 10001:10001
ENTRYPOINT ["java", "-jar", "app.jar"]
