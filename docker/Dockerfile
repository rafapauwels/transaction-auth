FROM openjdk:8-jre-alpine
ENV PORT 443
EXPOSE 443
COPY transaction-authorization.jar /transaction-auth.jar
CMD ["/usr/bin/java", "-jar", "/transaction-auth.jar"]
