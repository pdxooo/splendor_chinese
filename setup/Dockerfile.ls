FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /LS
COPY maven-settings.xml /root/.m2/settings.xml
COPY LobbyService /LS
COPY lobby-overrides/AccountBootstrap.java /LS/src/main/java/eu/kartoffelquadrat/ls/accountmanager/config/AccountBootstrap.java
COPY lobby-overrides/CreateGameForm.java /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/control/CreateGameForm.java
COPY lobby-overrides/SessionController.java /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/control/SessionController.java
COPY lobby-overrides/Session.java /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/model/Session.java
COPY lobby-overrides/LauncherInfo.java /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/model/LauncherInfo.java
COPY lobby-overrides/SessionControllerTest.java /LS/src/test/java/eu/kartoffelquadrat/ls/lobby/control/SessionControllerTest.java
RUN sed -i 's/new Session(creator, gameParameters, savegameid)/new Session(creator, gameParameters, savegameid, createGameForm.getTurnTimeSeconds())/' \
    /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/control/SessionController.java \
    && sed -i 's/new Session(creator, brandedParams, createGameForm.getSavegame())/new Session(creator, brandedParams, createGameForm.getSavegame(), createGameForm.getTurnTimeSeconds())/' \
    /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/control/SessionController.java \
    && sed -i 's/new LauncherInfo(gamename, players, session.getCreator(), session.getSavegameid())/new LauncherInfo(gamename, players, session.getCreator(), session.getSavegameid(), session.getTurnTimeSeconds())/' \
    /LS/src/main/java/eu/kartoffelquadrat/ls/lobby/control/SessionController.java
RUN mvn -f /LS/pom.xml clean package -P prod

FROM eclipse-temurin:17.0.5_8-jre
WORKDIR /LS
COPY --from=builder /LS/target/ls.jar /LS/ls.jar
EXPOSE 34172
CMD ["java", "-jar", "ls.jar", "--server.port=34172", "--api.games.url=/api/sessions/"]
