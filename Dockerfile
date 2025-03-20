FROM openjdk:17
VOLUME /logs
COPY build/libs/right-backend-0.0.1-SNAPSHOT.jar Right.jar
ENTRYPOINT ["java", "-jar", "Right.jar"]