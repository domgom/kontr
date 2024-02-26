FROM maven:3.9.6-eclipse-temurin-21 as build
COPY ./ /home/maven/src
WORKDIR /home/maven/src
RUN mvn package -Dgpg.skip=true

FROM eclipse-temurin:21
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/maven/src/kontr-web/target/*-with-dependencies.jar /app/kontr-web.jar
ENTRYPOINT ["java","-jar","/app/kontr-web.jar"]