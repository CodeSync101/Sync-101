FROM eclipse-temurin:23-jre-alpine

# Set working directory
WORKDIR /opt

# Set environment variables
ENV PORT=8083
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expose the application port
EXPOSE 8082

# Copy the jar file into the container
COPY target/*.jar /opt/app.jar

# Use the shell form to allow environment variable expansion
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
