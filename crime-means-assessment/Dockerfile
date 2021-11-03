FROM openjdk:11-jdk
VOLUME /tmp
COPY ./build/libs/crime-means-assessment.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]
