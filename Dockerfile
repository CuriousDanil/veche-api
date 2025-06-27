FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY docker-entrypoint.sh ./docker-entrypoint.sh
COPY build/libs/veche-api.jar app.jar

RUN chmod +x ./docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]