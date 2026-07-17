FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /LS
COPY LobbyService /LS
COPY lobby-overrides/simple-password.patch /tmp/simple-password.patch
RUN patch -d /LS -p1 < /tmp/simple-password.patch
RUN mvn -f /LS/pom.xml clean package -P prod

FROM eclipse-temurin:17.0.5_8-jre
WORKDIR /LS
COPY --from=builder /LS/target/ls.jar /LS/ls.jar
EXPOSE 34172
CMD ["java", "-jar", "ls.jar", "--server.port=34172", "--api.games.url=/api/sessions/"]
