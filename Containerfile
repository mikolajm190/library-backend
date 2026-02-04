FROM docker.io/library/maven:3.9.12-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -DskipTests dependecy:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

FROM docker.io/library/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN useradd --create-home --shell /usr/sbin/nologin appuser
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]