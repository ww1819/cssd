FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY cssd-common/pom.xml cssd-common/pom.xml
COPY cssd-app/pom.xml cssd-app/pom.xml
RUN mvn -q -DskipTests dependency:go-offline || true
COPY cssd-common cssd-common
COPY cssd-app cssd-app
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/cssd-app/target/cssd-app-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
