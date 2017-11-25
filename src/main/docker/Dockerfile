FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER yaromudr
VOLUME /build
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
RUN sh -c 'touch app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]