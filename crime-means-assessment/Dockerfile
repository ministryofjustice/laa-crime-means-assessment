FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-crime-means-assessment/
WORKDIR /opt/laa-crime-means-assessment/
COPY ./build/libs/crime-means-assessment.jar /opt/laa-crime-means-assessment/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8085 8096
CMD java -jar app.jar
