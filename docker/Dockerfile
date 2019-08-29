FROM openjdk:8-jre-alpine
ENV APP_PORT 80
EXPOSE 80
COPY transaction-authorization-1.0.0-SNAPSHOT-standalone.jar /opt/apl/transaction-authorization.jar
CMD ["/usr/bin/java", "-jar", "/opt/apl/transaction-authorization.jar"]