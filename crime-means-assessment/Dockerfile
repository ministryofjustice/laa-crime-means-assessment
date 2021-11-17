FROM openjdk:11-jdk
VOLUME /tmp
COPY ./build/libs/crime-means-assessment.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
