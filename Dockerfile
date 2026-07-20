FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

RUN useradd -m appuser
USER appuser

EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]
CMD [ "--spring.profiles.active=prod", "--server.port=8080"]
