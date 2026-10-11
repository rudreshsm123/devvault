FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:resolve
COPY src ./src
RUN mvn -q -B package -DskipTests

# The runtime image has no compiler, so the opt-in Java judge (JUDGE_JAVA_ENABLED=true) needs 21-jdk instead.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/devvault-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=cloud
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]
