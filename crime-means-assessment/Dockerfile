FROM openjdk:18.0-jdk
RUN mkdir -p /opt/laa-crime-means-assessment/
WORKDIR /opt/laa-crime-means-assessment/
COPY ./build/libs/crime-means-assessment.jar /opt/laa-crime-means-assessment/app.jar
RUN useradd -r -u 1001 appuser
USER 1001
EXPOSE 8080 8096
ENTRYPOINT ["java","-jar","app.jar"]
