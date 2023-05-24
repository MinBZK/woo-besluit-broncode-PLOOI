
#Running stage
FROM SSSSSSSSSSSSSSSSSS/koop-plooi-docker-hub/library/eclipse-temurin:17.0.2_8-jre-alpine
ARG JAR_NAME
COPY ./target/$JAR_NAME.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "/app.jar", "-Xmx5120m"]
