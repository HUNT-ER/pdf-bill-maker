FROM eclipse-temurin:17-jre-alpine
ARG JAR_PATH=target/*.jar
COPY ${JAR_PATH} /app/app.jar
EXPOSE 80
CMD ["java", "-jar", "/app/app.jar"]