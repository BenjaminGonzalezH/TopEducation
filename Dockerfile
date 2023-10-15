FROM openjdk:20
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} TopEducation.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/TopEducation.jar"]