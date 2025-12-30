# Stage 1: Build the application
FROM maven:3.8.4-openjdk-8-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM tomcat:9.0-jdk8-openjdk-slim
WORKDIR /usr/local/tomcat/webapps/

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the build stage
# We rename it to ROOT.war so the app is served at the root URL (/)
COPY --from=build /app/target/QuizWebApp.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
