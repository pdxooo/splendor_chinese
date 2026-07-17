FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /LS
COPY LobbyService /LS
COPY lobby-overrides/AccountBootstrap.java /LS/src/main/java/eu/kartoffelquadrat/ls/accountmanager/config/AccountBootstrap.java
RUN sed -i '/return Pattern\.compile.*matcher(password)\.find();/c\        return password != null \&\& !password.trim().isEmpty();' \
    /LS/src/main/java/eu/kartoffelquadrat/ls/accountmanager/controller/AccountForm.java \
    && grep -Fq 'return password != null && !password.trim().isEmpty();' \
    /LS/src/main/java/eu/kartoffelquadrat/ls/accountmanager/controller/AccountForm.java
RUN mvn -f /LS/pom.xml clean package -P prod

FROM eclipse-temurin:17.0.5_8-jre
WORKDIR /LS
COPY --from=builder /LS/target/ls.jar /LS/ls.jar
EXPOSE 34172
CMD ["java", "-jar", "ls.jar", "--server.port=34172", "--api.games.url=/api/sessions/"]
