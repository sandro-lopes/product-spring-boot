FROM openjdk:8-jdk-alpine

LABEL maintainer="sandrolopes6@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/app.jar"]
