#!/bin/bash
docker compose down
pkill -9 -f spring-boot
pkill -9 -f "java -jar"
docker compose up -d postgres
echo "Waiting for postgres to start..."
sleep 10
./mvnw spring-boot:run -DskipTests -Dcheckstyle.skip=true -Dspring.datasource.url="jdbc:postgresql://localhost:5432/mydatabase" -Dspring.datasource.username=myuser -Dspring.datasource.password=my-safe-password-2026 -Dspring.docker.compose.enabled=false > /tmp/app.log 2>&1 &
echo "Started app in background."
sleep 30
curl -s http://localhost:8080/api/courses | head -c 200
