FROM eclipse-temurin:23-jre-alpine

WORKDIR /opt

EXPOSE 8761

# Ensure the correct file name is copied (your file name is `discovery-server-0.0.1-SNAPSHOT.jar`)
COPY target/discovery-server-0.0.1-SNAPSHOT.jar /opt/discovery-server.jar

# Update the entry point to use the correct jar file name
ENTRYPOINT ["java", "-jar", "discovery-server.jar"]
