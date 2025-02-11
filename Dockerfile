FROM eclipse-temurin:21
MAINTAINER neel
COPY target/website-login-1-0.0.1-SNAPSHOT.jar website-login-1.0.0.jar
ENTRYPOINT ["java","-jar","/website-login-1.0.0.jar"]