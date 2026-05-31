FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/user-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
