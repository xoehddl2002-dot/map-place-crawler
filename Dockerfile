FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# ./gradlew bootJar -Pprofile=<env> 결과물
COPY build/libs/map-place-crawler.jar app.jar

EXPOSE 6085

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]
