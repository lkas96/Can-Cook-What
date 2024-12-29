FROM eclipse-temurin:23-jdk AS compiler

ARG COMPILE_DIR=/code_folder

WORKDIR ${COMPILE_DIR}

COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY src src
COPY .mvn .mvn

RUN chmod a+x ./mvnw
RUN ./mvnw clean package -Dmaven.skip.tests=true

ENV SERVER_PORT=3232

EXPOSE ${SERVER_PORT}

# stage 2
FROM eclipse-temurin:23-jdk

ARG DEPLOY_DIR=/app

WORKDIR ${DEPLOY_DIR}

COPY --from=compiler /code_folder/target/can-cook-what-1.0-RELEASE.jar CanCookWhat.jar

ENV SERVER_PORT=3232

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "CanCookWhat.jar"]