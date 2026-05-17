# ─────────────────────────────────────────────────────────
# Stage 1: Build – Maven + JDK 17
# ─────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the POM first so Maven dependency download is cached
# as a separate Docker layer (only re-runs when pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the JAR, skipping tests
# (tests should be run separately via mvn test / CI)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────────
# Stage 2: Runtime – slim JRE 17
# ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Spring Boot embeds Tomcat on port 8080
EXPOSE 8080

# Use the Spring Boot launcher class directly to avoid failures when the
# repackaged jar manifest is missing or altered. This runs the embedded
# Spring Boot loader from inside the fat jar.
ENTRYPOINT ["java", "-cp", "app.jar", "org.springframework.boot.loader.JarLauncher"]
