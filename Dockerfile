FROM openjdk:11
MAINTAINER vermar0187
VOLUME /tmp
EXPOSE 8080
ADD target/silverwing-1.0-SNAPSHOT.jar silverwing-1.0-SNAPSHOT.jar
ADD assets assets
ADD patch patch
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/silverwing-1.0-SNAPSHOT.jar"]