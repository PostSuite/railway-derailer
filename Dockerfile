# Use the official JDK 19 image as the base image for the build stage
FROM openjdk:19-jdk-slim AS build

# Install xargs and other necessary utilities
RUN apt-get update && apt-get install -y findutils bash

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and the gradle-wrapper files
COPY gradlew .
COPY gradle/wrapper/ gradle/wrapper/

# Ensure the gradlew script is executable
RUN chmod +x gradlew

# Copy the build.gradle file
COPY build.gradle .

# Copy the source code
COPY ./src ./src

# Compile and package the application
RUN ./gradlew build

# Use the official JDK 19 image as the base image for the runtime stage
FROM openjdk:19-jdk-slim AS runtime

# Enable preview features
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager --enable-preview"

# Set the working directory
WORKDIR /app

# Copy the build artifacts from the build stage
COPY --from=build /app/build/quarkus-app/lib/ /app/lib/
COPY --from=build /app/build/quarkus-app/*.jar /app/
COPY --from=build /app/build/quarkus-app/app/ /app/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /app/quarkus/

# Set the entrypoint and command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/quarkus-run.jar"]