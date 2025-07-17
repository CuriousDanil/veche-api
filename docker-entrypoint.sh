#!/bin/sh
export SPRING_DATASOURCE_PASSWORD=$(cat /run/secrets/postgres_password)
export JWT_SECRET=$(cat /run/secrets/jwt_secret)
exec java -jar /app/app.jar