FROM gradle:8.13.0-jdk21-alpine AS build

WORKDIR /app

COPY ./src ./src
COPY settings.gradle ./

RUN gradle :src:services:accounting:assemble --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/src/services/accounting/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]