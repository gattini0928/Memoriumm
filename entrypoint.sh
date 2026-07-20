#!/bin/sh

echo "================================="
echo "🚀 Iniciando Memorium API"
echo "☕ Java 21"
echo "🌱 Spring Boot"
echo "🐘 PostgreSQL"
echo "☁️ AWS S3"
echo "📖 Swagger"
echo "================================="

echo "PROFILE: $SPRING_PROFILES_ACTIVE"
echo "PORTA: $SERVER_PORT"

exec java \
-Dfile.encoding=UTF-8 \
-XX:+UseContainerSupport \
-jar /app/target/*.jar