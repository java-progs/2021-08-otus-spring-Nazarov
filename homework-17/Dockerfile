FROM maven:3.8.5-openjdk-11-slim as build
ENV PROJECT_DIR=/tmp/docker_build
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
ADD ./pom.xml $PROJECT_DIR
RUN mvn dependency:resolve
ADD ./src/ $PROJECT_DIR/src
RUN mvn install

FROM openjdk:11-jre-slim
ENV PROJECT_DIR_APP=/app
RUN mkdir -p PROJECT_DIR_RUN
WORKDIR $PROJECT_DIR_APP
COPY --from=build /tmp/docker_build/target/homework-17.jar $PROJECT_DIR_APP/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/homework-17.jar"]