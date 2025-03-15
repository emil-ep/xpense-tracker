# Use a Maven image to build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
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
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-cv7dm0tumphs738h3tmg-a.singapore-postgres.render.com:5432/xpense_tracker_gwtd
ENV SPRING_DATASOURCE_USERNAME=xpense_admin
ENV SPRING_DATASOURCE_PASSWORD=eFflAfi2pd8oupIooXvGRoJTGqanCxhP
ENTRYPOINT ["java", "-jar", "/app.jar"]

