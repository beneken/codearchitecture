FROM gradle:8.13.0-jdk21-alpine AS build

WORKDIR /app

COPY ./src ./src
COPY settings.gradle ./

RUN gradle :src:services:scanner:assemble --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/src/services/scanner/build/libs/*.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]