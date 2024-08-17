FROM openjdk:16-jdk AS build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew dependencies --no-daemon

FROM openjdk:16-jdk-slim

WORKDIR /app

COPY --from=build /app/gradlew /app/gradlew
COPY --from=build /app/gradle /app/gradle
COPY --from=build /app/settings.gradle /app/settings.gradle
COPY --from=build /app/build.gradle /app/build.gradle
COPY --from=build /app/src /app/src

RUN chmod +x gradlew

EXPOSE 8080

ENTRYPOINT ["./gradlew", "bootRun", "--no-daemon"]
