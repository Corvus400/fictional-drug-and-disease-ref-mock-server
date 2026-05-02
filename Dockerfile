FROM eclipse-temurin:25.0.3_9-jre
WORKDIR /app
COPY build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
