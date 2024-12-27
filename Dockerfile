# Use the official Eclipse Temurin image for Java 21
FROM eclipse-temurin:21-jre as base

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
# Assuming the JAR file is built in the `target` directory with the name `app.jar`
COPY target/xpense-tracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Define environment variables for PostgreSQL configuration
# These can be overridden during deployment
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-ctn3u6a3esus739turn0-a.singapore-postgres.render.com:5432/xpense_tracker
ENV SPRING_DATASOURCE_USERNAME=xpense_tracker_user
ENV SPRING_DATASOURCE_PASSWORD=sNc0EtRaXVGBykLwtjxMtAitaU4wz6BY

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
