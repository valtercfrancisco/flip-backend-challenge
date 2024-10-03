FROM gradle:7.6-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .

RUN gradle build --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]