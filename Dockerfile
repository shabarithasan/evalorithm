FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY backend/target/evalorithm-backend-1.0.0.jar app.jar
COPY backend/serviceAccountKey.json serviceAccountKey.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
