
#Package stage
FROM SSSSSSSSSSSSSSSSSS/koop-plooi-docker-hub/library/eclipse-temurin:17.0.2_8-jre-alpine
ARG JAR_NAME
ENV DELIVER_URL="http://localhost:8181"

COPY ./target/$JAR_NAME.jar /app.jar
COPY ./src/main/resources/metadata /src/main/resources/metadata
COPY ./src/main/resources/waardelijsten /src/main/resources/waardelijsten

EXPOSE 8080
ENTRYPOINT ["java","-jar", "/app.jar"]
