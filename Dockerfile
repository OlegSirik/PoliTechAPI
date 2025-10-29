# syntax=docker/dockerfile:1

FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY build.gradle gradlew gradlew.bat ./
COPY gradle/ ./gradle/
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]



