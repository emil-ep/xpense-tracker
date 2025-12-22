# Use a Maven image to build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Accept GitHub credentials as build args
ARG GITHUB_TOKEN
ARG GITHUB_ACTOR

# Configure Maven to authenticate GitHub Packages
RUN mkdir -p ~/.m2 \
 && echo "<settings><servers><server><id>github</id><username>${GITHUB_ACTOR}</username><password>${GITHUB_TOKEN}</password></server></servers></settings>" > ~/.m2/settings.xml

# Copy pom.xml and dependencies first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build
COPY src ./src
RUN mvn clean install -DskipTests

# Use a smaller image to run the application
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
