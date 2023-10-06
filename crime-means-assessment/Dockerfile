#FROM amazoncorretto:17-alpine
FROM amazoncorretto:17
RUN mkdir -p /opt/laa-crime-means-assessment/
WORKDIR /opt/laa-crime-means-assessment/
COPY ./build/libs/crime-means-assessment.jar /opt/laa-crime-means-assessment/app.jar
#RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8080 8096 8081
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENTRYPOINT ["java","-jar","app.jar"]
