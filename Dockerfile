FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY build/libs/botics-pre-demo.jar botics-pre-demo.jar
ENTRYPOINT ["java","-jar","/botics-pre-demo.jar"]