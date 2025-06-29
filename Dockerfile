FROM openjdk:17
#COPY build/libs/Test-JWT1-0.0.1-SNAPSHOT.jar /jwt.jar
ENTRYPOINT ["java", "-jar", "/jwt.jar"]
