FROM eclipse-temurin:21.0.10_7-jre
WORKDIR /app
COPY build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
