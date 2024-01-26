FROM maven:3.9-eclipse-temurin-21 AS builder

ARG APP_DIR=/src

WORKDIR ${APP_DIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

COPY .mvn .mvn
COPY src src

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk

ARG APP_DIR2=/JD
WORKDIR ${APP_DIR2}

ENV PORT=8080
ENV SPRING_DATA_MONGODB_URI= 
ENV SPRING_DATASOURCE_URL=

COPY --from=builder /src//target/assessment-0.0.1-SNAPSHOT.jar assessment.jar

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar assessment.jar