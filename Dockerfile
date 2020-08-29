FROM openjdk:11

ARG JAR_FILE=target/docker-file-server.jar
ARG JAR_LIB_FILE_DIR=target/libs/

WORKDIR /usr/local/pavlenko

COPY ${JAR_FILE} docker-file-server.jar
ADD ${JAR_LIB_FILE_DIR} libs/

ENTRYPOINT ["java","-jar","docker-file-server.jar"]