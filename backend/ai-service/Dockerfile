FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . /workspace
RUN mvn -pl ai-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/ai-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

