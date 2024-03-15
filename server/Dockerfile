FROM amazoncorretto:11
#ENV TZ="Europe/Moscow"
COPY target/*.jar shareit-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "shareit-server-0.0.1-SNAPSHOT.jar"]